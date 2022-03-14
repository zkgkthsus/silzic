package com.ssafy.deathnotelive.repository;

import com.ssafy.deathnotelive.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
