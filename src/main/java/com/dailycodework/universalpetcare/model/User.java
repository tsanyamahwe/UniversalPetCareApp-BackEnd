package com.dailycodework.universalpetcare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
    @Transient
    private List<Review> reviews = new ArrayList<>();
    @Transient
    private List<Appointment> appointments = new ArrayList<>();
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> patientAppointments;
    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> veterinarianAppointments;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Photo photo;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<VerificationToken> verificationTokens = new ArrayList<>();
    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;
    @Column(name = "password_change_count")
    private Integer passwordChangeCount = 0;

    public void removeUserPhoto(){
        if(this.getPhoto() != null){
            this.setPhoto(null);
        }
    }

    public void updatePasswordChangeInfo() {
        this.lastPasswordChange = LocalDateTime.now();
        this.passwordChangeCount = (this.passwordChangeCount == null ? 0 : this.passwordChangeCount) + 1;
    }

    public boolean canChangePassword(){
        if(lastPasswordChange == null){
            return true;
        }
        return lastPasswordChange.isBefore(LocalDateTime.now().minusWeeks(2));
    }

    public long getDaysUntilPasswordChangeAllowed(){
        if(lastPasswordChange == null){
            return 0;
        }
        LocalDateTime allowedDate = lastPasswordChange.plusWeeks(2);
        if(allowedDate.isBefore(LocalDateTime.now())){
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDateTime.now(), allowedDate);
    }

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
