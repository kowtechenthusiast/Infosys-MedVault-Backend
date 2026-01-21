package com.medibook.medibook_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(
        name = "appointment",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"doctor_id", "slot_id"}
        ),
        indexes = {
                @Index(name = "idx_patient", columnList = "patient_id"),
                @Index(name = "idx_doctor_status", columnList = "doctor_id, status"),
                @Index(name = "idx_appointment_date", columnList = "appointment_date")
        }
)
public class Appointment {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties({"appointments"})
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private Doctor doctor;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private DoctorAvailability slot;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "reason")
    private String reason;

    Boolean rated;


    /* ================= SNAPSHOT (CRITICAL) ================= */
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    /* ================= STATUS ================= */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.REQUESTED;

    public enum Status {
        REQUESTED,   // patient booked
        CONFIRMED,   // doctor approved
        REJECTED,    // doctor rejected
        CANCELLED,   // either party cancelled
        COMPLETED    // after appointment time
    }

    /* ================= CANCELLATION INFO ================= */
    private LocalDateTime cancelledAt;

    @Column(length = 255)
    private String cancellationReason;

    /* ================= AUDIT ================= */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Boolean getRated() {
        return rated;
    }

    public void setRated(Boolean flag) {
        this.rated = flag;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public DoctorAvailability getSlot() {
        return slot;
    }

    public void setSlot(DoctorAvailability slot) {
        this.slot = slot;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
