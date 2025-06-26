package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Pet;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.service.pet.IPetService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.PETS)
public class PetController {
    private final IPetService petService;

    @PostMapping(UrlMapping.SAVE_PETS_FOR_APPOINTMENTS)
    public ResponseEntity<APIResponse> savePets(@RequestBody List<Pet> pets){
        try {
            List<Pet> savedPets = petService.savePetForAppointment(pets);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.CREATE_SUCCESS, savedPets));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_PET_BY_ID)
    public ResponseEntity<APIResponse> getPetById(@PathVariable Long id){
        try{
            Pet pet = petService.getPetById(id);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, pet));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_PET_BY_ID)
    public ResponseEntity<APIResponse> deletePetById(@PathVariable Long id){
        try{
            petService.deletePet(id);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.PET_UPDATE)
    public ResponseEntity<APIResponse> updatePet(@PathVariable Long id, @RequestBody Pet pet){
        try {
            Pet thePet = petService.updatePet(pet, id);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.UPDATE_SUCCESS, thePet));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_PET_TYPES)
    public ResponseEntity<APIResponse> getAllPetTypes(){
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, petService.getPetTypes()));
    }

    @GetMapping(UrlMapping.GET_PET_COLORS)
    public ResponseEntity<APIResponse> getAllPetColors(){
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, petService.getPetColors()));
    }

    @GetMapping(UrlMapping.GET_PET_BREEDS)
    public ResponseEntity<APIResponse> getAllPetBreeds(@RequestParam String petType){
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, petService.getPetBreeds(petType)));
    }
}
