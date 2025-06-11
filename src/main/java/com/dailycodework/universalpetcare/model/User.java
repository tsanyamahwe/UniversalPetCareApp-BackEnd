package com.dailycodework.universalpetcare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    @Column(name = "mobile")
    private String phoneNumber;
    private String email;
    private String password;
    private String userType;
    private boolean isEnabled;
    @CreationTimestamp
    private LocalDate createdAt;
    @Transient
    private String specialization;
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> patientAppointments;
    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> veterinarianAppointments;
    @Transient
    private List<Appointment> appointments;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Photo photo;

    public List<Appointment> getAppointments() {
        if(appointments == null){
            appointments = new ArrayList<>();
            if(patientAppointments != null){
                appointments.addAll(patientAppointments);
            }
            if(veterinarianAppointments != null){
                appointments.addAll(veterinarianAppointments);
            }
        }
        return appointments;
    }

    public void addPatientAppointment(Appointment appointment){
        if(patientAppointments == null){
            patientAppointments = new ArrayList<>();
        }
        patientAppointments.add(appointment);
        appointment.setPatient(this);
    }

    public void addVeterinarianAppointment(Appointment appointment){
        if(veterinarianAppointments == null){
            veterinarianAppointments = new ArrayList<>();
        }
        veterinarianAppointments.add(appointment);
        appointment.setVeterinarian(this);
    }

    @Override
    public String toString(){
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object){
        if(this == object) return  true;
        if(object == null || getClass() != object.getClass()) return  false;
        User user = (User)object;
        return id != null && id.equals(user.getId());
    }

    @Override
    public int hashCode(){
        return id != null ? id.hashCode(): 0;
    }
}
