package com.medibook.medibook_backend.dto;

public class BookingRequest {
    private Long patientId;
    private Long doctorId;
    private Long slotId;
    private String reason;

    // Standard Getters and Setters
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}