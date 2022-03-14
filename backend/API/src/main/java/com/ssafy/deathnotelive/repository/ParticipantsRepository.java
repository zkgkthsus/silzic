package com.ssafy.deathnotelive.repository;

import com.ssafy.deathnotelive.entity.Participants;
import com.ssafy.deathnotelive.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantsRepository extends JpaRepository<Participants, Long> {

    List<Participants> findAllByRoom(Room room);

    Optional<Participants> findByRoomAndNickName(Room room, String nickName);
}
