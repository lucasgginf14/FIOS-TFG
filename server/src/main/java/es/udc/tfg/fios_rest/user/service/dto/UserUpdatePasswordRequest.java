package es.udc.tfg.fios_rest.user.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdatePasswordRequest(
        @NotBlank
        @Size(max = 100)
        String currentPassword,

        @NotBlank
        @Size(min = 8, max = 100)
        String newPassword,

        @NotBlank
        @Size(min = 8, max = 100)
        String confirmNewPassword
) {
}