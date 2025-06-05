package com.dailycodework.universalpetcare.service.appointment;

import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.request.AppointmentUpdateRequest;

import java.sql.Time;
import java.util.*;
import java.util.List;

public interface IAppointmentService {
    Appointment createAppointment(Appointment appointment, Long sender, Long recipient);
    List<Appointment> getAllAppointments();
    Appointment updateAppointment(Long id, AppointmentUpdateRequest appointmentRequest);
    void deleteAppointment(Long id);
    Appointment getAppointmentById(Long id);
    Appointment getAppointmentByNo(String appointmentNo);
    Appointment getAppointmentByDate(Date appointmentDate);
    Appointment getAppointmentByTime(Time appointmentTime);
    Appointment getAppointmentByAllSpecs(Long id, String appointmentNo, Date appointmentDate, Time appointmentTime);
}
