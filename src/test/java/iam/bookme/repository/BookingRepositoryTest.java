package iam.bookme.repository;

import iam.bookme.AbstractContainerTest;
import iam.bookme.entity.Booking;
import iam.bookme.enums.BookingStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("Running bookingRepository tests")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookingRepositoryTest extends AbstractContainerTest {

    @Autowired
    private BookingRepository underTest;
    private Booking booking;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @BeforeEach
    void setUp() {
        booking = new Booking(
                null, // Let Hibernate generate ID
                "test@email.com",
                LocalDateTime.parse("2022-08-01T10:00:00Z", formatter),
                LocalDateTime.parse("2022-08-01T10:00:00Z", formatter),
                LocalDateTime.parse("2022-08-05T11:00:00Z", formatter),
                45,
                BookingStatus.PENDING,
                "This is a test booking.");
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findAllByBookingId_shouldFindBookingById() {
        // given
        underTest.save(booking);
        var generatedId = booking.getBookingId();
        // when
        var actual = underTest.findById(generatedId);
        //then
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(booking.getBookingId(), actual.get().getBookingId());
        assertEquals(booking.getStartTime(), actual.get().getStartTime());
        assertEquals(booking.getEndTime(), actual.get().getEndTime());
        assertEquals(booking.getDurationInMinutes(), actual.get().getDurationInMinutes());
        assertEquals(booking.getStatus(), actual.get().getStatus());
    }

    @Test
    void findAllByBookingId_shouldReturnEmpty_WhenNotFound() {
        // given
        underTest.save(booking);
        UUID randomUUID = UUID.randomUUID();
        // when
        var actual = underTest.findById(randomUUID);
        //then
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void findAllByUserEmail_shouldFindAllBookingsForUserEmail() {
        //given
        underTest.save(booking);
        var generatedId = booking.getBookingId();
        //when
        var actual = underTest.findAllByUserEmail(booking.getUserEmail());
        //then
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertAll(
                () -> assertEquals("test@email.com", actual.get(0).getUserEmail()),
                () -> assertEquals(generatedId, actual.get(0).getBookingId()),
                () -> assertEquals(BookingStatus.PENDING, actual.get(0).getStatus()),
                () -> assertEquals("This is a test booking.", actual.get(0).getComments())
        );
    }

    @Test
    void findAllByUserEmail_shouldReturnEmptyList_ForNonexistentEmail() {
        // given
        underTest.save(booking);
        // when
        var actual = underTest.findAllByUserEmail("nonexistent@email.com");
        // then
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void findAllByStartTimeAfter_shouldFindAllAfterTime() {
        // given
        LocalDateTime now = LocalDateTime.of(2022, 8, 5, 10, 0);
        LocalDateTime startTime1 = now.minusDays(0).withHour(9).withMinute(59);
        LocalDateTime startTime2 = now.plusDays(1).withHour(10).withMinute(10);

        Booking booking1 = new Booking(null, "book1@test.com", null, null, startTime1, 60, BookingStatus.PENDING, null);
        Booking booking2 = new Booking(null, "book2@test.com", null, null, startTime2, 60, BookingStatus.CANCELLED, null);

        underTest.save(booking);
        underTest.save(booking1);
        underTest.save(booking2);
        // when
        var actual = underTest.findAllByStartTimeAfter(now);
        // then
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertAll(
                () -> assertEquals("test@email.com", actual.get(0).getUserEmail()),
                () -> assertEquals("book2@test.com", actual.get(1).getUserEmail()),
                () -> assertEquals("This is a test booking.", actual.get(0).getComments()),
                () -> assertNull(actual.get(1).getComments())
        );
    }

    @Test
    void findAllByStartTimeAfter_shouldReturnEmptyList_AtExactTime() {
        // given
        LocalDateTime now = LocalDateTime.of(2022, 8, 5, 10, 0);
        Booking booking1 = new Booking(null, "book1@test.com", null, null, now, 60, BookingStatus.PENDING, null);

        underTest.save(booking1);
        // when
        var actual = underTest.findAllByStartTimeAfter(now);
        // then
        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    @Test
    void findAllByStartTimeAfter_shouldReturnEmptyList_ForEmptyDatabase() {
        // Given (no bookings saved in db)
        LocalDateTime now = LocalDateTime.now();
        // When
        var actual = underTest.findAllByStartTimeAfter(now);
        // Then
        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

}