package com.medibook.medibook_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "doctor_day_off",
        uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "off_date"})
)
public class DoctorDayOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "off_date", nullable = false)
    private LocalDate offDate;

    public DoctorDayOff() {}

    public DoctorDayOff(Doctor doctor, LocalDate offDate) {
        this.doctor = doctor;
        this.offDate = offDate;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDate getOffDate() {
        return offDate;
    }
}
