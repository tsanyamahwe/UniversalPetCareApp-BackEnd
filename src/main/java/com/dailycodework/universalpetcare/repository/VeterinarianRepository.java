package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {
    List<Veterinarian> findBySpecialization(String specialization);
    boolean existsBySpecialization(String specialization);
    @Query("SELECT DISTINCT v.specialization From Veterinarian v")
    List<String> getDistinctVetSpecialization();
    @Query("SELECT v.specialization as specialization, COUNT(v) as count FROM Veterinarian v GROUP BY v.specialization")
    List<Object[]> countVeterinariansBySpecialization();
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Veterinarian v WHERE LOWER(v.specialization) = LOWER(?1)")
    boolean existsBySpecializationIgnoreCase(String normalizesSpecialization);
}
