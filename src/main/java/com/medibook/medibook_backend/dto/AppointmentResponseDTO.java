package com.medibook.medibook_backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentResponseDTO {

    private Long id;

    /* ================= PATIENT BASIC ================= */
    private Long patientId;
    private String patientName;
    private Integer patientAge;
    private String email;
    private String gender;
    private String bloodGroup;
    private String phone;

    /* ================= ADDRESS ================= */
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;

    /* ================= LIFESTYLE ================= */
    private Integer sleepHours;
    private String diet;
    private String smoking;
    private String alcohol;

    /* ================= HEALTH METRICS ================= */
    private Integer sugarLevel;
    private Integer bpSys;
    private Integer bpDia;
    private Integer spo2;
    private Integer heartRate;

    /* ================= APPOINTMENT ================= */
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reason;
    private String status;

    private LocalDateTime registrationDate;

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }


    public Integer getPatientAge() { return patientAge; }
    public void setPatientAge(Integer patientAge) { this.patientAge = patientAge; }

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

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
}
