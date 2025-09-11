package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.model.Admin;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.AdminRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.service.role.IRoleService;
import com.dailycodework.universalpetcare.service.user.UserAttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AdminFactory {
    private final AdminRepository adminRepository;
    private final UserAttributeMapper userAttributeMapper;
    private final IRoleService roleService;

    public User createAdmin(RegistrationRequest registrationRequest) {
        Admin admin = new Admin();
        admin.setRoles(roleService.setUserRoles(Collections.singletonList("ADMIN")));
        userAttributeMapper.setCommonAttributes(registrationRequest, admin);
        return adminRepository.save(admin);
    }
}
