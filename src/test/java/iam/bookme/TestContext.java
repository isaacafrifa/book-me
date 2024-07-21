package iam.bookme;

import iam.bookme.dto.BookingStatus;
import iam.bookme.entity.Booking;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class TestContext {

    // This pattern (XXX) includes the 3-digit zone offset (e.g. +05:30 for India Standard Time).
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    public static final UUID bookingId = UUID.fromString("f81d4fae-7dec-11e4-9635-286e88f7c621");

    public Booking getTestBooking() {
        return new Booking(
                null, // Let Hibernate generate ID
                "test@email.com",
                OffsetDateTime.parse("2022-08-01T10:00:00+00:00", formatter),
                OffsetDateTime.parse("2022-08-01T10:00:00+00:00", formatter),
                OffsetDateTime.parse("2022-08-05T11:00:00+00:00", formatter),
                45,
                BookingStatus.PENDING,
                "This is a test booking.");
    }

}
