package com.training.management.controller;

import com.training.management.model.Batch;
import com.training.management.model.Trainee;
import com.training.management.service.BatchService;
import com.training.management.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;
    private final TraineeService traineeService;

    @GetMapping
    public ResponseEntity<List<Batch>> getAllBatches() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Batch> getBatchById(@PathVariable Long id) {
        return batchService.getBatchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Batch> createBatch(@RequestBody Batch batch) {
        Batch savedBatch = batchService.createBatch(batch);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBatch);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Batch> getBatchDetails(@PathVariable Long id) {
        return batchService.getBatchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<Batch>> getBatchesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(batchService.getBatchesByLocation(location));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{batchId}/trainees")
    public ResponseEntity<List<Trainee>> getBatchTrainees(@PathVariable Long batchId) {
        return ResponseEntity.ok(traineeService.getTraineesByBatch(batchId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Batch> updateBatch(@PathVariable Long id, @RequestBody Batch batch) {
        batch.setId(id);
        Batch updatedBatch = batchService.updateBatch(batch);
        return ResponseEntity.ok(updatedBatch);
    }
}