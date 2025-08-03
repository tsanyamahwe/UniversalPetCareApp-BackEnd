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
import com.dailycodework.universalpetcare.repository.PetRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.AppointmentUpdateRequest;
import com.dailycodework.universalpetcare.request.BookAppointmentRequest;
import com.dailycodework.universalpetcare.service.pet.IPetService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService{
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final IPetService petService;
    private final EntityConverter<Appointment, AppointmentDTO> entityConverter;
    private final EntityConverter<Pet, PetDTO> petEntityConverter;
    private final ModelMapper modelMapper;
    private final PetRepository petRepository;

    @Transactional
    @Override
//    public Appointment createAppointment(BookAppointmentRequest bookAppointmentRequest, Long senderId, Long recipientId) {
//        Optional<User> sender = userRepository.findById(senderId);
//        Optional<User> recipient = userRepository.findById(recipientId);
//        if(sender.isPresent() && recipient.isPresent()){
//            Appointment appointment = bookAppointmentRequest.getAppointment();
//            List<Pet> pets = bookAppointmentRequest.getPet();
//            if(pets == null){
//                pets= Collections.emptyList();
//            }
//            appointment.addPatient(sender.get());
//            appointment.addVeterinarian(recipient.get());
//            appointment.setAppointmentNo();
//            appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);
//
//            Appointment savedAppointment = appointmentRepository.save(appointment);
//            pets.forEach(pet -> pet.setAppointment(savedAppointment));
//            List<Pet> savedPet = petService.savePetForAppointment(pets);
//
//            savedAppointment.setPets(savedPet);
//
//            return savedAppointment;
//        }
//        throw new ResourceNotFoundException(FeedBackMessage.SENDER_RECIPIENT_NOT_FOUND);
//    }
    public Appointment createAppointment(BookAppointmentRequest bookAppointmentRequest, Long senderId, Long recipientId) {
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> recipient = userRepository.findById(recipientId);
        if(sender.isPresent() && recipient.isPresent()){
            Appointment appointment = bookAppointmentRequest.getAppointment();
            appointment.addPatient(sender.get());
            appointment.addVeterinarian(recipient.get());
            appointment.setAppointmentNo();
            appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

            // Get pets from request
            List<Pet> pets = bookAppointmentRequest.getPet();
            if(pets != null && !pets.isEmpty()) {
                // Set the bidirectional relationship properly
                for(Pet pet : pets) {
                    pet.setAppointment(appointment);
                }
                appointment.setPets(new ArrayList<>(pets));
            }

            // Let JPA cascade handle everything
            Appointment savedAppointment = appointmentRepository.save(appointment);

            // Debug: Print what was saved
            System.out.println("=== DEBUG INFO ===");
            System.out.println("Appointment ID: " + savedAppointment.getId());
            System.out.println("Number of pets: " + savedAppointment.getPets().size());
            savedAppointment.getPets().forEach(pet -> {
                System.out.println("Pet ID: " + pet.getId() + ", Name: " + pet.getName() +
                        ", Appointment ID: " + (pet.getAppointment() != null ? pet.getAppointment().getId() : "NULL"));
            });

            return savedAppointment;
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
    @Transactional
//    public void deleteAppointmentById(Long id) {
//        appointmentRepository.findById(id).ifPresentOrElse(appointment -> {
//            appointment.getPets().size();
//            appointmentRepository.delete(appointment);
//        }, ()->{throw new ResourceNotFoundException(FeedBackMessage.NOT_FOUND);});
//    }

    public void deleteAppointmentById(Long id) {
        appointmentRepository.findById(id).ifPresentOrElse(appointment -> {
            // Get all pets for this appointment, and delete them
            List<Pet> pets = appointment.getPets();
            if (!pets.isEmpty()) {
                petRepository.deleteAll(pets);
            }

            // Clear the relationship
            appointment.getPets().clear();

            // Delete the appointment
            appointmentRepository.delete(appointment);

        }, () -> {
            throw new ResourceNotFoundException(FeedBackMessage.NOT_FOUND);
        });
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
