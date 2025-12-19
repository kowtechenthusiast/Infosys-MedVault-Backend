package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.Doctor;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUserId(Long userId);

    boolean existsByUserId(Long userId);   // ✅ ADD THIS

    @Query("""
        SELECT d
        FROM Doctor d
        JOIN d.user u
        WHERE u.status = PENDING
        AND d.dateOfBirth IS NOT NULL
        AND d.gender IS NOT NULL
        AND d.phone IS NOT NULL
        AND d.medicalRegistrationNumber IS NOT NULL
        AND d.licensingAuthority IS NOT NULL
        AND d.specialization IS NOT NULL
        AND d.qualification IS NOT NULL
        AND d.experience IS NOT NULL
        AND d.city IS NOT NULL
        AND d.state IS NOT NULL
        AND d.pincode IS NOT NULL
        AND d.medicalLicensePath IS NOT NULL
        AND d.consultationFee IS NOT NULL
    """)
    List<Doctor> findAllPendingDoctorsWithBasicInfo();


    @Query("""
        SELECT d
        FROM Doctor d
        JOIN d.user u
        WHERE u.status = ACTIVE
        AND d.dateOfBirth IS NOT NULL
        AND d.gender IS NOT NULL
        AND d.phone IS NOT NULL
        AND d.medicalRegistrationNumber IS NOT NULL
        AND d.licensingAuthority IS NOT NULL
        AND d.specialization IS NOT NULL
        AND d.qualification IS NOT NULL
        AND d.experience IS NOT NULL
        AND d.city IS NOT NULL
        AND d.state IS NOT NULL
        AND d.pincode IS NOT NULL
        AND d.medicalLicensePath IS NOT NULL
        AND d.consultationFee IS NOT NULL
    """)
    List<Doctor> findAllDoctors();



}
