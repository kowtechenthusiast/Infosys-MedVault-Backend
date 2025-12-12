package com.medibook.medibook_backend.entity;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient")
public class Patient implements Persistable<Long> {

    @Id
    private Long id; // Same as user.id

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    // --------------------------
    // PERSONAL INFO
    // --------------------------
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;

    @Column(name = "blood_group")
    private String bloodGroup;

    private String phone;

    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;

    // --------------------------
    // DOCUMENT
    // --------------------------
    @Column(name = "id_proof_path")
    private String idProofPath;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    // --------------------------
    // LIFESTYLE INFO (NEW)
    // --------------------------
    private Integer sleepHours;   // avg daily sleep
    private String diet;          // vegetarian, etc.
    private String smoking;       // never, occasional, etc.
    private String alcohol;       // consumption habit

    // --------------------------
    // HEALTH METRICS (NEW)
    // --------------------------
    private Integer sugarLevel;   // mg/dL
    private Integer bpSys;        // systolic BP
    private Integer bpDia;        // diastolic BP
    private Integer spo2;         // oxygen %
    private Integer heartRate;    // BPM

    // --------------------------
    // Constructors
    // --------------------------
    public Patient() {}

    public Patient(User user) {
        this.user = user;
        this.id = user.getId();
    }

    // --------------------------
    // GETTERS + SETTERS
    // --------------------------
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;
        if (user != null) this.id = user.getId();
    }

    public LocalDate getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getPincode() { return pincode; }

    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getIdProofPath() { return idProofPath; }

    public void setIdProofPath(String idProofPath) {
        this.idProofPath = idProofPath;
    }

    public LocalDateTime getRegistrationDate() { return registrationDate; }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    // -------- LIFESTYLE ----------
    public Integer getSleepHours() { return sleepHours; }

    public void setSleepHours(Integer sleepHours) {
        this.sleepHours = sleepHours;
    }

    public String getDiet() { return diet; }

    public void setDiet(String diet) { this.diet = diet; }

    public String getSmoking() { return smoking; }

    public void setSmoking(String smoking) { this.smoking = smoking; }

    public String getAlcohol() { return alcohol; }

    public void setAlcohol(String alcohol) { this.alcohol = alcohol; }

    // -------- HEALTH METRICS ----------
    public Integer getSugarLevel() { return sugarLevel; }

    public void setSugarLevel(Integer sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public Integer getBpSys() { return bpSys; }

    public void setBpSys(Integer bpSys) { this.bpSys = bpSys; }

    public Integer getBpDia() { return bpDia; }

    public void setBpDia(Integer bpDia) { this.bpDia = bpDia; }

    public Integer getSpo2() { return spo2; }

    public void setSpo2(Integer spo2) { this.spo2 = spo2; }

    public Integer getHeartRate() { return heartRate; }

    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    // Persistable logic
    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() { return isNew; }

    @PostLoad
    @PostPersist
    void markNotNew() { this.isNew = false; }
}
