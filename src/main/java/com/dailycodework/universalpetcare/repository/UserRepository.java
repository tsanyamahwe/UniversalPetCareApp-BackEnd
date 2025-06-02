package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
