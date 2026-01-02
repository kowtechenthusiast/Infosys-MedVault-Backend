package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.MedicalRecordAccessRequestDTO;
import com.medibook.medibook_backend.dto.MedicalRecordResponse;
import com.medibook.medibook_backend.dto.RecordAccessRequest;
import com.medibook.medibook_backend.service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    /* ================= UPLOAD ================= */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam Long patientId,
            @RequestPart MultipartFile file
    ) {
        service.uploadRecord(patientId, file);
        return ResponseEntity.ok(
                Map.of("success", true, "message", "Medical record uploaded")
        );
    }

    /* ================= PATIENT VIEW ================= */
    @GetMapping("/patient/{patientId}")
    public List<MedicalRecordResponse> patientRecords(
            @PathVariable Long patientId
    ) {
        return service.getPatientRecords(patientId);
    }

    /* ================= DOCTOR VIEW ================= */
    @GetMapping("/doctor/{doctorId}")
    public List<MedicalRecordResponse> doctorRecords(
            @PathVariable Long doctorId
    ) {
        return service.getDoctorAccessibleRecords(doctorId);
    }

    /* ================= DELETE RECORD ================= */
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Map<String, Object>> deleteRecord(
            @PathVariable Long recordId
    ) {
        service.deleteRecord(recordId);
        return ResponseEntity.ok(
                Map.of("success", true, "message", "Record deleted")
        );
    }

    /* ================= GRANT ACCESS (OPTIONAL / FUTURE) ================= */
    @PostMapping("/grant-access")
    public ResponseEntity<Void> grant(@RequestBody RecordAccessRequest req) {
        service.grantDoctorAccess(req.getRecordId(), req.getDoctorId());
        return ResponseEntity.ok().build();
    }

    /* ================= REVOKE ACCESS (USED BY UI) ================= */
    @PostMapping("/revoke-access")
    public ResponseEntity<Void> revoke(@RequestBody RecordAccessRequest req) {
        service.revokeDoctorAccess(req.getRecordId(), req.getDoctorId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request-access")
    public ResponseEntity<?> requestAccess(
            @RequestBody MedicalRecordAccessRequestDTO request
    ) {
        service.requestAccess(
                request.getRecordId(),
                request.getPatientId(),
                request.getDoctorId()
        );

        return ResponseEntity.ok().build();
    }
}
