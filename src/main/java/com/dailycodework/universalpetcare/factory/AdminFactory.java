package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.model.Admin;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.AdminRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.service.user.UserAttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminFactory {
    private final AdminRepository adminRepository;
    private final UserAttributeMapper userAttributeMapper;

    public User createAdmin(RegistrationRequest registrationRequest) {
        Admin admin = new Admin();
        userAttributeMapper.setCommonAttributes(registrationRequest, admin);
        return adminRepository.save(admin);
    }
}
