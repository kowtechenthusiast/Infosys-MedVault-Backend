package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatient_Id(Long patientId);

    @Query("""
        SELECT r FROM MedicalRecord r
        JOIN r.allowedDoctors d
        WHERE d.id = :doctorId
    """)
    List<MedicalRecord> findRecordsAccessibleByDoctor(Long doctorId);
}
