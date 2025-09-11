package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.Veterinarian;
import com.dailycodework.universalpetcare.repository.VeterinarianRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.service.role.IRoleService;
import com.dailycodework.universalpetcare.service.user.UserAttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class VeterinarianFactory {
    private final VeterinarianRepository veterinarianRepository;
    private final UserAttributeMapper userAttributeMapper;
    private  final IRoleService roleService;

    public User createVeterinarian(RegistrationRequest registrationRequest) {
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setRoles(roleService.setUserRoles(Collections.singletonList("VETERINARIAN")));
        userAttributeMapper.setCommonAttributes(registrationRequest, veterinarian);
        veterinarian.setSpecialization(registrationRequest.getSpecialization());
        return veterinarianRepository.save(veterinarian);
    }
}
