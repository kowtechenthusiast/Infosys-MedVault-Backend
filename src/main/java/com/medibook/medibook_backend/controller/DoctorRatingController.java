package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.DoctorReviewDTO;
import com.medibook.medibook_backend.entity.DoctorRating;
import com.medibook.medibook_backend.service.DoctorRatingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class DoctorRatingController {

    private final DoctorRatingService ratingService;

    public DoctorRatingController(DoctorRatingService ratingService) {
        this.ratingService = ratingService;
    }

    /* ================= ADD RATING ================= */

    @PostMapping
    public void rateDoctor(
            @RequestParam Long appointmentId,
            @RequestParam int rating,
            @RequestParam(required = false) String review,
            @RequestParam Long patientId   // 🔒 Replace with SecurityContext later
    ) {
        ratingService.addRating(appointmentId, patientId, rating, review);
    }

    /* ================= GET RATINGS ================= */

    @GetMapping("/doctor/{doctorId}")
    public List<DoctorReviewDTO> getDoctorRatings(@PathVariable Long doctorId) {
        return ratingService.getRatingsByDoctor(doctorId);
    }

}
