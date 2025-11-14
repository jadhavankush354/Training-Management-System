package com.training.management.repository;

import com.training.management.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    List<Trainer> findByLocation(String location);
    List<Trainer> findByAssignedFalse();
    List<Trainer> findByBatchId(Long batchId);
}