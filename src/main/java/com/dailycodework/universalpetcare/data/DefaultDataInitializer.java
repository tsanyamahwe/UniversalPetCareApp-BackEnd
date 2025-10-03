package com.dailycodework.universalpetcare.data;

import com.dailycodework.universalpetcare.model.Admin;
import com.dailycodework.universalpetcare.model.Patient;
import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.model.Veterinarian;
import com.dailycodework.universalpetcare.repository.*;
import com.dailycodework.universalpetcare.service.role.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DefaultDataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_PATIENT", "ROLE_VETERINARIAN");
        createDefaultRoleIfNotExists(defaultRoles);
        createDefaultAdminIfNotExists();
        createDefaultVeterinariansIfNotExists();
        createDefaultPatientsIfNotExists();
    }

    @Transactional
    public void createDefaultVeterinariansIfNotExists(){
        Role veterinarianRole = roleService.getRoleByName("ROLE_VETERINARIAN").orElseThrow(() -> new RuntimeException("ROLE_VETERINARIAN not found"));
        for(int i=1; i<= 10; i++){
            String defaultEmail = "veterinarian"+i+"@gmail.com";
            if(userRepository.existsByEmail(defaultEmail)){
                continue;
            }
            Veterinarian veterinarian = new Veterinarian();
            veterinarian.setFirstName("Veterinarian");
            veterinarian.setLastName("Number"+i);
            veterinarian.setGender("Not Specified");
            veterinarian.setPhoneNumber("0123456789");
            veterinarian.setEmail(defaultEmail);
            veterinarian.setPassword(passwordEncoder.encode("password"+i));
            veterinarian.setUserType("VET");
            veterinarian.setSpecialization("Dermatologist");
            veterinarian.setEnabled(true);
            veterinarian.setRoles(new HashSet<>(Collections.singletonList(veterinarianRole)));

            Veterinarian theVeterinarian = veterinarianRepository.save(veterinarian);
            System.out.println("Default veterinarian user "+i+" created successfully");
        }
    }

    @Transactional
    public void createDefaultPatientsIfNotExists(){
        Role patientRole = roleService.getRoleByName("ROLE_PATIENT").orElseThrow(() -> new RuntimeException("ROLE_PATIENT not found"));
        for(int i=1; i<= 10; i++) {
            String defaultEmail = "patient" + i + "@gmail.com";
            if (userRepository.existsByEmail(defaultEmail)) {
                continue;
            }
            Patient patient = new Patient();
            patient.setFirstName("Patient");
            patient.setLastName("Number" + i);
            patient.setGender("Not Specified");
            patient.setPhoneNumber("0123456789");
            patient.setEmail(defaultEmail);
            patient.setPassword(passwordEncoder.encode("password"+i));
            patient.setUserType("PATIENT");
            patient.setEnabled(true);
            patient.setRoles(new HashSet<>(Collections.singletonList(patientRole)));

            Patient thePatient = patientRepository.save(patient);
            System.out.println("Default veterinarian user " + i + " created successfully");
        }
    }

    @Transactional
    public void createDefaultAdminIfNotExists(){
        Role adminRole = roleService.getRoleByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
        final String defaultAdminEmail = "admin@email.com";
        if(userRepository.existsByEmail(defaultAdminEmail)){
            return;
        }
        Admin admin = new Admin();
        admin.setFirstName("UPC");
        admin.setLastName("Admin");
        admin.setGender("Female");
        admin.setPhoneNumber("0612349547");
        admin.setEmail(defaultAdminEmail);
        admin.setPassword(passwordEncoder.encode("Sanyas84$!@#$"));
        admin.setUserType("ADMIN");
        admin.setEnabled(true);
        admin.setRoles(new HashSet<>(Collections.singletonList(adminRole)));

        Admin theAdmin = adminRepository.save(admin);
        System.out.println("Default admin user created successfully");
    }

    private void createDefaultRoleIfNotExists(Set<String> roles){
        roles.stream().filter(role -> roleRepository
                .findByName(role).isEmpty()).map(Role::new).forEach(roleRepository::save);
    }
}
