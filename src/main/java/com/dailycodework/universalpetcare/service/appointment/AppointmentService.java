package com.dailycodework.universalpetcare.service.appointment;

import com.dailycodework.universalpetcare.dto.AppointmentDTO;
import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.PetDTO;
import com.dailycodework.universalpetcare.enums.AppointmentStatus;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Pet;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.AppointmentUpdateRequest;
import com.dailycodework.universalpetcare.request.BookAppointmentRequest;
import com.dailycodework.universalpetcare.service.pet.IPetService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService{
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final IPetService petService;
    private final EntityConverter<Appointment, AppointmentDTO> entityConverter;
    private final EntityConverter<Pet, PetDTO> petEntityConverter;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Appointment createAppointment(BookAppointmentRequest bookAppointmentRequest, Long senderId, Long recipientId) {
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> recipient = userRepository.findById(recipientId);
        if(sender.isPresent() && recipient.isPresent()){
            Appointment appointment = bookAppointmentRequest.getAppointment();
            List<Pet> pets = bookAppointmentRequest.getPet();
            if(pets == null){
                pets= Collections.emptyList();
            }
            pets.forEach(pet -> pet.setAppointment(appointment));
            List<Pet> savedPet = petService.savePetForAppointment(pets);
            appointment.setPets(savedPet);
            appointment.addPatient(sender.get());
            appointment.addVeterinarian(recipient.get());
            appointment.setAppointmentNo();
            appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);
            return appointmentRepository.save(appointment);
        }
        throw new ResourceNotFoundException(FeedBackMessage.SENDER_RECIPIENT_NOT_FOUND);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment updateAppointment(Long id, AppointmentUpdateRequest appointmentUpdateRequestRequest) {
        Appointment existingAppointment = getAppointmentById(id);
        if(!Objects.equals(existingAppointment.getStatus(), AppointmentStatus.WAITING_FOR_APPROVAL)){
            throw new IllegalStateException(FeedBackMessage.ALREADY_APPROVED);
        }else{
            existingAppointment.setAppointmentDate(appointmentUpdateRequestRequest.getAppointmentDate());
            existingAppointment.setAppointmentTime(appointmentUpdateRequestRequest.getAppointmentTime());
            existingAppointment.setReason(appointmentUpdateRequestRequest.getReason());
        }
        return appointmentRepository.save(existingAppointment);
    }

    @Override
    public void deleteAppointmentById(Long id) {
        appointmentRepository.findById(id).ifPresentOrElse(appointmentRepository::delete, ()->{throw new ResourceNotFoundException(FeedBackMessage.NOT_FOUND);});
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));
    }

    @Override
    public Appointment getAppointmentByNo(String appointmentNo) {
        return appointmentRepository.findByAppointmentNo(appointmentNo);
    }

    @Override
    public List<AppointmentDTO> getUserAppointments(Long userId){
        List<Appointment> appointments = appointmentRepository.findAllAppointmentsByUserId(userId);
        return appointments.stream().map(appointment -> {
            AppointmentDTO appointmentDTO = entityConverter.mapEntityToDTO(appointment, AppointmentDTO.class);
            List<PetDTO> petDTO = appointment.getPets().stream().map(pet -> petEntityConverter.mapEntityToDTO(pet, PetDTO.class)).toList();
            appointmentDTO.setPets(petDTO);
            return appointmentDTO;
        }).toList();
    }
}
