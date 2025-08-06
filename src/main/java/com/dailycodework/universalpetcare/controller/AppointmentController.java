package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Pet;
import com.dailycodework.universalpetcare.request.AppointmentUpdateRequest;
import com.dailycodework.universalpetcare.request.BookAppointmentRequest;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.service.appointment.AppointmentService;
import com.dailycodework.universalpetcare.service.pet.IPetService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.APPOINTMENTS)
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final IPetService petService;

    @GetMapping(UrlMapping.ALL_APPOINTMENTS)
    public ResponseEntity<APIResponse> getAllAppointments(){
        try{
            List<Appointment> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.RESOURCE_FOUND, appointments));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PostMapping(UrlMapping.BOOK_APPOINTMENT)
    public ResponseEntity<APIResponse> bookAppointment(@RequestBody BookAppointmentRequest bookAppointmentRequest, @RequestParam Long senderId, @RequestParam Long recipientId){
        try{
            Appointment theAppointment = appointmentService.createAppointment(bookAppointmentRequest, senderId, recipientId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.CREATE_SUCCESS, theAppointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_APPOINTMENT_BY_ID)
    public ResponseEntity<APIResponse> getAppointmentById(@PathVariable Long id){
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.RESOURCE_FOUND, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_APPOINTMENT_BY_NO)
    public ResponseEntity<APIResponse> getAppointmentByNo(@PathVariable String appointmentNo){
        try {
            Appointment appointment = appointmentService.getAppointmentByNo(appointmentNo);
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.RESOURCE_FOUND, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_APPOINTMENT)
    public ResponseEntity<APIResponse> deleteAppointmentById(@PathVariable Long id){
        try{
            appointmentService.deleteAppointmentById(id);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.DELETE_SUCCESS, null));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_APPOINTMENT)
    public ResponseEntity<APIResponse> updateAppointment(@PathVariable Long id, @RequestBody AppointmentUpdateRequest appointmentUpdateRequest){
        try{
            Appointment appointment = appointmentService.updateAppointment(id, appointmentUpdateRequest);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.UPDATE_SUCCESS, appointment));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (IllegalStateException e){
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        }
    }
}
