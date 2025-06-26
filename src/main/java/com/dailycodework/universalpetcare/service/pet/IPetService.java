package com.dailycodework.universalpetcare.service.pet;

import com.dailycodework.universalpetcare.model.Pet;

import java.util.List;

public interface IPetService {
    List<Pet> savePetForAppointment(List<Pet> pets);
    Pet updatePet(Pet pet, Long id);
    void deletePet(Long id);
    Pet getPetById(Long id);

    List<String> getPetTypes();

    List<String> getPetColors();

    List<String> getPetBreeds(String petType);
}
