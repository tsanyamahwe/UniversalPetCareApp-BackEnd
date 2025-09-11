package com.dailycodework.universalpetcare.service.role;

import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService{
    public  final RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long Id) {
        return roleRepository.findById(Id).orElse(null);
    }

    @Override
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    @Override
    public Collection<Role> setUserRoles(List<String> roles) {
        return roles.stream()
                .map(roleName -> roleRepository.findByName("ROLE_"+roleName).orElse(null))
                .collect(Collectors.toList());
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
    }
}
