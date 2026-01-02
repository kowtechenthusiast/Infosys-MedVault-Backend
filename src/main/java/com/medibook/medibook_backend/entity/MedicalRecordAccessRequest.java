package com.medibook.medibook_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "medical_record_access_request",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"record_id", "doctor_id"}
        )
)
public class MedicalRecordAccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private MedicalRecord record;

    @ManyToOne(optional = false)
    private Doctor doctor;

    @ManyToOne(optional = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private LocalDateTime requestedAt;

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    @PrePersist
    void onCreate() {
        requestedAt = LocalDateTime.now();
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MedicalRecord getRecord() {
        return record;
    }

    public void setRecord(MedicalRecord record) {
        this.record = record;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}
