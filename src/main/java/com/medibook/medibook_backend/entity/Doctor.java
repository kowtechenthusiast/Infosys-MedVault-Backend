package com.medibook.medibook_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String gender;
    private Integer yearsOfExperience;
    private String specialization;
    private String contactPhone;

    @Column(unique = true, nullable = false)
    private String email;

    private String licenseDocumentPath;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = false)
    private boolean mustChangePassword = false;

    public Doctor() {
    }

    public Doctor(Long id, String fullName, String gender, Integer yearsOfExperience, String specialization,
            String contactPhone, String email, String licenseDocumentPath, String password, String status) {
        this.id = id;
        this.fullName = fullName;
        this.gender = gender;
        this.yearsOfExperience = yearsOfExperience;
        this.specialization = specialization;
        this.contactPhone = contactPhone;
        this.email = email;
        this.licenseDocumentPath = licenseDocumentPath;
        this.password = password;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicenseDocumentPath() {
        return licenseDocumentPath;
    }

    public void setLicenseDocumentPath(String licenseDocumentPath) {
        this.licenseDocumentPath = licenseDocumentPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}
