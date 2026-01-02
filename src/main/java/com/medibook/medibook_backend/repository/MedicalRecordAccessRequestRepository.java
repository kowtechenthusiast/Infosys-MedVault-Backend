package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.MedicalRecordAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordAccessRequestRepository
        extends JpaRepository<MedicalRecordAccessRequest, Long> {

    boolean existsByRecord_IdAndDoctor_Id(Long recordId, Long doctorId);
    List<MedicalRecordAccessRequest> findByPatient_IdOrderByRequestedAtDesc(Long patientId);

}

