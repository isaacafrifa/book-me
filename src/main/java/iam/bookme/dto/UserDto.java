package iam.bookme.dto;

import java.time.OffsetDateTime;

public record UserDto(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Long id,
        OffsetDateTime createdOn,
        OffsetDateTime updatedOn) {
}
