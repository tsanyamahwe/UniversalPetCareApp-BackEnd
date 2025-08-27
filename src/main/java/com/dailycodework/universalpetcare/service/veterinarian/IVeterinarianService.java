package com.dailycodework.universalpetcare.service.veterinarian;

import com.dailycodework.universalpetcare.dto.UserDTO;
import com.dailycodework.universalpetcare.model.Veterinarian;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface IVeterinarianService {
    List<UserDTO> getAllVeterinariansWithDetails();

    List<UserDTO> findAvailableVeterinariansForAppointment(String specialization, LocalDate date, LocalTime time);

    List<Veterinarian> getVeterinarianBySpecialization(String specialization);

    List<String> getVeterinarianSpecializations();

    List<Map<String, Object>> aggregateVeterinariansBySpecialization();
}
