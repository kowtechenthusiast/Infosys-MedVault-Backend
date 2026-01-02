package com.medibook.medibook_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "doctor_rating",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"doctor_id", "patient_id"}
        )
)
public class DoctorRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= RELATIONS ================= */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    /* ================= RATING ================= */
    @Column(nullable = false)
    private Integer rating; // 1–5

    @Column(nullable = false, updatable = false)
    private LocalDateTime ratedAt;

    @PrePersist
    void onCreate() {
        ratedAt = LocalDateTime.now();
    }

    /* ================= GETTERS & SETTERS ================= */
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
