package com.training.management.repository;

import com.training.management.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByTraineeId(Long traineeId);
    List<Attendance> findByBatchId(Long batchId);
    List<Attendance> findByDate(LocalDate date);
}