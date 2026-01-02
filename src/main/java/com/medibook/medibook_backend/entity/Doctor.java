package com.medibook.medibook_backend.entity;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor")
public class Doctor implements Persistable<Long> {

    /* ================= PRIMARY KEY ================= */
    @Id
    private Long id; // Same as user.id

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    /* ================= PERSONAL INFO ================= */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;

    /* ================= CONTACT ================= */
    private String phone;

    /* ================= PROFESSIONAL INFO ================= */
    @Column(name = "medical_registration_number", unique = true)
    private String medicalRegistrationNumber;

    @Column(name = "licensing_authority")
    private String licensingAuthority;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "qualification")
    private String qualification;

    private Integer experience; // years

    /* ================= CLINIC INFO ================= */
    @Column(name = "clinic_hospital_name")
    private String clinicHospitalName;

    private String city;
    private String state;
    private String country;
    private String pincode;

    /* ================= DOCUMENTS ================= */
    @Column(name = "medical_license_path")
    private String medicalLicensePath;

    @Column(name = "consultationFee")
    private Integer consultationFee;

    /* ================= RATING ================= */
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    /* ================= STATUS ================= */
    @Column(name = "acceptingAppointments")
    private Boolean acceptingAppointments = true;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /* ================= JPA STATE ================= */
    @Transient
    private boolean isNew = true;

    /* ================= CONSTRUCTORS ================= */
    public Doctor() {}

    public Doctor(User user) {
        this.user = user;
        this.id = user.getId();
    }

    /* ================= PERSISTABLE ================= */
    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    /* ================= BUSINESS LOGIC ================= */

    public void addRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (this.ratingCount == null) {
            this.ratingCount = 0;
        }

        if (this.averageRating == null) {
            this.averageRating = 0.0;
        }

        double total = this.averageRating * this.ratingCount;
        this.ratingCount += 1;
        this.averageRating = (total + rating) / this.ratingCount;
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) this.id = user.getId();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMedicalRegistrationNumber() {
        return medicalRegistrationNumber;
    }

    public void setMedicalRegistrationNumber(String medicalRegistrationNumber) {
        this.medicalRegistrationNumber = medicalRegistrationNumber;
    }

    public String getLicensingAuthority() {
        return licensingAuthority;
    }

    public void setLicensingAuthority(String licensingAuthority) {
        this.licensingAuthority = licensingAuthority;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getClinicHospitalName() {
        return clinicHospitalName;
    }

    public void setClinicHospitalName(String clinicHospitalName) {
        this.clinicHospitalName = clinicHospitalName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getMedicalLicensePath() {
        return medicalLicensePath;
    }

    public void setMedicalLicensePath(String medicalLicensePath) {
        this.medicalLicensePath = medicalLicensePath;
    }

    public Integer getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Integer consultationFee) {
        this.consultationFee = consultationFee;
    }

    public Double getAverageRating() {
        return averageRating;
    }

//    public void setAverageRating(Double averageRating) {
//        this.averageRating = averageRating;
//    }

    public Integer getRatingCount() {
        return ratingCount;
    }

//    public void setRatingCount(Integer ratingCount) {
//        this.ratingCount = ratingCount;
//    }

}
