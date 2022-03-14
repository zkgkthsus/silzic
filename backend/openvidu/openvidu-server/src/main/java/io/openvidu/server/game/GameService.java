/*
 * (C) Copyright 2017-2020 OpenVidu (https://openvidu.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.openvidu.server.game;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.openvidu.client.internal.ProtocolElements;
import io.openvidu.server.core.Participant;
import io.openvidu.server.rpc.RpcNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameService {

    static final int GETJOBSSETTING = 0;
    static final int SETJOBSSETTING = 1;
    static final int GETREADYSETTING = 2;
    static final int SETREADYSETTING = 3;
    static final int GAMESTART = 4;
    static final int USESKILL = 5;
    static final int CHECKPARTICIPANTS = 7;
    static final int SECRETMESSAGE = 10;


    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    static RpcNotificationService rpcNotificationService;

    /**
     * 게임 정보 관리.
     */
    //Tread 관리
    public static ConcurrentHashMap<String, Thread> gameThread = new ConcurrentHashMap<>();
    // 역할 관리 <sessionId, <Participant, Roles>>
    public static ConcurrentHashMap<String, ArrayList<Roles>> gameRoles = new ConcurrentHashMap<>();
    // 역할 - player 매칭 정보 관리
    public static ConcurrentHashMap<String, ArrayList<Characters>> roleMatching = new ConcurrentHashMap<>();
    // 종료시에 보낼 정보
    public static ConcurrentHashMap<String, ArrayList<Characters>> roleInfo = new ConcurrentHashMap<>();
    // 참가자 목록 관리
    public static ConcurrentHashMap<String, ArrayList<Participant>> participantsList = new ConcurrentHashMap<>();
    // 살아있는 경찰 수 관리
    public static ConcurrentHashMap<String, Integer> alivePolices = new ConcurrentHashMap<>();
    // 경찰총장, 노트주인 따로 관리
    public static ConcurrentHashMap<String, ArrayList<Participant>> kiraAndL = new ConcurrentHashMap<>();
    // 데스노트 적힌사람.
    public static ConcurrentHashMap<String, ArrayList<Characters>> deathNoteList = new ConcurrentHashMap<>();
    // Ready현황 관리
    public static ConcurrentHashMap<String, HashMap<String, Boolean>> readySetting = new ConcurrentHashMap<>();

    public void gameNavigator(Participant participant, JsonObject message, Set<Participant> participants,
                              String sessionId, RpcNotificationService notice) {

        rpcNotificationService = notice;
        JsonObject params = new JsonObject();
        // data 파싱해서 다시 JSONOBJECT로 바꾸기.
        String dataString = message.get("data").toString();
        JsonObject data = (JsonObject) JsonParser.parseString(dataString);

        // data에 gameStatus로 게임 상태 분기
        int gameStatus = data.get("gameStatus").getAsInt();

        //타입은 game+gameStatus로 보내준다. 예시 : game2 or game3
        String type = message.get("type").getAsString();
        params.addProperty(ProtocolElements.PARTICIPANTSENDMESSAGE_TYPE_PARAM, type);

        switch (gameStatus) {
            case GETJOBSSETTING: // 사전 게임 정보값 얻기. 0번
                getJobsSetting(participant, sessionId, participants, params, data, notice);
                return;
            case SETJOBSSETTING: // 사전 게임 정보값 세팅하기. 1번
                setJobsSetting(participant, sessionId, participants, params, data, notice);
                return;
            case GETREADYSETTING: // 사전 레디정보 얻기 2번
                getReadySetting(participant, sessionId, participants, params, data, notice);
                return;
            case SETREADYSETTING: // 사전 레디정보 세팅 3번
                setReadySetting(participant, sessionId, participants, params, data, notice);
                return;
            case GAMESTART: // 게임 시작
                gameStart(participant, message, sessionId, participants, params, data, notice);
                return;
            case USESKILL: // 스킬 사용
                useSkill(participant, sessionId, participants, params, data, notice);
                return;
            case CHECKPARTICIPANTS:
                checkParticipants(participant, message, sessionId, participants, params, data, notice);
                return;
            case SECRETMESSAGE:
                sendSecretMessgae(participant, message, sessionId, participants, params, data, notice);
                return;
        }
    }


    /**
     * 받는 signal
     * type : 'game';
     * data :
     * gameStatus : 0,
     * 0 : {
     * jobName : 직업이름,
     * count : 직업 수
     * },
     * 1 : {
     * ...
     * }
     * <p>
     * 역할 데이터 가져오기
     */
    private void getJobsSetting(Participant participant, String sessionId, Set<Participant> participants, JsonObject params, JsonObject data, RpcNotificationService notice) {
        //살아있는 경찰 초기값이 없으면 넣어주기. 있으면 변동 X
        alivePolices.putIfAbsent(sessionId, 1);
        //게임 롤 아무것도 없으면 일단 빈 배열 넣어준다.
        gameRoles.putIfAbsent(sessionId, new ArrayList<>());

        ArrayList<Roles> players = gameRoles.get(sessionId);

        //세션 역할이 비어있으면 새롭게 만듬. 초기값을 담는다.
        if (players.isEmpty()) {
            //KIRA
            players.add(new Roles("KIRA", false, 1, 1));
            //경찰 총장
            players.add(new Roles("L", false, 1, 1));
            //CRIMINAL
            players.add(new Roles("CRIMINAL", false, 1, 3));
            //POLICE
            players.add(new Roles("POLICE", false, 1, 3));
            //BROADCASTER
            players.add(new Roles("GUARD", false, 1, 3));
            //GUARD
            players.add(new Roles("BROADCASTER", false, 1, 3));
            //세션별로 관리.
            gameRoles.compute(sessionId, (k, v) -> v = players);
        }

        //직업이름이랑, 숫자 data에 담기
        setJobsProperty(params, data, players);

        //요청한 사람만 신호 보내주기.
        rpcNotificationService.sendNotification(participant.getParticipantPrivateId(),
                ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
    }

    /**
     * 보내는 signal
     * type : 'game';
     * data :
     * {
     * gameStatus : 1,
     * jobName : 이름
     * count : 숫자
     * }
     * 역할 데이터 세팅하기
     */
    private void setJobsSetting(Participant participant, String sessionId,
                                Set<Participant> participants, JsonObject params, JsonObject data, RpcNotificationService notice) {
        log.info("PrepareGame is called by {}", participant.getParticipantPublicId());

        //세션에서 자원 가져오기(초기값은 getPre할때 이미 설정됨)
        ArrayList<Roles> players = gameRoles.get(sessionId);

        String jobName = data.get("jobName").getAsString();
        Integer count = data.get("count").getAsInt();

        //바꾸는 jobName 찾기
        for (Roles r : players) {
            //maxCount 초과 안했으면 바꾸기.
            if (r.getJobName().equals(jobName) && count <= r.getMaxCount()) {
                //경찰이면 살아있는 경찰 수 바꿔주기.
                if (r.getJobName().equals("POLICE")) {
                    alivePolices.compute(sessionId, (k, v) -> v = count);
                }
                //역할 수 바꾸기.
                r.setCount(count);
            }
        }


        //바뀐 역할 정보를 갱신.
        gameRoles.compute(sessionId, (k, v) -> v = players);

        setJobsProperty(params, data, players);

        //방 참여자들에게 바뀐 데이터 보내주기.
        for (Participant p : participants) {
            rpcNotificationService.sendNotification(p.getParticipantPrivateId(),
                    ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
        }
    }

    private void setJobsProperty(JsonObject params, JsonObject data, ArrayList<Roles> players) {
        for (int i = 0; i < players.size(); i++) {
            JsonObject temp = new JsonObject();
            temp.addProperty("jobName", players.get(i).getJobName());
            temp.addProperty("count", players.get(i).getCount());
            data.add(Integer.toString(i), temp);
        }
        params.add("data", data);
    }

    /**
     * 처음 방 접속시 접속인원들의 Ready상태를 알려줌.
     */
    private void getReadySetting(Participant participant, String sessionId, Set<Participant> participants, JsonObject params, JsonObject data, RpcNotificationService notice) {
        //session에서 관리되는게 없으면 빈 배열 삽입
        readySetting.putIfAbsent(sessionId, new HashMap<>());

        //기존에 관리되고 있다면 세션별 관리값을 불러온다.
        HashMap<String, Boolean> preReadyState = readySetting.get(sessionId);
        HashMap<String, Boolean> readyState = new HashMap<>();

        //현재 방에 없는 애들 다 제외 시키기
        for (Participant p : participants) {
            String id = p.getParticipantPublicId();
            readyState.put(id, preReadyState.getOrDefault(id, false));
        }

        //새로운 participant, false 기본값 넣고 바꿔준다.
        readySetting.computeIfPresent(sessionId, (k, v) -> v = readyState);


        // publicId : 레디상태(false/true) 로 보냄.
        for (String publicId : readyState.keySet()) {
            data.addProperty(publicId, readyState.get(publicId));
        }
        params.add("data", data);

        //신호 요청자에게 바뀐 데이터 보내주기.
        rpcNotificationService.sendNotification(participant.getParticipantPrivateId(),
                ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
    }

    private void setReadySetting(Participant participant, String sessionId, Set<Participant> participants, JsonObject params, JsonObject data, RpcNotificationService notice) {
        //레디 상태 가져오기.
        HashMap<String, Boolean> readyState = readySetting.get(sessionId);
        //레디 값 토글
        readyState.compute(participant.getParticipantPublicId(), (k, v) -> v = !v);

        //레디값 변경.
        readySetting.computeIfPresent(sessionId, (k, v) -> v = readyState);

        int cnt = 0;
        // publicId : true로 보냄.
        for (String publicId : readyState.keySet()) {
            data.addProperty(publicId, readyState.get(publicId));
            if (readyState.get(publicId)) {
                cnt++;
            }
        }

        if (participants.size() >= 4 && participants.size() == cnt) {
            data.addProperty("readyStatus", true);
        }

        params.add("data", data);

        //방 참여자들에게 바뀐 데이터 보내주기.
        for (Participant p : participants) {
            rpcNotificationService.sendNotification(p.getParticipantPrivateId(),
                    ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
        }
    }

    /**
     * 게임 시작 가능 여부 확인은 Front에서!
     * type : 'game';
     * data :
     * {
     * gameStatus : 1,
     * }
     * 게임 시작 메소드
     */
    private void gameStart(Participant participant, JsonObject message, String sessionId, Set<Participant> participants,
                           JsonObject params, JsonObject data, RpcNotificationService notice) {

        deathNoteList.putIfAbsent(sessionId, new ArrayList<Characters>());

        //참여자 목록 넣기
        ArrayList<Participant> gameParticipants = new ArrayList<>(participants);
        participantsList.putIfAbsent(sessionId, gameParticipants);

        //참가자 목록 가져와서 shuffle
        ArrayList<Participant> players = new ArrayList<>(participants);
        Collections.shuffle(players);

        //역할 매칭 준비
        ArrayList<Characters> userRoles = new ArrayList<>();

        //역할 준비
        ArrayList<Roles> roles = gameRoles.get(sessionId);

        //역할 분배
        int cnt = 0;
        for (Roles r : roles) {
            for (int i = 0; i < r.getCount(); i++) {
                userRoles.add(new Characters(r.getJobName(), players.get(cnt++)));
            }
        }
        //역할 분배된 것 넣기.
        roleMatching.putIfAbsent(sessionId, userRoles);

        ArrayList<Characters> userInfo = new ArrayList<>(userRoles);
        roleInfo.putIfAbsent(sessionId, userInfo);

        //0,1번이 KIRA,L임
        ArrayList<Participant> KIRAandL = new ArrayList<>(players.subList(0, 2));

        //중요 역할들 목록에 담기
        kiraAndL.putIfAbsent(sessionId, KIRAandL);

        //각자에게 역할 알려주기.
        for (int i = 0; i < userRoles.size(); i++) {
            data.addProperty("jobName", userRoles.get(i).getJobName());
            params.add("data", data);
            rpcNotificationService.sendNotification(userRoles.get(i).getParticipant().getParticipantPrivateId(),
                    ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
        }

        //쓰래드 생성 및 등록.
        GameRunnable gameRunnable = new GameRunnable(sessionId, participantsList.get(sessionId), notice);
        Thread deathNoteThread = new Thread(gameRunnable);

        //스래드 시작.(명교, 미션 쓰레드 두개 다 시작)
        deathNoteThread.start();

        //게임 끝날때 삭제 했기 때문에 ifAbsent씀.
        gameThread.putIfAbsent(sessionId, deathNoteThread);

    }


    /**
     * 데이터 전송 예시
     * type : 'game';
     * data :
     * {
     * gameStatus : 5,
     * skillType : kill / protect / announce / noteWrite / noteUse / announceToL
     * result : true / false
     * target : connectionId
     * (kill, note에만 필요) jobName : 'L', 'KIRA', 'GUARD', 'BROADCASTER', 'CRIMINAL', 'POLICE' 중 하나.
     * (announce에만 필요) announce : "으아아아아 테스트!!"
     * }
     */
    //스킬 사용 메소드
    private void useSkill(Participant participant, String sessionId, Set<Participant> participants,
                          JsonObject params, JsonObject data, RpcNotificationService notice) {


        //사용하는 스킬 타입 구별
        String skillType = data.get("skillType").getAsString();
        //역할 리스트 가져오기.
        ArrayList<Characters> cList = roleMatching.get(sessionId);

        String jobName = null;

        //중요인물 리스트의 0번 = 키라, 1번 = 경찰총장
        Participant KIRA = kiraAndL.get(sessionId).get(0);
        //경찰총장
        Participant L = kiraAndL.get(sessionId).get(1);

        switch (skillType) {
            case "kill":
                //publicId로 타겟 찾아서 가져옴
                Characters target = getTarget(data, cList);

                jobName = data.get("jobName").getAsString();

                //skill대상의 직업이 jobName과 일치하는지 체크
                if (target.getJobName().equals(jobName)) {

                    //보호되는 상태가 아니면
                    if (!target.isProtected()) {

                        //사망처리.
                        target.setAlive(false);

                        //경찰일시 경찰 수 -1;
                        if (target.getJobName().equals("POLICE")) {
                            alivePolices.compute(sessionId, (k, v) -> v = v - 1);
                        }

                        //사망 소식 전하기
                        data.addProperty("isAlive", 2);
                        data.addProperty("userId", target.getParticipant().getClientMetadata());
                        data.addProperty("connectionId", target.getParticipant().getParticipantPublicId());
                        params.add("data", data);

                        for (Participant p : participants) {
                            rpcNotificationService.sendNotification(p.getParticipantPrivateId(),
                                    ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                        }

                        if (alivePolices.getOrDefault(sessionId, 0) < 1) {
                            finishGame(participant, sessionId, participants, params, data, "KIRA");
                        }


                        //보호 중이면.
                    } else {
                        //보호 풀기
                        target.setProtected(false);

                        //방어됨 소식 알리기.
                        data.addProperty("isAlive", 0);
                        data.addProperty("userId", target.getParticipant().getClientMetadata());
                        data.addProperty("connectionId", target.getParticipant().getParticipantPublicId());
                        params.add("data", data);

                        //스킬 쓴사람에게만 보호 소식 알리기.
                        rpcNotificationService.sendNotification(participant.getParticipantPrivateId(),
                                ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                    }
                } else {
                    //적은 사람이 일치하지 않는 소식 알리기.
                    data.addProperty("isAlive", 1);
                    data.addProperty("userId", target.getParticipant().getClientMetadata());
                    data.addProperty("connectionId", target.getParticipant().getParticipantPublicId());
                    params.add("data", data);

                    //스킬 쓴사람에게만 보호 소식 알리기.
                    rpcNotificationService.sendNotification(participant.getParticipantPrivateId(),
                            ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                }
                break;
            case "arrest":
                target = getTarget(data, cList);

                //KIRA 일때
                if (target.getJobName().equals("KIRA")) {
                    data.addProperty("isCriminal", true);
                    data.addProperty("userId", target.getParticipant().getClientMetadata());
                    data.addProperty("connectionId", target.getParticipant().getParticipantPublicId());
                    params.add("data", data);

                    //키라는 사망처리 없이 게임이 끝남.
                    finishGame(participant, sessionId, participants, params, data, "POLICE");

                    //CRIMINAL 일때
                } else if (target.getJobName().equals("CRIMINAL")) {
                    target.setAlive(false);
                    data.addProperty("isCriminal", true);
                    data.addProperty("userId", target.getParticipant().getClientMetadata());
                    data.addProperty("connectionId", target.getParticipant().getParticipantPublicId());
                    params.add("data", data);

                    //CRIMINAL 사망처리(수정값 적용은 case문 빠져 나간 뒤에)
                    for (Characters criminal : cList) {
                        if (criminal.getParticipant().getParticipantPublicId().equals(target.getParticipant().getParticipantPublicId())) {
                            criminal.setAlive(false);
                            break;
                        }
                    }

                    // L측 일때
                } else {
                    data.addProperty("isCriminal", false);
                    data.addProperty("userId", participant.getClientMetadata());
                    data.addProperty("connectionId", participant.getParticipantPublicId());
                    params.add("data", data);

                    //잘못된 사람을 체포했으므로 본인 사망처리.
                    for (Characters player : cList) {
                        if (player.getParticipant().getParticipantPublicId().equals(participant.getParticipantPublicId())) {
                            player.setAlive(false);
                            break;
                        }
                    }
                    alivePolices.compute(sessionId, (k, v) -> v - 1);

                    if (alivePolices.getOrDefault(sessionId, 0) < 1) {
                        finishGame(participant, sessionId, participants, params, data, "KIRA");
                    }
                }

                for (Participant p : participants) {
                    rpcNotificationService.sendNotification(p.getParticipantPrivateId(),
                            ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                }
                break;
            case "protect":
                target = getTarget(data, cList);
                //보호받는 사람 닉네임 같이 보내기
                data.addProperty("userId", target.getParticipant().getClientMetadata());
                //데이터 첨부
                params.add("data", data);
                //스킬 타겟 보호 설정
                target.setProtected(true);
                //가드한테 스킬 정상 작동 메세지 보내주기
                rpcNotificationService.sendNotification(participant.getParticipantPrivateId(),
                        ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                break;
            case "announce":

                params.add("data", data);

                for (Participant p : participants) {
                    rpcNotificationService.sendNotification(p.getParticipantPrivateId(),
                            ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                }
                break;
            case "noteWrite":
                target = getTarget(data, cList);

                jobName = data.get("jobName").getAsString();
                //노트 목록 불러오기
                ArrayList<Characters> noteList = deathNoteList.get(sessionId);

                //성공시 이름 적은 아이디, 실패시 실패 문구.
                if (target.getJobName().equals(jobName)) {
                    //노트에 사람 적기
                    noteList.add(target);
                    data.addProperty("writeName", true);
                    params.add("data", data);
                    //노트 목록 갱신
                    deathNoteList.compute(sessionId, (k, v) -> v = noteList);

                    for (Participant p : participants) {
                        rpcNotificationService.sendNotification(p.getParticipantPrivateId(),
                                ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                    }
                } else {
                    data.addProperty("writeName", false);
                    params.add("data", data);

                    rpcNotificationService.sendNotification(participant.getParticipantPrivateId(),
                            ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                }

                break;

            /**
             * 죽은사람 목록 모든 유저에게
             * data : {
             *  0:{  "isAlive" : false
             *       "userId" : userNickname
             *       "connectionId" : con5e786s
             *  },
             *  1:{  "isAlive" : false
             *       "userId" : userNickname
             *       "connectionId" : con5e786s
             *  },
             *  cnt : 2,
             *  SkillType : noteUse,
             *  GameStatue : 5
             * }
             *
             * 보호된 목록 키라한테만
             * data : {
             *  0:{  "isAlive" : true
             *       "userId" : userNickname
             *       "connectionId" : con5e786s
             *  },
             *  cnt : 1,
             *  SkillType : noteUse,
             *  GameStatue : 5
             * }
             */
            case "noteUse":
                noteList = deathNoteList.get(sessionId);
                JsonObject ForKira = data;

                int aliveCnt = 0;
                int protectedCnt = 0;

                // 노트에 적힌 사람들 죄다 죽이기.
                for (Characters c : noteList) {
                    JsonObject list = new JsonObject();

                    //보호되는 상태가 아니면
                    if (c.isAlive() && !c.isProtected()) {

                        //경찰일시 경찰 수 -1;
                        if (c.getJobName().equals("POLICE")) {
                            alivePolices.compute(sessionId, (k, v) -> v - 1);
                        }

                        //사망처리
                        for (Characters player : cList) {
                            if (player.getParticipant().getParticipantPublicId().equals(c.getParticipant().getParticipantPublicId())) {
                                player.setAlive(false);
                                break;
                            }
                        }

                        //사망 소식 전하기
                        list.addProperty("isAlive", false);
                        list.addProperty("userId", c.getParticipant().getClientMetadata());
                        list.addProperty("connectionId", c.getParticipant().getParticipantPublicId());
                        data.add(String.valueOf(aliveCnt), list);
                        aliveCnt++;

                        //보호 중이면.
                    } else {
                        //보호막 풀고
                        c.setProtected(false);
                        //방어됨 소식 알리기.
                        list.addProperty("isAlive", true);
                        list.addProperty("userId", c.getParticipant().getClientMetadata());
                        list.addProperty("connectionId", c.getParticipant().getParticipantPublicId());
                        ForKira.add(String.valueOf(protectedCnt), list);
                        protectedCnt++;
                    }
                }

                roleMatching.compute(sessionId, (k, v) -> v = cList);

                //죽은 사람 정보는 모든 유저에게 보낸다.
                data.addProperty("cnt", aliveCnt);
                params.add("data", data);
                for (Participant p : participants) {
                    rpcNotificationService.sendNotification(p.getParticipantPrivateId(),
                            ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                }

                //보호된 정보 키라한테만
                ForKira.addProperty("cnt", protectedCnt);
                params.add("data", ForKira);
                rpcNotificationService.sendNotification(participant.getParticipantPrivateId(),
                        ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);

                //사용후 데스노트 목록 비우기.
                deathNoteList.compute(sessionId, (k, v) -> v = new ArrayList<Characters>());

                //데스노트 사용후 경찰 수 체크
                if (alivePolices.getOrDefault(sessionId, 0) < 1) {
                    finishGame(participant, sessionId, participants, params, data, "KIRA");
                }

                break;

            case "announceToL":
                boolean result = data.get("result").getAsBoolean();
                String userId = participant.getClientMetadata();

                data.addProperty("result", result);
                data.addProperty("userId", userId);
                params.add("data", data);
                rpcNotificationService.sendNotification(L.getParticipantPrivateId(),
                        ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                break;
        }

    }

    private Characters getTarget(JsonObject data, ArrayList<Characters> cList) {

        String skillTarget = data.get("target").getAsString();

        Characters target = null;

        //connectionId로 Character 찾아옴.
        for (Characters c : cList) {
            if (c.getParticipant().getParticipantPublicId().equals(skillTarget)) {
                target = c;
                break;
            }
        }
        return target;
    }

    /**
     * gameStatus : 7,
     * 0: { userId : "userNickName",
     * connectionId : "conekt23lk",
     * },
     * cnt : 1,
     */
    private void checkParticipants(Participant participant, JsonObject message, String sessionId, Set<Participant> participants, JsonObject params, JsonObject data, RpcNotificationService notice) {
        int cnt = 0;

        System.out.println(participants.size());
        //참여자 정보 담기.
        for (Participant p : participants) {
            JsonObject player = new JsonObject();
            player.addProperty("userId", p.getClientMetadata());
            player.addProperty("connectionId", p.getParticipantPublicId());
            data.add(String.valueOf(cnt), player);
            cnt++;
        }
        data.addProperty("cnt", cnt);
        params.add("data", data);

        //요청자에게 정보 전달
        for (Participant p : participants) {
            rpcNotificationService.sendNotification(p.getParticipantPrivateId(),
                    ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
        }
    }

    /**
     * 게임 종료시 전달되는 데이터 예시
     * type : 'game';
     * data :
     * {
     * gameStatus : 8,
     * winner : KIRA / POLICE
     * }
     */
    //게임 종료 메소드
    private void finishGame(Participant participant, String sessionId, Set<Participant> participants, JsonObject params, JsonObject data, String winner) {

        log.info("finishGame is called by {}", participant.getParticipantPublicId());

        Thread deathNoteThread = gameThread.get(sessionId);

        ArrayList<Characters> userInfo = roleInfo.get(sessionId);

        for (Characters c : userInfo) {
            data.addProperty(c.getParticipant().getParticipantPublicId(), c.getJobName());
        }

        //자원 반납
        //쓰래드 자원 반납
        gameThread.remove(sessionId);
        //사용 직업 리스트 자원 반납
        gameRoles.remove(sessionId);
        //유저 직업 매칭 자원 반납
        roleMatching.remove(sessionId);
        //참여자 목록 자원 반납.
        participantsList.remove(sessionId);
        //살아있는 경찰 수 자원 반납.
        alivePolices.remove(sessionId);
        //중요인물 자원 반납
        kiraAndL.remove(sessionId);
        //노트 자원 반납.
        deathNoteList.remove(sessionId);
        readySetting.remove(sessionId);


        if (deathNoteThread != null) {
            deathNoteThread.interrupt();
        }

        data.addProperty("winner", winner);
        data.addProperty("gameStatus", 8);
        params.add("data", data);

        //게임 종료 알리기
        for (Participant p : participants) {
            rpcNotificationService.sendNotification(p.getParticipantPrivateId(),
                    ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
        }
    }

    //귓속말 보내기
    private void sendSecretMessgae(Participant participant, JsonObject message, String sessionId, Set<Participant> participants, JsonObject params, JsonObject data, RpcNotificationService notice) {
        String publicId = data.get("to").getAsString();
        params.add("data", data);


        rpcNotificationService.sendNotification(participant.getParticipantPrivateId(),
                ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
        for (Participant p : participants) {
            if (p.getParticipantPublicId().equals(publicId)) {
                rpcNotificationService.sendNotification(p.getParticipantPrivateId(),
                        ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                break;
            }
        }

    }
}
