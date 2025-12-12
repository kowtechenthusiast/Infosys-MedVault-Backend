package com.medibook.medibook_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SetPasswordRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String password;

    public Long getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }
}
