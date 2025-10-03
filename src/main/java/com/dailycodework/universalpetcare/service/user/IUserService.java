package com.dailycodework.universalpetcare.service.user;

import com.dailycodework.universalpetcare.dto.PasswordChangeInfoDTO;
import com.dailycodework.universalpetcare.dto.UserDTO;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.request.UserUpdateRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IUserService {
    User register(RegistrationRequest registrationRequest);

    void changeUserPassword(Long userId, String currentPassword, String newPassword, String confirmNewPassword);

    boolean canUserChangePassword(Long userId);

    long getDaysUntilPasswordChangeAllowed(Long userId);

    PasswordChangeInfoDTO getPasswordChangeInfo(Long userId);

    User update(Long userId, UserUpdateRequest userUpdateRequest);

    User findById(Long userId);

    User findByEmail(String email);

    void delete(Long userId);

    List<UserDTO> getAllUsers();

    UserDTO getUserWithDetails(Long userId) throws SQLException;

    long countVeterinarians();

    long countPatients();

    long countAllUsers();

    Map<String, Map<String, Long>> aggregateUsersByMonthAndType();

    Map<String, Map<String, Long>> aggregatesUsersByEnabledStatusAndType();

    void lockUserAccount(Long userId);

    void unLockUserAccount(Long userId);
}
