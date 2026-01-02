package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.dto.AllowedDoctorDto;
import com.medibook.medibook_backend.dto.MedicalRecordAccessRequestDTO;
import com.medibook.medibook_backend.dto.MedicalRecordAccessRequestResponseDTO;
import com.medibook.medibook_backend.dto.MedicalRecordResponse;
import com.medibook.medibook_backend.entity.Doctor;
import com.medibook.medibook_backend.entity.MedicalRecord;
import com.medibook.medibook_backend.entity.MedicalRecordAccessRequest;
import com.medibook.medibook_backend.entity.Patient;
import com.medibook.medibook_backend.repository.DoctorRepository;
import com.medibook.medibook_backend.repository.MedicalRecordAccessRequestRepository;
import com.medibook.medibook_backend.repository.MedicalRecordRepository;
import com.medibook.medibook_backend.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final FileStorageService fileStorageService;
    private final MedicalRecordAccessRequestRepository accessRequestRepo;

    public MedicalRecordService(
            MedicalRecordRepository recordRepo,
            PatientRepository patientRepo,
            DoctorRepository doctorRepo,
            FileStorageService fileStorageService, MedicalRecordAccessRequestRepository accessRequestRepo
    ) {
        this.recordRepo = recordRepo;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.fileStorageService = fileStorageService;
        this.accessRequestRepo = accessRequestRepo;
    }

    /* ================= UPLOAD ================= */
    public void uploadRecord(Long patientId, MultipartFile file) {

        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        String path = fileStorageService.saveMedicalRecord(file);

        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setFileName(file.getOriginalFilename());
        record.setFilePath(path);

        recordRepo.save(record);
    }

    /* ================= PATIENT VIEW ================= */
    public List<MedicalRecordResponse> getPatientRecords(Long patientId) {

        return recordRepo.findByPatient_Id(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /* ================= DOCTOR VIEW ================= */
    public List<MedicalRecordResponse> getDoctorAccessibleRecords(Long doctorId) {

        return recordRepo.findRecordsAccessibleByDoctor(doctorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /* ================= DELETE RECORD ================= */
    public void deleteRecord(Long recordId) {

        MedicalRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // Optional but recommended: delete file from disk
        fileStorageService.deleteFile(record.getFilePath());

        recordRepo.delete(record);
    }

    /* ================= GRANT ACCESS ================= */
    public void grantDoctorAccess(Long recordId, Long doctorId) {

        MedicalRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        record.getAllowedDoctors().add(doctor);
    }

    /* ================= REVOKE ACCESS ================= */
    public void revokeDoctorAccess(Long recordId, Long doctorId) {

        MedicalRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        record.getAllowedDoctors()
                .removeIf(d -> d.getId().equals(doctorId));
    }

    /* ================= MAPPER ================= */
    private MedicalRecordResponse toResponse(MedicalRecord record) {

        MedicalRecordResponse dto = new MedicalRecordResponse();
        dto.setId(record.getId());
        dto.setFileName(record.getFileName());
        dto.setFileUrl("http://localhost:8080" + record.getFilePath());
        dto.setUploadDate(record.getUploadDate());

        dto.setAllowedDoctors(
                record.getAllowedDoctors()
                        .stream()
                        .map(d -> {
                            AllowedDoctorDto ad = new AllowedDoctorDto();
                            ad.setId(d.getId());
                            ad.setName(d.getUser().getName());
                            ad.setSpecialization(d.getSpecialization());
                            return ad;
                        })
                        .toList()
        );

        return dto;
    }



    @Transactional
    public void requestAccess(
            Long recordId,
            Long patientId,
            Long doctorId
    ) {

        MedicalRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // Ensure record belongs to patient
        if (!record.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Invalid patient for this record");
        }

        // Check doctor exists
        Doctor doctor = doctorRepo.findByUser_Id(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Already has access?
        boolean alreadyAllowed = record.getAllowedDoctors()
                .stream()
                .anyMatch(d -> d.getId().equals(doctorId));

        if (alreadyAllowed) {
            throw new RuntimeException("Doctor already has access");
        }

        // Already requested?
        if (accessRequestRepo.existsByRecord_IdAndDoctor_Id(recordId, doctorId)) {
            throw new RuntimeException("Access already requested");
        }

        MedicalRecordAccessRequest request =
                new MedicalRecordAccessRequest();

        request.setRecord(record);
        request.setDoctor(doctor);
        request.setPatient(record.getPatient());

        accessRequestRepo.save(request);
    }

    /* ================= LIST ACCESS REQUESTS (PATIENT) ================= */
    public List<MedicalRecordAccessRequestResponseDTO>
    getPatientAccessRequests(Long patientId) {

        return accessRequestRepo
                .findByPatient_IdOrderByRequestedAtDesc(patientId)
                .stream()
                .map(req -> {
                    MedicalRecordAccessRequestResponseDTO dto =
                            new MedicalRecordAccessRequestResponseDTO();

                    dto.setRequestId(req.getId());
                    dto.setRecordId(req.getRecord().getId());
                    dto.setFileName(req.getRecord().getFileName());

                    dto.setDoctorId(req.getDoctor().getId());
                    dto.setDoctorName(req.getDoctor().getUser().getName());
                    dto.setSpecialization(req.getDoctor().getSpecialization());

                    dto.setStatus(req.getStatus().name());
                    dto.setRequestedAt(req.getRequestedAt());

                    return dto;
                })
                .toList();
    }

    /* ================= APPROVE ACCESS ================= */
    @Transactional
    public void approveAccessRequest(Long requestId) {

        MedicalRecordAccessRequest request =
                accessRequestRepo.findById(requestId)
                        .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != MedicalRecordAccessRequest.Status.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        MedicalRecord record = request.getRecord();
        Doctor doctor = request.getDoctor();

        // ✅ Prevent duplicate entries
        boolean alreadyAllowed = record.getAllowedDoctors()
                .stream()
                .anyMatch(d -> d.getId().equals(doctor.getId()));

        if (!alreadyAllowed) {
            record.getAllowedDoctors().add(doctor);
        }

        // ✅ Update request status
        request.setStatus(MedicalRecordAccessRequest.Status.APPROVED);

        // ✅ Explicit saves (IMPORTANT)
        accessRequestRepo.save(request);
    }


    /* ================= REJECT ACCESS ================= */
    @Transactional
    public void rejectAccessRequest(Long requestId) {

        MedicalRecordAccessRequest request =
                accessRequestRepo.findById(requestId)
                        .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != MedicalRecordAccessRequest.Status.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        request.setStatus(MedicalRecordAccessRequest.Status.REJECTED);

        // ✅ Explicit save (recommended)
        accessRequestRepo.save(request);
    }


}
