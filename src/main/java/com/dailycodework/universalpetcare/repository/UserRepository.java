package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    List<Veterinarian> findAllByUserType(String vet);
    long countByUserType(String userType);
}
