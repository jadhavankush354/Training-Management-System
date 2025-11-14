package com.training.management.controller;

import com.training.management.model.Trainee;
import com.training.management.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService traineeService;

    @GetMapping
    public ResponseEntity<List<Trainee>> getAllTrainees() {
        return ResponseEntity.ok(traineeService.getAllTrainees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trainee> getTraineeById(@PathVariable Long id) {
        return traineeService.getTraineeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/enroll")
    public ResponseEntity<Trainee> enrollTrainee(@RequestBody Trainee trainee) {
        Trainee savedTrainee = traineeService.enrollTrainee(trainee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTrainee);
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<Trainee>> getTraineesByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(traineeService.getTraineesByBatch(batchId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable Long id) {
        traineeService.deleteTrainee(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trainee> updateTrainee(@PathVariable Long id, @RequestBody Trainee trainee) {
        trainee.setId(id);
        Trainee updatedTrainee = traineeService.updateTrainee(trainee);
        return ResponseEntity.ok(updatedTrainee);
    }
}