package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.dto.DoctorReviewDTO;
import com.medibook.medibook_backend.entity.DoctorRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorRatingRepository extends JpaRepository<DoctorRating, Long> {

    List<DoctorRating> findByDoctor_Id(Long doctorId);

    boolean existsByAppointment_Id(Long appointmentId);

    @Query("""
    SELECT new com.medibook.medibook_backend.dto.DoctorReviewDTO(
        r.rating,
        r.review,

        a.id,
        a.appointmentDate,
        a.appointmentTime,
        a.status,
        a.reason,

        p.id,
        u.name,
        p.gender,
        p.city,

        r.createdAt
    )
    FROM DoctorRating r
    JOIN r.appointment a
    JOIN a.patient p
    JOIN p.user u
    WHERE r.doctor.id = :doctorId
    ORDER BY r.createdAt DESC
""")
    List<DoctorReviewDTO> findReviewsByDoctorId(Long doctorId);


}
