package com.medibook.medibook_backend.dto;

import java.time.LocalDate;

public class CompletePatientProfileRequest {

    private Long userId;

    // Basic details
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;

    // Lifestyle fields
    private Integer sleepHours;
    private String diet;
    private String smoking;
    private String alcohol;

    // Health metrics
    private Integer sugarLevel;
    private Integer bpSys;
    private Integer bpDia;
    private Integer spo2;
    private Integer heartRate;

    // Password update (optional)
    private String password;

    // ID proof file path
    private String idProofPath;

    public CompletePatientProfileRequest() {}

    // Getters & Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

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

    public Integer getSleepHours() { return sleepHours; }
    public void setSleepHours(Integer sleepHours) { this.sleepHours = sleepHours; }

    public String getDiet() { return diet; }
    public void setDiet(String diet) { this.diet = diet; }

    public String getSmoking() { return smoking; }
    public void setSmoking(String smoking) { this.smoking = smoking; }

    public String getAlcohol() { return alcohol; }
    public void setAlcohol(String alcohol) { this.alcohol = alcohol; }

    public Integer getSugarLevel() { return sugarLevel; }
    public void setSugarLevel(Integer sugarLevel) { this.sugarLevel = sugarLevel; }

    public Integer getBpSys() { return bpSys; }
    public void setBpSys(Integer bpSys) { this.bpSys = bpSys; }

    public Integer getBpDia() { return bpDia; }
    public void setBpDia(Integer bpDia) { this.bpDia = bpDia; }

    public Integer getSpo2() { return spo2; }
    public void setSpo2(Integer spo2) { this.spo2 = spo2; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getIdProofPath() { return idProofPath; }
    public void setIdProofPath(String idProofPath) { this.idProofPath = idProofPath; }
}
