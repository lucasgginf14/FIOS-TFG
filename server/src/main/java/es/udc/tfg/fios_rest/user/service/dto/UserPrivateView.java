package es.udc.tfg.fios_rest.user.service.dto;

import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentRef;
import es.udc.tfg.fios_rest.user.persistence.entity.PlatformRole;
import es.udc.tfg.fios_rest.user.persistence.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserPrivateView(
        Long id,
        String name,
        String firstSurname,
        String secondSurname,
        String email,
        String phone,
        String profileImage,
        LocalDateTime createdAt,
        LocalDate birthDate,
        boolean active,
        PlatformRole platformRole,
        InstrumentRef instrument,
        List<InstrumentRef> instruments
) {
    public Long getId() {
        return id;
    }

    public PlatformRole getPlatformRole() {
        return platformRole;
    }

    public static UserPrivateView from(User user) {
        InstrumentRef primaryInstrument = user.getPrimaryInstrument() == null
                ? null
                : InstrumentRef.from(user.getPrimaryInstrument());

        return new UserPrivateView(
                user.getId(),
                user.getName(),
                user.getFirstSurname(),
                user.getSecondSurname(),
                user.getEmail(),
                user.getPhone(),
                UserProfileImages.visibleProfileImage(user),
                user.getCreatedAt(),
                user.getBirthDate(),
                user.isActive(),
                user.getPlatformRole(),
                primaryInstrument,
                primaryInstrument == null ? List.of() : List.of(primaryInstrument)
        );
    }
}
