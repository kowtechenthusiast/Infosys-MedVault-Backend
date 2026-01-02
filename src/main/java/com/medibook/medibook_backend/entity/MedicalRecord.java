package com.medibook.medibook_backend.entity;

import com.medibook.medibook_backend.entity.Doctor;
import com.medibook.medibook_backend.entity.Patient;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medical_record")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Patient patient;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    private LocalDateTime uploadDate;

    /* Doctors who can access this document */
    @ManyToMany
    @JoinTable(
            name = "medical_record_access",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id")
    )
    private Set<Doctor> allowedDoctors = new HashSet<>();

    @PrePersist
    void onCreate() {
        uploadDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Set<Doctor> getAllowedDoctors() {
        return allowedDoctors;
    }

    public void setAllowedDoctors(Set<Doctor> allowedDoctors) {
        this.allowedDoctors = allowedDoctors;
    }

}
