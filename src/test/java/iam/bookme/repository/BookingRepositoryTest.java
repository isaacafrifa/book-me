package iam.bookme.repository;

import iam.bookme.AbstractContainerTest;
import iam.bookme.dto.BookingStatusDto;
import iam.bookme.entity.Booking;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static iam.bookme.config.AppConstants.DATE_TIME_FORMATTER;
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
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        booking = new Booking(
                USER_ID,
                OffsetDateTime.parse("2022-08-01T10:00:00+00:00", DATE_TIME_FORMATTER),
                OffsetDateTime.parse("2022-08-01T10:00:00+00:00", DATE_TIME_FORMATTER),
                OffsetDateTime.parse("2022-08-05T11:00:00+00:00", DATE_TIME_FORMATTER),
                45,
                BookingStatusDto.PENDING,
                "This is a test booking.");
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findAllByUserReferenceId_shouldFindAllBookingsForUserReferenceId() {
        //given
        underTest.save(booking);
        //when
        var actual = underTest.findAllByUserReferenceId(booking.getUserReferenceId());
        //then
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertAll(
                () -> assertEquals(USER_ID, actual.get(0).getUserReferenceId()),
                () -> assertEquals(BookingStatusDto.PENDING, actual.get(0).getStatus()),
                () -> assertEquals("This is a test booking.", actual.get(0).getComments())
        );
    }

    @Test
    void findAllByUserReferenceId_shouldReturnEmptyList_ForNonexistentUserReferenceId() {
        // given
        underTest.save(booking);
        // when
        var actual = underTest.findAllByUserReferenceId(55L);
        // then
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void findAllByStartTimeAfter_shouldFindAllAfterTime() {
        // given
        var localDateTime = LocalDateTime.of(2022, 8, 5, 10, 0);
        ZoneOffset offset = ZoneOffset.of("+00:00"); // use GMT
        OffsetDateTime now = localDateTime.atOffset(offset);

        var startTime1 = now.minusDays(0).withHour(9).withMinute(59);
        var startTime2 = now.plusDays(1).withHour(10).withMinute(10);
        Booking booking1 = new Booking(2L, null, null, startTime1, 60, BookingStatusDto.PENDING, null);

        Booking booking2 = new Booking(3L, null, null, startTime2, 60, BookingStatusDto.CANCELLED, null);
        underTest.save(booking);
        underTest.save(booking1);
        underTest.save(booking2);
        // when
        var actual = underTest.findAllByStartTimeAfter(now);
        // then
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertAll(
                () -> assertEquals(USER_ID, actual.get(0).getUserReferenceId()),
                () -> assertEquals(3L, actual.get(1).getUserReferenceId()),
                () -> assertEquals("This is a test booking.", actual.get(0).getComments()),
                () -> assertNull(actual.get(1).getComments())
        );
    }

    @Test
    void findAllByStartTimeAfter_shouldReturnEmptyList_AtExactTime() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2022, 8, 5, 10, 0);
        ZoneOffset offset = ZoneOffset.of("+00:00"); // use GMT
        OffsetDateTime now = localDateTime.atOffset(offset);

        Booking booking1 = new Booking(2L, null, null, now, 60, BookingStatusDto.PENDING, null);
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
        OffsetDateTime now = OffsetDateTime.now();
        // When
        var actual = underTest.findAllByStartTimeAfter(now);
        // Then
        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    @Test
    void findByUserReferenceIdAndStartTime_shouldReturnBookingWhenFound() {
        // Given
        OffsetDateTime startTime = booking.getStartTime();
        underTest.save(booking);
        // When
        Optional<Booking> foundBooking = underTest.findByUserReferenceIdAndStartTime(USER_ID, startTime);
        // Then
        assertTrue(foundBooking.isPresent());
        assertEquals(booking, foundBooking.get());
    }

    @Test
    void findByUserReferenceIdAndStartTime_shouldReturnEmptyWhenNotFound_dueToInvalidStartTime() {
        // Given
        OffsetDateTime startTime = OffsetDateTime.now().plusHours(1); // Different startTime
        underTest.save(booking);
        // When
        Optional<Booking> foundBooking = underTest.findByUserReferenceIdAndStartTime(USER_ID, startTime);
        // Then
        assertTrue(foundBooking.isEmpty());
    }

}