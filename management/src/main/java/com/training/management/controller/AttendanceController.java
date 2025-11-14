package com.training.management.controller;

import com.training.management.model.Attendance;
import com.training.management.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(@RequestBody Attendance attendance) {
        Attendance savedAttendance = attendanceService.markAttendance(attendance);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAttendance);
    }

    @GetMapping("/trainee/{traineeId}")
    public ResponseEntity<List<Attendance>> getAttendanceByTrainee(@PathVariable Long traineeId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByTrainee(traineeId));
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<Attendance>> getAttendanceByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByBatch(batchId));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Attendance>> getAttendanceByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(localDate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable Long id, @RequestBody Attendance attendance) {
        attendance.setId(id);
        Attendance updatedAttendance = attendanceService.markAttendance(attendance);
        return ResponseEntity.ok(updatedAttendance);
    }
}