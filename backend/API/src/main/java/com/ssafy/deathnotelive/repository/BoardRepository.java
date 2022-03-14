package com.ssafy.deathnotelive.repository;

import com.ssafy.deathnotelive.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
