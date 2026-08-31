package es.udc.tfg.fios_rest.user.service.dto;

import es.udc.tfg.fios_rest.common.validation.ValidPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record UserCreateRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 100)
        String firstSurname,

        @Size(max = 100)
        String secondSurname,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotBlank
        @Size(min = 8, max = 100)
        String confirmPassword,

        @NotBlank
        @Size(max = 30)
        @ValidPhone
        String phone,

        @NotNull
        @PastOrPresent
        LocalDate birthDate,

        @Size(max = 1)
        Set<@Positive Long> instrumentIds
) {
}
