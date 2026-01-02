package com.medibook.medibook_backend.dto;

import com.medibook.medibook_backend.entity.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;

public class UpcomingAppointmentDTO {

    private Long id;

    /* ================= DOCTOR DETAILS ================= */
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private String qualification;
    private Integer experience;
    private String clinicHospitalName;
    private String phone;
    private String city;
    private String state;
    private Integer consultationFee;
    private Double averageRating;
    private Integer ratingCount;

    /* ================= APPOINTMENT ================= */
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Appointment.Status status;
    private String reason;

    public UpcomingAppointmentDTO(
            Long id,
            Long doctorId,
            String doctorName,
            String specialization,
            String qualification,
            Integer experience,
            String clinicHospitalName,
            String phone,
            String city,
            String state,
            Integer consultationFee,
            Double averageRating,
            Integer ratingCount,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            Appointment.Status status,
            String reason
    ) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.clinicHospitalName = clinicHospitalName;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.consultationFee = consultationFee;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.reason = reason;
    }

    /* ================= GETTERS ================= */

    public Long getId() { return id; }
    public Long getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getSpecialization() { return specialization; }
    public String getQualification() { return qualification; }
    public Integer getExperience() { return experience; }
    public String getClinicHospitalName() { return clinicHospitalName; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public Integer getConsultationFee() { return consultationFee; }
    public Double getAverageRating() { return averageRating; }
    public Integer getRatingCount() { return ratingCount; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public Appointment.Status getStatus() { return status; }
    public String getReason() { return reason; }
}
