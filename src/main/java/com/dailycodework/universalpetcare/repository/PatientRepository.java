package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}
