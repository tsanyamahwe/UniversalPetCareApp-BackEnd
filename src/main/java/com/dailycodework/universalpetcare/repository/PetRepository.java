package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
