package iam.bookme;

import iam.bookme.entity.Booking;
import iam.bookme.enums.BookingStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestContext {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

   public Booking getTestBooking() {
       return new Booking(
                null, // Let Hibernate generate ID
                "test@email.com",
                LocalDateTime.parse("2022-08-01T10:00:00Z", formatter),
                LocalDateTime.parse("2022-08-01T10:00:00Z", formatter),
                LocalDateTime.parse("2022-08-05T11:00:00Z", formatter),
                45,
                BookingStatus.PENDING,
                "This is a test booking.");
    }
}
