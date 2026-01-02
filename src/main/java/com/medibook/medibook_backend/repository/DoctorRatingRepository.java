package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.DoctorRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRatingRepository
        extends JpaRepository<DoctorRating, Long> {

    boolean existsByDoctor_IdAndPatient_Id(Long doctorId, Long patientId);

    Optional<DoctorRating> findByDoctor_IdAndPatient_Id(
            Long doctorId,
            Long patientId
    );
}
