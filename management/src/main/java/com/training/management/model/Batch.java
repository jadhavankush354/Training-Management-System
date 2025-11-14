package com.training.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(nullable = false)
    private String location;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "trainer_id")
    private Long trainerId;

    @Column(name = "trainer_name")
    private String trainerName;

    @Column(name = "max_capacity")
    private Integer maxCapacity = 30;

    @Column(name = "enrolled_count")
    private Integer enrolledCount = 0;

    @Column(name = "status")
    private String status = "Active";
}