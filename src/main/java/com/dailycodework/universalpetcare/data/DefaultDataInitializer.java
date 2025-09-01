package com.dailycodework.universalpetcare.data;

import com.dailycodework.universalpetcare.model.Patient;
import com.dailycodework.universalpetcare.model.Veterinarian;
import com.dailycodework.universalpetcare.repository.PatientRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.repository.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultDataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final PatientRepository patientRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createDefaultVeterinariansIfNotExists();
        createDefaultPatientsIfNotExists();
    }

    private void createDefaultVeterinariansIfNotExists(){
        for(int i=1; i<= 30; i++){
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
            veterinarian.setPassword("password"+i);
            veterinarian.setUserType("VET");
            veterinarian.setSpecialization("Dermatologist");

            Veterinarian theVeterinarian = veterinarianRepository.save(veterinarian);
            theVeterinarian.setEnabled(true);
            System.out.println("Default veterinarian user "+i+" created successfully");
        }
    }

    private void createDefaultPatientsIfNotExists(){
        for(int i=1; i<= 30; i++) {
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
            patient.setPassword("password" + i);
            patient.setUserType("PATIENT");

            Patient thePatient = patientRepository.save(patient);
            thePatient.setEnabled(true);
            System.out.println("Default veterinarian user " + i + " created successfully");
        }
    }
}
