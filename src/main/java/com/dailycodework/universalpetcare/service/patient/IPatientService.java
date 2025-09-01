package com.dailycodework.universalpetcare.service.patient;

import com.dailycodework.universalpetcare.dto.UserDTO;

import java.util.List;

public interface IPatientService {
    List<UserDTO> getAllPatientsWithDetails();
}
