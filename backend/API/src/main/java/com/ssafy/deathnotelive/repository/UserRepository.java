package com.ssafy.deathnotelive.repository;

import com.ssafy.deathnotelive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User getByUserId(String userId);

    User getByEmail(String email);

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);
}
