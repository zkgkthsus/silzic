package com.ssafy.deathnotelive.repository;

import com.ssafy.deathnotelive.entity.Room;
import com.ssafy.deathnotelive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findRoomByUser (User user);

    Optional<Room> findRoomByRoomCode (String roomCode);

}
