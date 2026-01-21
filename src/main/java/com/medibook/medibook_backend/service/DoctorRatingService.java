package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.dto.DoctorReviewDTO;
import com.medibook.medibook_backend.entity.*;
import com.medibook.medibook_backend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorRatingService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRatingRepository ratingRepository;

    public DoctorRatingService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            DoctorRatingRepository ratingRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.ratingRepository = ratingRepository;
    }

    /* ================= ADD RATING ================= */

    @Transactional
    public void addRating(
            Long appointmentId,
            Long patientId,
            int rating,
            String review
    ) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new IllegalStateException("Unauthorized rating attempt");
        }


        if (appointment.getRated()) {
            throw new IllegalStateException("Appointment already rated");
        }

        DoctorRating doctorRating = new DoctorRating();
        doctorRating.setDoctor(appointment.getDoctor());
        doctorRating.setPatient(appointment.getPatient().getUser());
        doctorRating.setAppointment(appointment);
        doctorRating.setRating(rating);
        doctorRating.setReview(review);
        doctorRating.validate();

        ratingRepository.save(doctorRating);

        updateDoctorAggregateRating(appointment.getDoctor(), rating);

        appointment.setRated(true);
    }

    /* ================= AGGREGATE UPDATE ================= */

    private void updateDoctorAggregateRating(Doctor doctor, int newRating) {
        int count = doctor.getRatingCount();
        double avg = doctor.getAverageRating();

        double total = avg * count;
        count++;

        doctor.setRatingCount(count);
        doctor.setAverageRating((total + newRating) / count);

        doctorRepository.save(doctor);
    }

    /* ================= GET REVIEWS ================= */

    public List<DoctorReviewDTO> getRatingsByDoctor(Long doctorId) {
        return ratingRepository.findReviewsByDoctorId(doctorId);
    }

}
