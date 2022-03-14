package io.openvidu.server.game;

import com.google.gson.JsonObject;
import io.openvidu.client.internal.ProtocolElements;
import io.openvidu.server.core.Participant;
import io.openvidu.server.rpc.RpcNotificationService;

import java.util.ArrayList;
import java.util.Collections;

public class GameRunnable implements Runnable {

    private ArrayList<Participant> participantsList;
    private RpcNotificationService rpcNotificationService;
    private String sessionId;

    public GameRunnable(String sessionId, ArrayList<Participant> participantsList, RpcNotificationService notice) {
        this.sessionId = sessionId;
        this.participantsList = participantsList;
        this.rpcNotificationService = notice;
    }

    @Override
    public void run() {
        try {
            JsonObject data = new JsonObject();
            JsonObject params = new JsonObject();
            int exchangeCnt = 1;
            int misssionCnt = 1;

            //타입 지정
            String type = "signal:autoSystem";
            params.addProperty(ProtocolElements.PARTICIPANTSENDMESSAGE_TYPE_PARAM, type);
            Thread.sleep(30000);

            while (true) {
                //처음 30초 sleep
                Thread.sleep(30000);

                //역할 목록 가져오기
                ArrayList<Characters> roles = GameService.roleMatching.get(sessionId);

                //살아있는 대상자만
                ArrayList<Characters> list = new ArrayList<>();
                for (Characters c : roles) {
                    if (c.isAlive()) {
                        list.add(c);
                    }
                }

                /**
                 * 명교 시작.
                 */
                ExchangeName(list, data, exchangeCnt, params);
                exchangeCnt++;
                /**
                 * 명교 끝
                 */

                //처음 30초 sleep
                Thread.sleep(30000);

                //역할 목록 가져오기
                roles = GameService.roleMatching.get(sessionId);
                //살아있는 대상자만
                list = new ArrayList<>();
                for (Characters c : roles) {
                    if (c.isAlive()) {
                        list.add(c);
                    }
                }

                /**
                 * 명교 시작.
                 */
                ExchangeName(list, data, exchangeCnt, params);
                exchangeCnt++;
                /**
                 * 명교 끝
                 */

                /**
                 * 미션 시작.
                 */
                //미션 시작할 사람 목록
                ArrayList<Characters> missionCandidates = new ArrayList<>(list);

                if (list.size() >= 4) {
                    //0,1번은 명교하러 갔으니 제외
                    missionCandidates.remove(1);
                    missionCandidates.remove(0);

                }

                //미션 대기자 목록 섞기
                Collections.shuffle(missionCandidates);

                //data에 담을 정보.
                data.addProperty("action", "missionStart");
                data.addProperty("turn", misssionCnt);
                params.add("data", data);

                //경찰 미션중인지 확인 하는 변수
                boolean isPolice = false;

                //미션 수행중인 사람 수
                int missionSize = missionCandidates.size();

                if (missionSize >= 2) {
                    //2명만 미션 시작.
                    for (int i = 0; i < 2; i++) {
                        rpcNotificationService.sendNotification(missionCandidates.get(i).getParticipant().getParticipantPrivateId(),
                                ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                    }

                    //미션 수행중인 사람이 police면 추종자에게 알리기.
                    for (int i = 0; i < list.size(); i++) {
                        if ("POLICE".equals(list.get(i).getJobName()) &&
                                (list.get(i) == missionCandidates.get(0) || list.get(i) == missionCandidates.get(1))) {
                            isPolice = true;
                            break;
                        }
                    }
                } else if (missionSize == 1) {

                    rpcNotificationService.sendNotification(missionCandidates.get(0).getParticipant().getParticipantPrivateId(),
                            ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);

                    //미션 수행중인 사람이 police면 추종자에게 알리기.
                    for (int i = 0; i < list.size(); i++) {
                        if ("POLICE".equals(list.get(i).getJobName()) &&
                                (list.get(i) == missionCandidates.get(0))) {
                            isPolice = true;
                            break;
                        }
                    }
                }

                if (isPolice) {
                    //data에 담을 정보.
                    data.remove("turn");
                    data.addProperty("action", "policeMissionStart");
                    params.add("data", data);

                    //추종자들에게 전달.
                    for (int i = 0; i < roles.size(); i++) {
                        if ("CRIMINAL".equals(roles.get(i).getJobName())) {
                            rpcNotificationService.sendNotification(roles.get(i).getParticipant().getParticipantPrivateId(),
                                    ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
                        }
                    }
                    data.remove("announce");
                }
                misssionCnt++;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void ExchangeName(ArrayList<Characters> roles, JsonObject data, int exchangeCnt, JsonObject params) throws InterruptedException {
        /**
         * 명교 시작.
         */

        //참가자 목록 섞기
        Collections.shuffle(roles);

        if (roles.get(0).getJobName().equals("KIRA") && roles.get(1).getJobName().equals("CRIMINAL")) {
            data.addProperty("action", "meetKIRA");
            params.add("data", data);

            for (int i = 0; i < 2; i++) {
                rpcNotificationService.sendNotification(roles.get(i).getParticipant().getParticipantPrivateId(),
                        ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
            }
        } else if (roles.get(1).getJobName().equals("KIRA") && roles.get(0).getJobName().equals("CRIMINAL")) {
            data.addProperty("action", "meetKIRA");
            params.add("data", data);

            for (int i = 0; i < 2; i++) {
                rpcNotificationService.sendNotification(roles.get(i).getParticipant().getParticipantPrivateId(),
                        ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
            }
        }

        //data에 담을 정보
        data.addProperty("action", "exchangeNameStart");
        data.addProperty("turn", exchangeCnt);
        params.add("data", data);

        //2명만 명교 시작.
        for (int i = 0; i < 2; i++) {
            rpcNotificationService.sendNotification(roles.get(i).getParticipant().getParticipantPrivateId(),
                    ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
            System.out.println(roles.get(i).getParticipant().getParticipantPublicId());
        }

        data.addProperty("action", "nameTurn");
        params.add("data", data);

        for (Characters c : roles) {
            rpcNotificationService.sendNotification(c.getParticipant().getParticipantPrivateId(),
                    ProtocolElements.PARTICIPANTSENDMESSAGE_METHOD, params);
        }

        /**
         * 명교 끝
         */
    }
}