package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.model.Patient;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.PatientRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.service.role.IRoleService;
import com.dailycodework.universalpetcare.service.user.UserAttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PatientFactory {
    private final PatientRepository patientRepository;
    private final UserAttributeMapper userAttributeMapper;
    private final IRoleService roleService;

    public User createPatient(RegistrationRequest registrationRequest) {
        Patient patient = new Patient();
        patient.setRoles(roleService.setUserRoles(Collections.singletonList("PATIENT")));
        userAttributeMapper.setCommonAttributes(registrationRequest, patient);
        return patientRepository.save(patient);
    }
}
