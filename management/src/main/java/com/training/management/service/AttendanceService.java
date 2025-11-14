package com.training.management.service;

import com.training.management.model.Attendance;
import com.training.management.model.Batch;
import com.training.management.model.Trainee;
import com.training.management.repository.AttendanceRepository;
import com.training.management.repository.BatchRepository;
import com.training.management.repository.TraineeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final TraineeRepository traineeRepository;
    private final BatchRepository batchRepository;

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Attendance markAttendance(Attendance attendance) {
        // Get trainee and batch details
        if (attendance.getTraineeId() != null) {
            Optional<Trainee> trainee = traineeRepository.findById(attendance.getTraineeId());
            trainee.ifPresent(t -> attendance.setTraineeName(t.getName()));
        }

        if (attendance.getBatchId() != null) {
            Optional<Batch> batch = batchRepository.findById(attendance.getBatchId());
            batch.ifPresent(b -> attendance.setBatchName(b.getCourseName()));
        }

        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceByTrainee(Long traineeId) {
        return attendanceRepository.findByTraineeId(traineeId);
    }

    public List<Attendance> getAttendanceByBatch(Long batchId) {
        return attendanceRepository.findByBatchId(batchId);
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}