package com.training.management.service;

import com.training.management.model.Batch;
import com.training.management.model.Trainer;
import com.training.management.repository.BatchRepository;
import com.training.management.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final TrainerRepository trainerRepository;

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public Optional<Batch> getBatchById(Long id) {
        return batchRepository.findById(id);
    }

    public Batch createBatch(Batch batch) {
        // Assign trainer and update trainer status
        if (batch.getTrainerId() != null) {
            Optional<Trainer> trainer = trainerRepository.findById(batch.getTrainerId());
            if (trainer.isPresent()) {
                Trainer t = trainer.get();
                batch.setTrainerName(t.getName());

                // Mark trainer as assigned
                t.setAssigned(true);
                t.setBatchId(batch.getId());
                trainerRepository.save(t);
            }
        }

        return batchRepository.save(batch);
    }

    public List<Batch> getBatchesByLocation(String location) {
        return batchRepository.findByLocation(location);
    }

    public void deleteBatch(Long id) {
        batchRepository.deleteById(id);
    }

    public Batch updateBatch(Batch batch) {
        // Update trainer assignment if changed
        if (batch.getTrainerId() != null) {
            Optional<Trainer> trainer = trainerRepository.findById(batch.getTrainerId());
            if (trainer.isPresent()) {
                Trainer t = trainer.get();
                batch.setTrainerName(t.getName());
            }
        }
        return batchRepository.save(batch);
    }
}