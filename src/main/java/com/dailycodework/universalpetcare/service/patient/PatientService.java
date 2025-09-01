package com.dailycodework.universalpetcare.service.patient;

import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.UserDTO;
import com.dailycodework.universalpetcare.model.Patient;
import com.dailycodework.universalpetcare.model.Veterinarian;
import com.dailycodework.universalpetcare.repository.PatientRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService implements IPatientService{
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final EntityConverter<Patient, UserDTO> entityConverter;

    @Override
    public List<UserDTO> getAllPatientsWithDetails() {
        List<Patient> patients = userRepository.findAllPatientsByUserType("PATIENT");
        return patients.stream().map(this::mapPatientToUserDTO).toList();
    }

    private UserDTO mapPatientToUserDTO(Patient patient) {
        UserDTO userDTO = entityConverter.mapEntityToDTO(patient, UserDTO.class);
        userDTO.setId(patient.getId());
        userDTO.setFirstName(patient.getFirstName());
        userDTO.setLastName(patient.getLastName());
        userDTO.setEmail(patient.getEmail());
        userDTO.setPhoneNumber(patient.getPhoneNumber());
        userDTO.setUserType(patient.getUserType());

        return userDTO;
    }
}
