package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.Otp;
import com.medibook.medibook_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByEmailAndRoleAndUsedFalseOrderByCreatedAtDesc(
            String email, User.Role role
    );

    void deleteByEmailAndRole(String email, User.Role role);

    boolean existsByEmailAndRoleAndUsedTrue(
            String email, User.Role role
    );
}
