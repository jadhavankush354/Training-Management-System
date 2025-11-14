package com.training.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name", nullable = false, unique = true)
    private String courseName;

    @Column(name = "description")
    private String description;

    @Column(name = "duration_weeks")
    private Integer durationWeeks;

    @Column(name = "category")
    private String category;
}