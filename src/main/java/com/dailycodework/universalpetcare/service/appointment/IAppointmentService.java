package com.dailycodework.universalpetcare.service.appointment;

import com.dailycodework.universalpetcare.dto.AppointmentDTO;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.request.AppointmentUpdateRequest;
import com.dailycodework.universalpetcare.request.BookAppointmentRequest;

import java.sql.Time;
import java.util.*;
import java.util.List;

public interface IAppointmentService {
    Appointment createAppointment(BookAppointmentRequest bookAppointmentRequest, Long sender, Long recipient);
    List<Appointment> getAllAppointments();
    Appointment updateAppointment(Long id, AppointmentUpdateRequest appointmentRequest);
    void deleteAppointmentById(Long id);
    Appointment getAppointmentById(Long id);
    Appointment getAppointmentByNo(String appointmentNo);

    List<AppointmentDTO> getUserAppointments(Long userId);

    Appointment cancelAppointment(Long appointmentId);

    Appointment approveAppointment(Long appointmentId);

    Appointment declineAppointment(Long appointmentId);
//    Appointment getAppointmentByDate(Date appointmentDate);
//    Appointment getAppointmentByTime(Time appointmentTime);
//    Appointment getAppointmentByAllSpecs(Long id, String appointmentNo, Date appointmentDate, Time appointmentTime);
}
