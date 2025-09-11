package com.dailycodework.universalpetcare.service.role;

import com.dailycodework.universalpetcare.model.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IRoleService {
    List<Role> getAllRoles();
    Role getRoleById(Long Id);
    Optional<Role> getRoleByName(String roleName);
    Collection<Role> setUserRoles(List<String> roles);
    void saveRole(Role role);
}
