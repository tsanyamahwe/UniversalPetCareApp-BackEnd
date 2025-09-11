package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.event.listener.AppointmentApprovedEvent;
import com.dailycodework.universalpetcare.event.listener.AppointmentBookedEvent;
import com.dailycodework.universalpetcare.event.listener.AppointmentDeclinedEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.APPOINTMENTS)
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final IPetService petService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @GetMapping(UrlMapping.ALL_APPOINTMENTS)
    public ResponseEntity<APIResponse> getAllAppointments(){
        try{
            List<Appointment> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.APPOINTMENTS_FOUND, appointments));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PostMapping(UrlMapping.BOOK_APPOINTMENT)
    public ResponseEntity<APIResponse> bookAppointment(@RequestBody BookAppointmentRequest bookAppointmentRequest, @RequestParam Long senderId, @RequestParam Long recipientId){
        try{
            Appointment theAppointment = appointmentService.createAppointment(bookAppointmentRequest, senderId, recipientId);
            applicationEventPublisher.publishEvent(new AppointmentBookedEvent(theAppointment));
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_CREATED, theAppointment));
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
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.APPOINTMENT_FOUND_BY_ID, appointment));
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
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.APPOINTMENT_FOUND_BY_NO, appointment));
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
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_DELETED, null));
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
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_UPDATED, appointment));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (IllegalStateException e){
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.CANCEL_APPOINTMENT)
    public ResponseEntity<APIResponse> cancelAppointment(@PathVariable Long id){
        try {
            Appointment appointment = appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_CANCELLED, appointment));
        } catch (IllegalStateException e) {
            return  ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.APPROVE_APPOINTMENT)
    public ResponseEntity<APIResponse> approveAppointment(@PathVariable Long id){
        try {
            Appointment appointment = appointmentService.approveAppointment(id);
            applicationEventPublisher.publishEvent(new AppointmentApprovedEvent(appointment));
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_APPROVED, appointment));
        } catch (IllegalStateException e) {
            return  ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.DECLINE_APPOINTMENT)
    public ResponseEntity<APIResponse> declineAppointment(@PathVariable Long id){
        try {
            Appointment appointment = appointmentService.declineAppointment(id);
            applicationEventPublisher.publishEvent(new AppointmentDeclinedEvent(appointment));
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_DECLINED , appointment));
        } catch (IllegalStateException e) {
            return  ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.COUNT_APPOINTMENTS)
    public long countAppointments(){
        return appointmentService.countAppointments();
    }

    @GetMapping(UrlMapping.APPOINTMENT_SUMMARY)
    public ResponseEntity<APIResponse> getAppointmentSummary(){
        try {
            List<Map<String, Object>> summary = appointmentService.getAppointmentStatusSummary();
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_SUMMARY, summary));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(FeedBackMessage.APPOINTMENT_ERROR_SUMMARY+e.getMessage(), null));
        }
    }
}
