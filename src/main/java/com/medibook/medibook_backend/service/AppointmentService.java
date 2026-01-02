package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.dto.AppointmentResponseDTO;
import com.medibook.medibook_backend.dto.UpcomingAppointmentDTO;
import com.medibook.medibook_backend.entity.*;
import com.medibook.medibook_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorDayOffRepository doctorDayOffRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorAvailabilityRepository availabilityRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorDayOffRepository doctorDayOffRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorDayOffRepository = doctorDayOffRepository;
    }

    // -------------------------------
    // PATIENT BOOKING
    // -------------------------------
    @Transactional
    public Appointment bookAppointment(Long patientId, Long doctorId, Long slotId, String reason) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // 🔐 Lock slot to prevent double booking (Pessimistic Locking)
        DoctorAvailability slot = availabilityRepository
                .findByIdForUpdate(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // ✅ Ensure slot belongs to doctor
        if (!slot.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Slot does not belong to this doctor");
        }

        // ❌ Check day-off
        if (doctorDayOffRepository
                .findByDoctor_IdAndDate(doctorId, slot.getSlotDate())
                .isPresent()) {
            throw new RuntimeException("Doctor not available on this date");
        }

        // ❌ Slot must be OPEN
        if (slot.getStatus() != DoctorAvailability.SlotStatus.OPEN) {
            throw new RuntimeException("Slot already booked or blocked");
        }

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSlot(slot);

        // ✅ Store the reason for the visit
        appointment.setReason(reason);

        // Snapshot (CRITICAL)
        appointment.setAppointmentDate(slot.getSlotDate());
        appointment.setAppointmentTime(slot.getStartTime());
        appointment.setStatus(Appointment.Status.REQUESTED);

        // Save appointment FIRST
        appointmentRepository.save(appointment);

        // 🔗 Link both sides + mark slot booked
        slot.setStatus(DoctorAvailability.SlotStatus.BOOKED);
        slot.setAppointment(appointment);

        return appointment;
    }

    // -------------------------------
    // DOCTOR CONFIRM
    // -------------------------------
    @Transactional
    public void confirmAppointment(Long doctorId, Long appointmentId) {

        Appointment appointment = appointmentRepository
                .findByIdAndDoctorId(appointmentId, doctorId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.Status.CONFIRMED);
    }

    // -------------------------------
    // DOCTOR REJECT
    // -------------------------------
    @Transactional
    public void rejectAppointment(Long doctorId, Long appointmentId) {

        Appointment appointment = appointmentRepository
                .findByIdAndDoctorId(appointmentId, doctorId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.Status.REJECTED);

        DoctorAvailability slot = appointment.getSlot();

        // Re-open slot safely
        if (slot.getStatus() == DoctorAvailability.SlotStatus.BOOKED) {
            slot.setStatus(DoctorAvailability.SlotStatus.OPEN);
            slot.setAppointment(null);
        }
    }

    // -------------------------------
    // LISTING
    // -------------------------------
    public List<AppointmentResponseDTO> getDoctorAppointments(Long doctorId) {

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdWithPatient(doctorId);

        return appointments.stream().map(a -> {
            Patient p = a.getPatient();

            AppointmentResponseDTO dto = new AppointmentResponseDTO();

            /* ================= APPOINTMENT ================= */
            dto.setId(a.getId());
            dto.setAppointmentDate(a.getAppointmentDate());
            dto.setAppointmentTime(a.getAppointmentTime());
            dto.setReason(a.getReason());
            dto.setStatus(a.getStatus().name());

            /* ================= PATIENT BASIC ================= */
            dto.setPatientId(p.getId());
            dto.setPatientName(p.getUser().getName());
            dto.setEmail(p.getUser().getEmail());
            dto.setGender(p.getGender());
            dto.setBloodGroup(p.getBloodGroup());
            dto.setPhone(p.getPhone());

            dto.setPatientAge(
                    p.getDateOfBirth() != null
                            ? Period.between(p.getDateOfBirth(), LocalDate.now()).getYears()
                            : null
            );

            /* ================= ADDRESS ================= */
            dto.setAddress(p.getAddress());
            dto.setCity(p.getCity());
            dto.setState(p.getState());
            dto.setCountry(p.getCountry());
            dto.setPincode(p.getPincode());

            /* ================= LIFESTYLE ================= */
            dto.setSleepHours(p.getSleepHours());
            dto.setDiet(p.getDiet());
            dto.setSmoking(p.getSmoking());
            dto.setAlcohol(p.getAlcohol());

            /* ================= HEALTH METRICS ================= */
            dto.setSugarLevel(p.getSugarLevel());
            dto.setBpSys(p.getBpSys());
            dto.setBpDia(p.getBpDia());
            dto.setSpo2(p.getSpo2());
            dto.setHeartRate(p.getHeartRate());

            dto.setRegistrationDate(p.getRegistrationDate());

            return dto;
        }).toList();
    }

    public List<AppointmentResponseDTO> getPendingDoctorAppointments(Long doctorId) {

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdWithPatientPending(doctorId);

        return appointments.stream().map(a -> {
            Patient p = a.getPatient();

            AppointmentResponseDTO dto = new AppointmentResponseDTO();

            /* ================= APPOINTMENT ================= */
            dto.setId(a.getId());
            dto.setAppointmentDate(a.getAppointmentDate());
            dto.setAppointmentTime(a.getAppointmentTime());
            dto.setReason(a.getReason());
            dto.setStatus(a.getStatus().name());

            /* ================= PATIENT BASIC ================= */
            dto.setPatientId(p.getId());
            dto.setPatientName(p.getUser().getName());
            dto.setEmail(p.getUser().getEmail());
            dto.setGender(p.getGender());
            dto.setBloodGroup(p.getBloodGroup());
            dto.setPhone(p.getPhone());

            dto.setPatientAge(
                    p.getDateOfBirth() != null
                            ? Period.between(p.getDateOfBirth(), LocalDate.now()).getYears()
                            : null
            );

            /* ================= ADDRESS ================= */
            dto.setAddress(p.getAddress());
            dto.setCity(p.getCity());
            dto.setState(p.getState());
            dto.setCountry(p.getCountry());
            dto.setPincode(p.getPincode());

            /* ================= LIFESTYLE ================= */
            dto.setSleepHours(p.getSleepHours());
            dto.setDiet(p.getDiet());
            dto.setSmoking(p.getSmoking());
            dto.setAlcohol(p.getAlcohol());

            /* ================= HEALTH METRICS ================= */
            dto.setSugarLevel(p.getSugarLevel());
            dto.setBpSys(p.getBpSys());
            dto.setBpDia(p.getBpDia());
            dto.setSpo2(p.getSpo2());
            dto.setHeartRate(p.getHeartRate());

            dto.setRegistrationDate(p.getRegistrationDate());

            return dto;
        }).toList();
    }



    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }




        public List<UpcomingAppointmentDTO> getUpcomingAppointments(Long userId) {
            return appointmentRepository.findUpcomingAppointments(
                    userId,
                    LocalDate.now(),
                    LocalTime.now()
            );
        }



//
        @Transactional
        public void rescheduleAppointment(
                Long appointmentId,
                Long patientId,
                Long newSlotId,
                LocalDate newDate
        ) {
            /* ================= FETCH APPOINTMENT ================= */
            Appointment appointment = appointmentRepository
                    .findByIdAndPatient_Id(appointmentId, patientId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            if (appointment.getStatus() == Appointment.Status.CANCELLED
                    || appointment.getStatus() == Appointment.Status.COMPLETED) {
                throw new RuntimeException("Cannot reschedule this appointment");
            }

            /* ================= OLD SLOT ================= */
            DoctorAvailability oldSlot = appointment.getSlot();

            /* ================= NEW SLOT ================= */
            DoctorAvailability newSlot = availabilityRepository
                    .findById(newSlotId)
                    .orElseThrow(() -> new RuntimeException("Slot not found"));

            if (newSlot.getStatus() != DoctorAvailability.SlotStatus.OPEN) {
                throw new RuntimeException("Selected slot is not available");
            }

            /* ================= VALIDATE SAME DOCTOR ================= */
            if (!newSlot.getDoctor().getId().equals(appointment.getDoctor().getId())) {
                throw new RuntimeException("Slot does not belong to the same doctor");
            }

            /* ================= RELEASE OLD SLOT ================= */
            oldSlot.setStatus(DoctorAvailability.SlotStatus.OPEN);
            oldSlot.setAppointment(null);
            availabilityRepository.save(oldSlot);

            /* ================= ASSIGN NEW SLOT ================= */
            newSlot.setStatus(DoctorAvailability.SlotStatus.BOOKED);
            newSlot.setAppointment(appointment);
            availabilityRepository.save(newSlot);

            /* ================= UPDATE APPOINTMENT SNAPSHOT ================= */
            appointment.setSlot(newSlot);
            appointment.setAppointmentDate(newDate);
            appointment.setAppointmentTime(newSlot.getStartTime());

            // optional: reset status to REQUESTED if doctor must reconfirm
            appointment.setStatus(Appointment.Status.REQUESTED);

            appointmentRepository.save(appointment);
        }


    @Transactional
    public void approveAppointment(Long doctorId, Long appointmentId) {

        Appointment appt = appointmentRepository
                .findByIdAndDoctor_Id(appointmentId, doctorId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appt.getStatus() != Appointment.Status.REQUESTED) {
            throw new RuntimeException("Appointment already processed");
        }

        appt.setStatus(Appointment.Status.CONFIRMED);

        // Slot stays BOOKED
        DoctorAvailability slot = appt.getSlot();
        slot.setStatus(DoctorAvailability.SlotStatus.BOOKED);

        availabilityRepository.save(slot);
        appointmentRepository.save(appt);
    }

    /* ================= REJECT ================= */
    @Transactional
    public void rejectAppointment(
            Long doctorId,
            Long appointmentId,
            String reason
    ) {

        Appointment appt = appointmentRepository
                .findByIdAndDoctor_Id(appointmentId, doctorId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appt.getStatus() != Appointment.Status.REQUESTED) {
            throw new RuntimeException("Appointment already processed");
        }

        appt.setStatus(Appointment.Status.REJECTED);
        appt.setCancelledAt(LocalDateTime.now());
        appt.setCancellationReason(
                reason != null ? reason : "Rejected by doctor"
        );

        // Re-open slot
        DoctorAvailability slot = appt.getSlot();
        slot.setStatus(DoctorAvailability.SlotStatus.OPEN);

        availabilityRepository.save(slot);
        appointmentRepository.save(appt);
    }
}
