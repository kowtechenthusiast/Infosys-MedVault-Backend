package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.dto.UpcomingAppointmentDTO;
import com.medibook.medibook_backend.entity.Appointment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @EntityGraph(attributePaths = {"patient", "patient.user"})
    Optional<Appointment> findWithPatientById(Long id);
    // Doctor dashboard (date-wise)
    List<Appointment> findByDoctorIdAndAppointmentDate(
            Long doctorId, LocalDate date);

    // Doctor owns appointment
    Optional<Appointment> findByIdAndDoctorId(
            Long appointmentId, Long doctorId);

    // Doctor dashboard (all)
    @Query("""
    SELECT a FROM Appointment a
    JOIN FETCH a.patient p
    WHERE a.doctor.id = :doctorId AND a.status = CONFIRMED
""")
    List<Appointment> findByDoctorIdWithPatient(@Param("doctorId") Long doctorId);

    @Query("""
    SELECT a FROM Appointment a
    JOIN FETCH a.patient p
    WHERE a.doctor.id = :doctorId AND a.status = REQUESTED
""")
    List<Appointment> findByDoctorIdWithPatientPending(@Param("doctorId") Long doctorId);
    Optional<Appointment> findByIdAndDoctor_Id(Long id, Long doctorId);
    List<Appointment> findByPatientId(Long patientId);

    boolean existsBySlot_Id(Long slotId);


    @Query("""
    SELECT new com.medibook.medibook_backend.dto.UpcomingAppointmentDTO(
        a.id,
        d.id,
        d.user.name,
        d.specialization,
        d.qualification,
        d.experience,
        d.clinicHospitalName,
        d.phone,
        d.city,
        d.state,
        d.consultationFee,
        d.averageRating,
        d.ratingCount,
        a.appointmentDate,
        a.appointmentTime,
        a.status,
        a.reason,
        a.rated,
        r.rating,
        r.review
    )
    FROM Appointment a
    JOIN a.doctor d
    LEFT JOIN com.medibook.medibook_backend.entity.DoctorRating r
           ON r.appointment = a
    WHERE a.patient.id = :userId
      AND a.status IN ('REQUESTED', 'CONFIRMED', 'REJECTED', 'COMPLETED')
    ORDER BY a.appointmentDate, a.appointmentTime
""")
    List<UpcomingAppointmentDTO> findUpcomingAppointments(@Param("userId") Long userId);




    Optional<Appointment> findByIdAndPatient_Id(Long id, Long patientId);
}
