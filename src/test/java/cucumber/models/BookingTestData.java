package cucumber.models;

import iam.bookme.dto.BookingStatusDto;

import java.time.OffsetDateTime;

/**
 * Record class representing booking test data for comparison in tests.
 * Contains essential booking information including ID, user, timing, status and comments.
 *
 * @param bookingId      Unique identifier for the booking
 * @param userId         ID of the user who made the booking
 * @param startTime      Date and time when the booking starts
 * @param bookingStatus  Current status of the booking (PENDING, CONFIRMED, CANCELLED)
 * @param comments       Optional comments associated with the booking
 */
public record BookingTestData(
        Long bookingId,
        Long userId,
        OffsetDateTime startTime,
        BookingStatusDto bookingStatus,
        String comments
) {}
