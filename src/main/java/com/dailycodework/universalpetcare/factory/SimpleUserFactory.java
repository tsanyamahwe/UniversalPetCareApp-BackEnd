package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.exception.UserAlreadyExistException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleUserFactory implements UserFactory{
    private final UserRepository userRepository;
    private final VeterinarianFactory veterinarianFactory;
    private final AdminFactory adminFactory;
    private final PatientFactory patientFactory;

    @Override
    public User createUser(RegistrationRequest registrationRequest) {
        if(userRepository.existsByEmail(registrationRequest.getEmail())){
            throw new UserAlreadyExistException("Oops! "+registrationRequest.getEmail()+ " already exist");
        }else{
            switch (registrationRequest.getUserType()){
                case "VET" -> {return veterinarianFactory.createVeterinarian(registrationRequest);}
                case "PATIENT" -> {return patientFactory.createPatient(registrationRequest);}
                case "ADMIN" -> {return adminFactory.createAdmin(registrationRequest);}
                default -> {return null;}
            }
        }
    }
}
