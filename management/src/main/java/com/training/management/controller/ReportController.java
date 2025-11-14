package com.training.management.controller;

import com.training.management.service.BatchService;
import com.training.management.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReportController {

    private final TrainerService trainerService;
    private final BatchService batchService;

    @GetMapping("/trainer-occupancy")
    public ResponseEntity<List<Map<String, Object>>> getTrainerOccupancy() {
        // Mock data for now - you can enhance this with actual logic
        var trainers = trainerService.getAllTrainers();
        List<Map<String, Object>> occupancyData = trainers.stream()
                .map(trainer -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("trainerName", trainer.getName());
                    data.put("availableDays", 20);
                    data.put("occupiedDays", trainer.getAssigned() ? 15 : 0);
                    return data;
                })
                .toList();

        return ResponseEntity.ok(occupancyData);
    }

    @GetMapping("/batch-enrollments")
    public ResponseEntity<List<Map<String, Object>>> getBatchEnrollments() {
        var batches = batchService.getAllBatches();
        List<Map<String, Object>> enrollmentData = batches.stream()
                .map(batch -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("batchName", batch.getCourseName() + " - " + batch.getLocation());
                    data.put("enrolledCount", batch.getEnrolledCount());
                    return data;
                })
                .toList();

        return ResponseEntity.ok(enrollmentData);
    }

    @GetMapping("/attendance-trends")
    public ResponseEntity<List<Map<String, Object>>> getAttendanceTrends() {
        // Mock attendance trend data
        List<Map<String, Object>> trendData = List.of(
                Map.of("session", "Session 1", "attendanceRate", 92),
                Map.of("session", "Session 2", "attendanceRate", 88),
                Map.of("session", "Session 3", "attendanceRate", 85),
                Map.of("session", "Session 4", "attendanceRate", 90),
                Map.of("session", "Session 5", "attendanceRate", 87)
        );

        return ResponseEntity.ok(trendData);
    }

    @GetMapping("/attendance-trends/{batchId}")
    public ResponseEntity<List<Map<String, Object>>> getAttendanceTrendsByBatch(@PathVariable Long batchId) {
        // Mock data for specific batch
        List<Map<String, Object>> trendData = List.of(
                Map.of("session", "Session 1", "attendanceRate", 95),
                Map.of("session", "Session 2", "attendanceRate", 90),
                Map.of("session", "Session 3", "attendanceRate", 88)
        );

        return ResponseEntity.ok(trendData);
    }
}