package com.medibook.medibook_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MedicalRecordResponse {

    private Long id;
    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadDate;

    // ✅ UPDATED: detailed doctor info
    private List<AllowedDoctorDto> allowedDoctors;

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public List<AllowedDoctorDto> getAllowedDoctors() {
        return allowedDoctors;
    }

    public void setAllowedDoctors(List<AllowedDoctorDto> allowedDoctors) {
        this.allowedDoctors = allowedDoctors;
    }
}
