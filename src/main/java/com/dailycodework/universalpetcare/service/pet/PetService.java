package com.dailycodework.universalpetcare.service.pet;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Pet;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.PetRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PetService implements IPetService{
    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<Pet> savePetForAppointment(List<Pet> pets) {return petRepository.saveAll(pets);}

    @Override
    public Pet updatePet(Pet pet, Long id) {
        Pet existingPet = getPetById(id);
        existingPet.setName(pet.getName());
        existingPet.setType(pet.getType());
        existingPet.setColor(pet.getColor());
        existingPet.setBreed(pet.getBreed());
        existingPet.setAge(pet.getAge());
        return petRepository.save(existingPet);
    }

    @Override
    public void deletePet(Long id) {
        Pet petToDelete = petRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));
        Appointment appointment = petToDelete.getAppointment();
        if (appointment != null) {
            long petCount = appointment.getPets().size();
            if (petCount <= 1) {
                throw new IllegalStateException(FeedBackMessage.CAN_NOT_DELETE);
            }
        }
        petRepository.delete(petToDelete);
    }

    @Override
    public Pet getPetById(Long id) {
        return petRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));
    }

    @Override
    public List<String> getPetTypes(){
        return petRepository.getDistinctPetTypes();
    }

    @Override
    public List<String> getPetColors(){
        return petRepository.getDistinctPetColors();
    }

    @Override
    public List<String> getPetBreeds(String petType){
        return petRepository.getDistinctPetBreedsByPetType(petType);
    }

    @Override
    public Pet addPetToExistingAppointment(Long appointmentId, Pet pet) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException(FeedBackMessage.NOT_AVAILABLE));
        validateSinglePet(pet);
        pet.setAppointment(appointment);
        return petRepository.save(pet);
    }

    private void validateSinglePet(Pet pet) {
        if (pet == null) {throw new IllegalArgumentException(FeedBackMessage.CAN_NOT_BE_NULL);}
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {throw new IllegalArgumentException(FeedBackMessage.NAME_REQUIRED);}
        if (pet.getType() == null || pet.getType().trim().isEmpty()) {throw new IllegalArgumentException(FeedBackMessage.TYPE_REQUIRED);}
        if (pet.getBreed() == null || pet.getBreed().trim().isEmpty()) {throw new IllegalArgumentException(FeedBackMessage.BREED_REQUIRED);}
        if (pet.getColor() == null || pet.getColor().trim().isEmpty()) {throw new IllegalArgumentException(FeedBackMessage.COLOR_REQUIRED);}
        if (Objects.isNull(pet.getAge()) || pet.getAge() <= 0) {throw new IllegalArgumentException(FeedBackMessage.AGE_REQUIREMENT);}
    }

}
