package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.dto.*;
import com.medibook.medibook_backend.entity.*;
import com.medibook.medibook_backend.repository.*;
import com.medibook.medibook_backend.security.JwtService;
import jakarta.transaction.Transactional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, OtpRepository otpRepository,
            PatientRepository patientRepository, DoctorRepository doctorRepository,
            AdminRepository adminRepository, EmailService emailService,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.adminRepository = adminRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public Map<String, Object> setPassword(SetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != VerificationStatus.ACTIVE) {
            throw new RuntimeException("Email not verified");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(VerificationStatus.ACTIVE);
        userRepository.save(user);

        // ✅ Create role-specific entity
        if (user.getRole() == User.Role.PATIENT &&
                !patientRepository.existsByUserId(user.getId())) {
            patientRepository.save(new Patient(user));
        }

        if (user.getRole() == User.Role.DOCTOR &&
                !doctorRepository.existsByUserId(user.getId())) {
            doctorRepository.save(new Doctor(user));
        }

        return Map.of(
                "success", true,
                "message", "Account created successfully"
        );
    }



    /**
     * Generate OTP for registration
     */
    @Transactional
    public Map<String, Object> generateOtp(GenerateOtpRequest request) {

        User.Role role = User.Role.valueOf(request.getRole().toUpperCase());

        // ❗ Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Generate OTP
        String otpCode = String.format("%05d",
                new Random().nextInt(90000) + 10000);

        // Delete previous OTPs for same email + role
        otpRepository.deleteByEmailAndRole(request.getEmail(), role);

        // Save OTP
        Otp otp = new Otp();
        otp.setEmail(request.getEmail());
        otp.setRole(role);
        otp.setOtpCode(otpCode);
        otp.setUsed(false);
        otp.setCreatedAt(LocalDateTime.now());
        otpRepository.save(otp);

        // Send email
        emailService.sendOtpEmail(
                request.getEmail(),
                request.getName(),
                otpCode
        );

        return Map.of(
                "success", true,
                "message", "OTP sent successfully"
        );
    }

    /**
     * Verify OTP
     */
    @Transactional
    public Map<String, Object> verifyOtp(VerifyOtpRequest request) {

        User.Role role = User.Role.valueOf(request.getRole().toUpperCase());

        Otp otp = otpRepository
                .findTopByEmailAndRoleAndUsedFalseOrderByCreatedAtDesc(
                        request.getEmail(), role
                )
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));

        if (otp.isExpired() || !otp.getOtpCode().equals(request.getOtp())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        otp.setUsed(true);
        otpRepository.save(otp);

        return Map.of(
                "success", true,
                "message", "OTP verified"
        );
    }

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {

        User.Role role = User.Role.valueOf(request.getRole().toUpperCase());

        // Ensure OTP was verified
        if (!otpRepository.existsByEmailAndRoleAndUsedTrue(
                request.getEmail(), role)) {
            throw new RuntimeException("Email not verified");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                role
        );

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(VerificationStatus.PENDING);
        userRepository.save(user);

        // Create role-specific row
        if (role == User.Role.PATIENT) {
            patientRepository.save(new Patient(user));
        } else if (role == User.Role.DOCTOR) {
            doctorRepository.save(new Doctor(user));
        }

        Map<String, Object> response = new HashMap<>();

        response.put("role", user.getRole().name());
        response.put("userId", user.getId());
        response.put("success", true);
        response.put("message", "Registration successful");

        return response;
    }



    /**
     * Complete Patient Profile
     */
    @Transactional
    public Map<String, Object> completePatientProfile(CompletePatientProfileRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.Role.PATIENT) {
            throw new RuntimeException("User is not a patient");
        }

        Patient patient = patientRepository.findById(user.getId()).orElse(null);

        if (patient == null) {
            patient = new Patient();
            patient.setUser(user);
            patient.setId(user.getId());
        }

        // Update Basic Details
        if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getBloodGroup() != null) patient.setBloodGroup(request.getBloodGroup());
        if (request.getPhone() != null) patient.setPhone(request.getPhone());
        if (request.getAddress() != null) patient.setAddress(request.getAddress());
        if (request.getCity() != null) patient.setCity(request.getCity());
        if (request.getState() != null) patient.setState(request.getState());
        if (request.getCountry() != null) patient.setCountry(request.getCountry());
        if (request.getPincode() != null) patient.setPincode(request.getPincode());

        // Update Lifestyle
        if (request.getSleepHours() != null) patient.setSleepHours(request.getSleepHours());
        if (request.getDiet() != null) patient.setDiet(request.getDiet());
        if (request.getSmoking() != null) patient.setSmoking(request.getSmoking());
        if (request.getAlcohol() != null) patient.setAlcohol(request.getAlcohol());

        // Update Health Metrics
        if (request.getSugarLevel() != null) patient.setSugarLevel(request.getSugarLevel());
        if (request.getBpSys() != null) patient.setBpSys(request.getBpSys());
        if (request.getBpDia() != null) patient.setBpDia(request.getBpDia());
        if (request.getSpo2() != null) patient.setSpo2(request.getSpo2());
        if (request.getHeartRate() != null) patient.setHeartRate(request.getHeartRate());

        // Update ID Proof Path only if a new file was uploaded
        if (request.getIdProofPath() != null) {
            patient.setIdProofPath(request.getIdProofPath());
        }

        patientRepository.save(patient);

        return Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "profileCompleted", true
        );
    }

    /**
     * Complete Doctor Profile
     */
    @Transactional
    public Map<String, Object> completeDoctorProfile(CompleteDoctorProfileRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.Role.DOCTOR) {
            throw new RuntimeException("User is not a doctor");
        }

        Doctor doctor = doctorRepository.findById(user.getId()).orElse(null);

        if (doctor == null) {
            doctor = new Doctor(user);
        }

        doctor.setDateOfBirth(request.getDateOfBirth());
        doctor.setGender(request.getGender());
        doctor.setMedicalRegistrationNumber(request.getMedicalRegistrationNumber());
        doctor.setLicensingAuthority(request.getLicensingAuthority());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setQualification(request.getQualification());
        doctor.setExperience(request.getExperience());
        doctor.setPhone(request.getPhone());
        doctor.setClinicHospitalName(request.getClinicHospitalName());
        doctor.setCity(request.getCity());
        doctor.setState(request.getState());
        doctor.setCountry(request.getCountry());
        doctor.setPincode(request.getPincode());
        doctor.setConsultationFee(request.getConsultationFee());

        if (request.getMedicalLicensePath() != null) {
            doctor.setMedicalLicensePath(request.getMedicalLicensePath());
        }

        doctorRepository.save(doctor);

        return Map.of(
                "success", true,
                "message", "Doctor profile updated successfully"
        );
    }

    /**
     * Complete Admin Profile (Legacy method)
     */
    @Transactional
    public Map<String, Object> completeAdminProfile(CompleteAdminProfileRequest request, String proofUrl) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Create or update admin profile
        Admin admin = adminRepository.findById(user.getId()).orElse(null);

        boolean isNewAdmin = (admin == null);
        if (isNewAdmin) {
            admin = new Admin();
            admin.setUser(user);
            admin.setId(user.getId());
        }

        admin.setPhone(request.getPhone());
        admin.setDesignation(request.getDesignation());
        admin.setDepartment(request.getDepartment());
        admin.setProofUrl(proofUrl);

        userRepository.save(user);
        adminRepository.save(admin);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile saved. Waiting for admin verification.");

        return response;
    }

    /**
     * Register Admin (Invitation-based)
     * Creates admin user with temporary password and sends invitation email
     */
    @Transactional
    public Map<String, Object> registerAdmin(AdminRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Generate temporary password (will be changed during profile completion)
        String tempPassword = UUID.randomUUID().toString().substring(0, 12);

        // Create User with temporary password
        User user = new User();
        user.setName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(User.Role.ADMIN);
        user.setStatus(VerificationStatus.PENDING);
        user.setPassword(passwordEncoder.encode(tempPassword));
        user = userRepository.save(user);

        // Create Admin Profile (minimal, will be completed later)
        Admin admin = new Admin();
        admin.setUser(user);
        admin.setId(user.getId());
        admin.setVerificationStatus(VerificationStatus.PENDING);
        admin.setDeleted(false);
        adminRepository.save(admin);

        // Send invitation email with temporary credentials
        emailService.sendAdminInvitationEmail(
                request.getEmail(),
                request.getFullName(),
                tempPassword);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin invitation sent successfully. Check email for login credentials.");
        response.put("userId", user.getId());
        return response;
    }

    /**
     * Login
     */
    public Map<String, Object> login(LoginRequest request) {

        System.out.println("🔐 Login attempt started");
        System.out.println("Email received: " + request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    System.out.println("❌ User not found for email: " + request.getEmail());
                    return new BadCredentialsException("Incorrect email or password.");
                });

        System.out.println("✅ User found. User ID: " + user.getId());
        System.out.println("User role: " + user.getRole());

        // Verify password
        if (user.getPassword() == null ||
                !passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            System.out.println("❌ Password mismatch for user ID: " + user.getId());
            throw new BadCredentialsException("Incorrect email or password.");
        }

        System.out.println("✅ Password verified");

        // Account status check (currently relaxed)
        System.out.println("Account status: " + user.getStatus());

        // Generate JWT token
        String token = jwtService.generateToken(user.getId(), user.getRole().name());
        System.out.println("🎟️ JWT token generated for user ID: " + user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("role", user.getRole().name());
        response.put("userId", user.getId());
        response.put("name", user.getName());

        if (user.getStatus() != VerificationStatus.ACTIVE) {
            response.put("status", "PENDING");
            System.out.println("⚠️ Login allowed but account status is PENDING");
        } else {
            response.put("status", "ACTIVE");
            System.out.println("✅ Account is ACTIVE");
        }

        System.out.println("🔓 Login successful for user ID: " + user.getId());

        return response;
    }


    public Map<String, Object> getPatientProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findByUserId(userId).orElse(null);

        Map<String, Object> response = new HashMap<>();

        // ---- Basic User Info ----
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("password", user.getPassword());
        response.put("role", user.getRole());
        response.put("status", user.getStatus());

        // ---- If patient profile is NOT completed ----
        if (patient == null) {
            response.put("profileCompleted", false);
            response.put("message", "Patient has not completed profile yet");
            return response;
        }

        // ---- Mark profile as completed ----
        response.put("profileCompleted", true);

        // ---- Personal Details ----
        response.put("dateOfBirth", patient.getDateOfBirth());
        response.put("gender", patient.getGender());
        response.put("bloodGroup", patient.getBloodGroup());
        response.put("phone", patient.getPhone());
        response.put("address", patient.getAddress());
        response.put("city", patient.getCity());
        response.put("state", patient.getState());
        response.put("country", patient.getCountry());
        response.put("pincode", patient.getPincode());
        response.put("idProofPath", patient.getIdProofPath());

        // ---- Lifestyle Details ----
        response.put("sleepHours", patient.getSleepHours());
        response.put("diet", patient.getDiet());
        response.put("smoking", patient.getSmoking());
        response.put("alcohol", patient.getAlcohol());

        // ---- Health Metrics ----
        response.put("sugarLevel", patient.getSugarLevel());
        response.put("bpSys", patient.getBpSys());
        response.put("bpDia", patient.getBpDia());
        response.put("spo2", patient.getSpo2());
        response.put("heartRate", patient.getHeartRate());

        return response;
    }
    public Map<String, Object> getDoctorProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.Role.DOCTOR) {
            throw new RuntimeException("User is not a doctor");
        }

        Doctor doctor = doctorRepository.findById(userId).orElse(null);

        Map<String, Object> response = new HashMap<>();

        /* ---------- BASIC USER INFO ---------- */
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("status", user.getStatus());

        /* ---------- PROFILE NOT COMPLETED ---------- */
        if (doctor == null) {
            response.put("profileCompleted", false);
            response.put("message", "Doctor has not completed profile yet");
            return response;
        }

        response.put("profileCompleted", true);

        /* ---------- PERSONAL INFO ---------- */
        response.put("dateOfBirth", doctor.getDateOfBirth());
        response.put("gender", doctor.getGender());
        response.put("phone", doctor.getPhone());

        /* ---------- PROFESSIONAL INFO ---------- */
        response.put("medicalRegistrationNumber", doctor.getMedicalRegistrationNumber());
        response.put("licensingAuthority", doctor.getLicensingAuthority());
        response.put("specialization", doctor.getSpecialization());
        response.put("qualification", doctor.getQualification());
        response.put("experience", doctor.getExperience());
        response.put("consultationFee", doctor.getConsultationFee());
        response.put("rating", doctor.getAverageRating());
        response.put("ratingCount", doctor.getRatingCount());

        /* ---------- CLINIC INFO ---------- */
        response.put("clinicHospitalName", doctor.getClinicHospitalName());
        response.put("city", doctor.getCity());
        response.put("state", doctor.getState());
        response.put("country", doctor.getCountry());
        response.put("pincode", doctor.getPincode());

        /* ---------- DOCUMENT ---------- */
        response.put("medicalLicense", doctor.getMedicalLicensePath());

        return response;
    }

    public List<Map<String, Object>> getPendingPatients() {

        List<Patient> pendingPatients = patientRepository.findAllPendingPatientsWithBasicInfo();

        return pendingPatients.stream().map(patient -> {
            User user = patient.getUser();

            Map<String, Object> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("name", user.getName());
            map.put("email", user.getEmail());
            map.put("status", user.getStatus());
            map.put("role", user.getRole());

            // Basic Personal Info
            map.put("dateOfBirth", patient.getDateOfBirth());
            map.put("gender", patient.getGender());
            map.put("bloodGroup", patient.getBloodGroup());
            map.put("phone", patient.getPhone());
            map.put("address", patient.getAddress());
            map.put("city", patient.getCity());
            map.put("state", patient.getState());
            map.put("country", patient.getCountry());
            map.put("pincode", patient.getPincode());

            // Optional fields
            map.put("idProof", patient.getIdProofPath());
            map.put("registrationDate", patient.getRegistrationDate());

            return map;
        }).toList();
    }

    public List<Map<String, Object>> getPendingDoctors() {

        List<Doctor> pendingDoctors =
                doctorRepository.findAllPendingDoctorsWithBasicInfo();

        return pendingDoctors.stream().map(doctor -> {
            User user = doctor.getUser();

            Map<String, Object> map = new HashMap<>();

            // User Info
            map.put("userId", user.getId());
            map.put("name", user.getName());
            map.put("email", user.getEmail());
            map.put("status", user.getStatus());
            map.put("role", user.getRole());

            // Personal Info
            map.put("dateOfBirth", doctor.getDateOfBirth());
            map.put("gender", doctor.getGender());
            map.put("phone", doctor.getPhone());

            // Professional Info
            map.put("medicalRegistrationNumber", doctor.getMedicalRegistrationNumber());
            map.put("licensingAuthority", doctor.getLicensingAuthority());
            map.put("specialization", doctor.getSpecialization());
            map.put("qualification", doctor.getQualification());
            map.put("experience", doctor.getExperience());
            map.put("consultationFee", doctor.getConsultationFee());

            // Clinic Info
            map.put("clinicHospitalName", doctor.getClinicHospitalName());
            map.put("city", doctor.getCity());
            map.put("state", doctor.getState());
            map.put("country", doctor.getCountry());
            map.put("pincode", doctor.getPincode());

            // Documents
            map.put("medicalLicense", doctor.getMedicalLicensePath());

            return map;
        }).toList();
    }

    public List<Map<String, Object>> geDoctorsList() {

        List<Doctor> pendingDoctors =
                doctorRepository.findAllDoctors();

        return pendingDoctors.stream().map(doctor -> {
            User user = doctor.getUser();

            Map<String, Object> map = new HashMap<>();

            // User Info
            map.put("userId", user.getId());
            map.put("name", user.getName());
            map.put("email", user.getEmail());
            map.put("status", user.getStatus());
            map.put("role", user.getRole());

            // Personal Info
            map.put("dateOfBirth", doctor.getDateOfBirth());
            map.put("gender", doctor.getGender());
            map.put("phone", doctor.getPhone());

            // Professional Info
            map.put("medicalRegistrationNumber", doctor.getMedicalRegistrationNumber());
            map.put("licensingAuthority", doctor.getLicensingAuthority());
            map.put("specialization", doctor.getSpecialization());
            map.put("qualification", doctor.getQualification());
            map.put("experience", doctor.getExperience());
            map.put("consultationFee", doctor.getConsultationFee());
            map.put("rating",doctor.getAverageRating());
            map.put("ratingCount",doctor.getRatingCount());

            // Clinic Info
            map.put("clinicHospitalName", doctor.getClinicHospitalName());
            map.put("city", doctor.getCity());
            map.put("state", doctor.getState());
            map.put("country", doctor.getCountry());
            map.put("pincode", doctor.getPincode());

            // Documents
            map.put("medicalLicense", doctor.getMedicalLicensePath());

            return map;
        }).toList();
    }
}
