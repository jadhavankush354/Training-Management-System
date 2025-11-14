package com.training.management.service;

import com.training.management.model.Batch;
import com.training.management.model.Trainee;
import com.training.management.repository.BatchRepository;
import com.training.management.repository.TraineeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TraineeService {

    private final TraineeRepository traineeRepository;
    private final BatchRepository batchRepository;

    public List<Trainee> getAllTrainees() {
        return traineeRepository.findAll();
    }

    public Optional<Trainee> getTraineeById(Long id) {
        return traineeRepository.findById(id);
    }

    public Trainee enrollTrainee(Trainee trainee) {
        // Get batch details and update trainee
        if (trainee.getBatchId() != null) {
            Optional<Batch> batch = batchRepository.findById(trainee.getBatchId());
            if (batch.isPresent()) {
                Batch b = batch.get();
                trainee.setBatchName(b.getCourseName());
                trainee.setLocation(b.getLocation());

                // Update batch enrolled count
                b.setEnrolledCount(b.getEnrolledCount() + 1);
                batchRepository.save(b);
            }
        }

        return traineeRepository.save(trainee);
    }

    public List<Trainee> getTraineesByBatch(Long batchId) {
        return traineeRepository.findByBatchId(batchId);
    }

    public void deleteTrainee(Long id) {
        traineeRepository.deleteById(id);
    }

    public Trainee updateTrainee(Trainee trainee) {
        // Get batch details and update trainee
        if (trainee.getBatchId() != null) {
            Optional<Batch> batch = batchRepository.findById(trainee.getBatchId());
            if (batch.isPresent()) {
                Batch b = batch.get();
                trainee.setBatchName(b.getCourseName());
                trainee.setLocation(b.getLocation());
            }
        }
        return traineeRepository.save(trainee);
    }
}