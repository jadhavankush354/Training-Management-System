package com.training.management.service;

import com.training.management.model.Trainer;
import com.training.management.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    public Optional<Trainer> getTrainerById(Long id) {
        return trainerRepository.findById(id);
    }

    public Trainer addTrainerAvailability(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    public List<Trainer> getAvailableTrainers() {
        return trainerRepository.findByAssignedFalse();
    }

    public List<Trainer> getTrainersByLocation(String location) {
        return trainerRepository.findByLocation(location);
    }

    public void deleteTrainer(Long id) {
        trainerRepository.deleteById(id);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerRepository.save(trainer);
    }
}