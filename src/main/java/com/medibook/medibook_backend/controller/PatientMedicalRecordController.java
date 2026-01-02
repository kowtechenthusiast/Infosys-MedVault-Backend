package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.MedicalRecordAccessRequestResponseDTO;
import com.medibook.medibook_backend.service.MedicalRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient/medical-records")
public class PatientMedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public PatientMedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    /* ================= LIST REQUESTS ================= */
    @GetMapping("/access-requests")
    public List<MedicalRecordAccessRequestResponseDTO> getAccessRequests(
            @RequestParam Long patientId
    ) {
        return medicalRecordService.getPatientAccessRequests(patientId);
    }

    /* ================= APPROVE ================= */
    @PutMapping("/access-requests/{id}/approve")
    public void approveRequest(@PathVariable Long id) {
        medicalRecordService.approveAccessRequest(id);
    }

    /* ================= REJECT ================= */
    @PutMapping("/access-requests/{id}/reject")
    public void rejectRequest(@PathVariable Long id) {
        medicalRecordService.rejectAccessRequest(id);
    }
}
