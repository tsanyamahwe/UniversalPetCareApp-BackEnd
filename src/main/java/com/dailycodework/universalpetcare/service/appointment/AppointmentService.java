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
import java.util.stream.Collectors;

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
    public Appointment createAppointment(BookAppointmentRequest bookAppointmentRequest, Long senderId, Long recipientId) {
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> recipient = userRepository.findById(recipientId);

        if(sender.isPresent() && recipient.isPresent()){
            Appointment appointment = bookAppointmentRequest.getAppointment();
            appointment.addPatient(sender.get());
            appointment.addVeterinarian(recipient.get());
            appointment.setAppointmentNo();
            appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

            List<Pet> pets = bookAppointmentRequest.getPets();
            if(pets != null && !pets.isEmpty()) {
                for(Pet pet : pets) {
                    pet.setAppointment(appointment);
                }
                appointment.setPets(new ArrayList<>(pets));
            }
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
            throw new IllegalStateException(FeedBackMessage.APPOINTMENT_ALREADY_APPROVED);
        }else{
            existingAppointment.setAppointmentDate(appointmentUpdateRequestRequest.getAppointmentDate());
            existingAppointment.setAppointmentTime(appointmentUpdateRequestRequest.getAppointmentTime());
            existingAppointment.setReason(appointmentUpdateRequestRequest.getReason());
        }
        return appointmentRepository.save(existingAppointment);
    }

    @Override
    @Transactional
    public void deleteAppointmentById(Long id) {
        appointmentRepository.findById(id).ifPresentOrElse(appointment -> {
            // Get all pets for this appointment, and delete them
            List<Pet> pets = appointment.getPets();
            if (!pets.isEmpty()) {
                petRepository.deleteAll(pets);
            }
            appointment.getPets().clear();// Clear the relationship
            appointmentRepository.delete(appointment); // Delete the appointment
        }, () -> {
            throw new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND);
        });
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND));
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

    @Override
    public Appointment cancelAppointment(Long appointmentId){
        return appointmentRepository.findById(appointmentId).filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {appointment.setStatus(AppointmentStatus.CANCELLED);
                return appointmentRepository.saveAndFlush(appointment);
                }).orElseThrow(()-> new IllegalStateException(FeedBackMessage.CANNOT_CANCEL_APPOINTMENT));
    }

    @Override
    public Appointment approveAppointment(Long appointmentId){
        return appointmentRepository.findById(appointmentId).filter(appointment -> !appointment.getStatus().equals(AppointmentStatus.APPROVED))
                .map(appointment -> {appointment.setStatus(AppointmentStatus.APPROVED);
                    return appointmentRepository.saveAndFlush(appointment);
                }).orElseThrow(()-> new IllegalStateException(FeedBackMessage.APPOINTMENT_ALREADY_APPROVED));
    }

    @Override
    public Appointment declineAppointment(Long appointmentId){
        return appointmentRepository.findById(appointmentId).map(appointment -> {appointment.setStatus(AppointmentStatus.NOT_APPROVED);
                    return appointmentRepository.saveAndFlush(appointment);
                }).orElseThrow(()-> new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND));
    }

    @Override
    public long countAppointments(){
        return  appointmentRepository.count();
    }

    private String formatAppointmentStatus(AppointmentStatus appointmentStatus){
        return appointmentStatus.toString().replace("_", "-").toLowerCase();
    }

    private Map<String, Object> createStatusSummaryMap(AppointmentStatus appointmentStatus, Long value){
        Map<String, Object> summaryMap = new HashMap<>();
        summaryMap.put("name", formatAppointmentStatus(appointmentStatus));
        summaryMap.put("value", value);
        return summaryMap;
    }

    @Override
    public List<Map<String, Object>> getAppointmentStatusSummary(){
        return getAllAppointments()
                .stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> createStatusSummaryMap(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
