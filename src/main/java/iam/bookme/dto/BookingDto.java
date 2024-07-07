package iam.bookme.dto;

import iam.bookme.enums.BookingStatus;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link iam.bookme.entity.Booking}
 */
@Value
public class BookingDto implements Serializable {
    UUID bookingId;
    String userEmail;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    LocalDateTime startTime;
    BookingStatus status;
    String comments;
}