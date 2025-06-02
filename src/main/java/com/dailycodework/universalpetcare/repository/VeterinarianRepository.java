package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {
}
