package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.util.Date;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Appointment findByAppointmentNo(String appointmentNo);
    Appointment findByAppointmentDate(Date date);
    Appointment findAppointmentByTime(Time time);
    Appointment findAppointmentByAllSpecs(Long id, String appointmentNo, Date date, Time time);
}
