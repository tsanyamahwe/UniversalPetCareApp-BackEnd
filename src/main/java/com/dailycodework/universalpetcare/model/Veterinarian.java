package com.dailycodework.universalpetcare.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "vet_id")
public class Veterinarian extends User{
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String specialization;
    @Column(length = 1000)
    private String bio;
}
