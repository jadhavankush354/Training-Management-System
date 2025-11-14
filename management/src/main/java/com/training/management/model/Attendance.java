package com.training.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainee_id", nullable = false)
    private Long traineeId;

    @Column(name = "trainee_name")
    private String traineeName;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "batch_name")
    private String batchName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE
    }
}