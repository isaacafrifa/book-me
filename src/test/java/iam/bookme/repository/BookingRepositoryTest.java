package iam.bookme.repository;

import iam.bookme.AbstractContainerTest;
import iam.bookme.entity.Booking;
import iam.bookme.enums.BookingStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Running repository tests")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest extends AbstractContainerTest {

    @Autowired
    private BookingRepository underTest;
    private Booking booking;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        //create the booking object
        booking = new Booking(
                id,
                "test@email.com",
                LocalDateTime.parse("2022-08-01T10:00:00Z"),
                LocalDateTime.parse("2022-08-01T10:00:00Z"),
                LocalDateTime.parse("2022-08-05T11:00:00Z"),
                45,
                BookingStatus.PENDING,
                "This is a test booking.");
    }
    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindByBookingId() {
        // given
underTest.save(booking);
        // when
        var actual = underTest.findById(booking.getBookingId());
        //then
        assertNotNull(actual);
    }

    @Test
    @Disabled
    void shouldThrowException_WhenItDoesNot_FindByBookingId() {
        // given
        // when
        //then
        fail("Not implemented yet");
    }

    @Test
    @Disabled
    void shouldThrowException_WhenNullIsPaasedInto_FindByBookingId() {
        // given
        // when
        //then
        fail("Not implemented yet");
    }

    @Disabled
    @Test
    void findAllByStartTimeAfter() {
    }

    @Disabled
    @Test
    void findAllByUser() {
    }
}