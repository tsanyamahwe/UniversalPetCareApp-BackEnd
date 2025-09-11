package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.service.role.IRoleService;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.ROLES)
public class RoleController {
    private final IRoleService roleService;

    @GetMapping(UrlMapping.ALL_ROLES)
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping(UrlMapping.ROLE_BY_ID)
    public Role getRoleById(Long id) {
        return roleService.getRoleById(id);
    }

    @GetMapping(UrlMapping.ROLE_BY_NAME)
    public ResponseEntity<Role> getRoleByName(@RequestParam String name) {
        return roleService.getRoleByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
