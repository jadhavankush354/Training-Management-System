package com.training.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "available_date")
    private LocalDate availableDate;

    @Column(name = "time_slot")
    private String timeSlot;

    @Column(name = "assigned")
    private Boolean assigned = false;

    @Column(name = "batch_id")
    private Long batchId;
}