package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.dto.UserDTO;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.service.appointment.IAppointmentService;
import com.dailycodework.universalpetcare.service.veterinarian.IVeterinarianService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.VETERINARIANS)
public class VeterinarianController {
    private final IVeterinarianService veterinarianService;
    private final IAppointmentService appointmentService;

    @GetMapping(UrlMapping.GET_ALL_VETERINARIANS)
    public ResponseEntity<APIResponse> getAllVeterinarians(){
        List<UserDTO> allVeterinarians = veterinarianService.getAllVeterinariansWithDetails();
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.VETERINARIANS_FOUND, allVeterinarians));
    }

    @GetMapping(UrlMapping.SEARCH_VETERINARIAN_FOR_APPOINTMENT)
    public  ResponseEntity<APIResponse> searchVeterinariansForAppointment(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime time,
            @RequestParam String specialization){
        try {
            List<UserDTO> availableVeterinarians = veterinarianService.findAvailableVeterinariansForAppointment(specialization, date, time);
            if(availableVeterinarians.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new APIResponse(FeedBackMessage.NO_VETS_AVAILABLE, null));
            }
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.VETERINARIAN_FOUND, availableVeterinarians));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_VET_SPECIALIZATIONS)
    public ResponseEntity<APIResponse> getAllSpecializations(){
        try {
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.VET_SPEC_FOUND, veterinarianService.getVeterinarianSpecializations()));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));

        }
    }

    @GetMapping(UrlMapping.VETS_AGGREGATE_BY_SPECIALIZATION)
    public ResponseEntity<List<Map<String, Object>>> aggregateVeterinariansBySpecialization(){
            List<Map<String, Object>> aggregatedVeterinarians = veterinarianService.aggregateVeterinariansBySpecialization();
            return ResponseEntity.ok(aggregatedVeterinarians);
    }
}
