package com.training.management.controller;

import com.training.management.model.Trainer;
import com.training.management.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers() {
        return ResponseEntity.ok(trainerService.getAllTrainers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trainer> getTrainerById(@PathVariable Long id) {
        return trainerService.getTrainerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/availability")
    public ResponseEntity<Trainer> addTrainerAvailability(@RequestBody Trainer trainer) {
        Trainer savedTrainer = trainerService.addTrainerAvailability(trainer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTrainer);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Trainer>> getAvailableTrainers() {
        return ResponseEntity.ok(trainerService.getAvailableTrainers());
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<Trainer>> getTrainersByLocation(@PathVariable String location) {
        return ResponseEntity.ok(trainerService.getTrainersByLocation(location));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trainer> updateTrainer(@PathVariable Long id, @RequestBody Trainer trainer) {
        trainer.setId(id);
        Trainer updatedTrainer = trainerService.updateTrainer(trainer);
        return ResponseEntity.ok(updatedTrainer);
    }
}