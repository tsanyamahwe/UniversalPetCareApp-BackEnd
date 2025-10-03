package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.PasswordReset;
import com.dailycodework.universalpetcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    PasswordReset findByToken(String token);
    // void deleteByUser(User user);
    @Modifying
    @Query("DELETE FROM PasswordReset pr WHERE pr.user = :user")
    void deleteByUser(@Param("user") User user);
}
