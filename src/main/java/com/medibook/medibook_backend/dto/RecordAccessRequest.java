package com.medibook.medibook_backend.dto;

public class RecordAccessRequest {
    private Long recordId;
    private Long doctorId;
    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

}
