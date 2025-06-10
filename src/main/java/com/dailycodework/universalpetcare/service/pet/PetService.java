package com.dailycodework.universalpetcare.service.pet;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Pet;
import com.dailycodework.universalpetcare.repository.PetRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService implements IPetService{
    private final PetRepository petRepository;

    @Override
    public List<Pet> savePetForAppointment(List<Pet> pets) {
        return petRepository.saveAll(pets);
    }

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
        petRepository.findById(id).ifPresentOrElse(petRepository::delete,()-> { throw new ResourceNotFoundException(FeedBackMessage.NOT_FOUND);});
    }

    @Override
    public Pet getPetById(Long id) {
        return petRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));
    }
}
