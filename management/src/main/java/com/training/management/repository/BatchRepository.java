package com.training.management.repository;

import com.training.management.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByLocation(String location);
    List<Batch> findByTrainerId(Long trainerId);
}