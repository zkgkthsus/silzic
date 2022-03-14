package com.ssafy.deathnotelive.service;

import com.ssafy.deathnotelive.entity.Participants;
import com.ssafy.deathnotelive.entity.Room;
import com.ssafy.deathnotelive.entity.User;
import com.ssafy.deathnotelive.error.exception.ParticipantsNotFoundException;
import com.ssafy.deathnotelive.error.exception.RoomNotFoundException;
import com.ssafy.deathnotelive.error.exception.UserNotFoundException;
import com.ssafy.deathnotelive.repository.ParticipantsRepository;
import com.ssafy.deathnotelive.repository.RoomRepository;
import com.ssafy.deathnotelive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ParticipantsRepository participantsRepository;

    public Room getRoomByUserId(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Error"));
        Room room = roomRepository.findRoomByUser(user).orElseThrow(() -> new RoomNotFoundException("Error"));

        return room;
    }

    public boolean validateName(String roomCode, String nickName) {
        Room room = roomRepository.findRoomByRoomCode(roomCode).orElseThrow(() -> new RoomNotFoundException("Error"));
        List<Participants> userList = participantsRepository.findAllByRoom(room);

        for (Participants p : userList) {
            if (p.getNickName().equals(nickName)) {
                return false;
            }
        }
        //중복되는 닉네임이 없다면 등록한다.
        participantsRepository.save(Participants.builder().room(room).nickName(nickName).build());
        return true;
    }

    public String createRoom(String userId) {
        //유저가 없으면 에러!
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Error"));
        Optional<Room> room = roomRepository.findRoomByUser(user);

        //룸이 없으면 룸 코드 새롭게 만들어서 넣어주기.
        if (!room.isPresent()) {

            String roomCode;
            HashSet<Room> set = new HashSet<>(roomRepository.findAll());
            while (true) {
                roomCode = Util.getRoomCode();
                if(!set.contains(roomCode)) break;
            }

            Room nRoom = Room.builder().user(user).roomCode(roomCode).isOpen(true).build();
            roomRepository.save(nRoom);
            return roomCode;
            //룸이 있으면 룸 열고 룸 코드 보내주기.
        } else {
            Room nRoom = room.get();
            nRoom.setIsOpen(true);
            //변경된 사항 저장
            roomRepository.save(nRoom);
            String roomCode = nRoom.getRoomCode();
            return roomCode;
        }

    }

    public void finishRoom(String roomCode) {
        Room room = roomRepository.findRoomByRoomCode(roomCode).orElseThrow(() -> new RoomNotFoundException("Error"));
        //room의 닉네임 전부 지우기.
        List<Participants> userList = participantsRepository.findAllByRoom(room);
        for (Participants p : userList) {
            participantsRepository.delete(p);
        }
        room.setIsOpen(false);
        roomRepository.save(room);
    }

    public void deleteParticipant(String sessionId, String nickName) {
        Room room = roomRepository.findRoomByRoomCode(sessionId).orElseThrow(() -> new RoomNotFoundException("Error"));
        Participants participants = participantsRepository.findByRoomAndNickName(room, nickName).orElseThrow(()-> new ParticipantsNotFoundException("Error"));
        participantsRepository.delete(participants);

    }
}
