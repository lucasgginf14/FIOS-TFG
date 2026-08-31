package es.udc.tfg.fios_rest.user.service.dto;

import es.udc.tfg.fios_rest.common.validation.ValidPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record UserUpdateRequest(
        @Size(max = 100)
        String name,

        @Size(max = 100)
        String firstSurname,

        @Size(max = 100)
        String secondSurname,

        @Email
        @Size(max = 150)
        String email,

        @Size(max = 30)
        @ValidPhone
        String phone,

        @PastOrPresent
        LocalDate birthDate,

        @Size(max = 1)
        Set<@Positive Long> instrumentIds
) {
}
