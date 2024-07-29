package iam.bookme.repository;

import iam.bookme.AbstractContainerTest;
import iam.bookme.dto.BookingStatusDto;
import iam.bookme.entity.Booking;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    /// This pattern (XXX) includes the 3-digit zone offset (e.g. +05:30 for India Standard Time).
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking(
                "test@email.com",
                OffsetDateTime.parse("2022-08-01T10:00:00+00:00", formatter),
                OffsetDateTime.parse("2022-08-01T10:00:00+00:00", formatter),
                OffsetDateTime.parse("2022-08-05T11:00:00+00:00", formatter),
                45,
                BookingStatusDto.PENDING,
                "This is a test booking.");
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findAllByUserEmail_shouldFindAllBookingsForUserEmail() {
        //given
        underTest.save(booking);
        //when
        var actual = underTest.findAllByUserEmail(booking.getUserEmail());
        //then
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertAll(
                () -> assertEquals("test@email.com", actual.get(0).getUserEmail()),
                () -> assertEquals(BookingStatusDto.PENDING, actual.get(0).getBookingStatus()),
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
        var localDateTime = LocalDateTime.of(2022, 8, 5, 10, 0);
        ZoneOffset offset = ZoneOffset.of("+00:00"); // use GMT
        OffsetDateTime now = localDateTime.atOffset(offset);

        var startTime1 = now.minusDays(0).withHour(9).withMinute(59);
        var startTime2 = now.plusDays(1).withHour(10).withMinute(10);
        Booking booking1 = new Booking("book1@test.com", null, null, startTime1, 60, BookingStatusDto.PENDING, null);

        Booking booking2 = new Booking("book2@test.com", null, null, startTime2, 60, BookingStatusDto.CANCELLED, null);
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
        LocalDateTime localDateTime = LocalDateTime.of(2022, 8, 5, 10, 0);
        ZoneOffset offset = ZoneOffset.of("+00:00"); // use GMT
        OffsetDateTime now = localDateTime.atOffset(offset);

        Booking booking1 = new Booking("book1@test.com", null, null, now, 60, BookingStatusDto.PENDING, null);
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
    void existsByUserEmailIgnoreCase_shouldReturnTrue_ForExistingUserEmail() {
        //given
        underTest.save(booking);
        //when
        var actual = underTest.existsByUserEmailIgnoreCase(booking.getUserEmail());
        //then
        assertTrue(actual);
    }

    @Test
    void existsByUserEmailIgnoreCase_shouldReturnFalse_ForNull() {
        //when
        var actual = underTest.existsByUserEmailIgnoreCase(null);
        //then
        assertFalse(actual);
    }

    @ParameterizedTest
    @DisplayName("test for non existing user email, and empty argument")
    @ValueSource(strings = {"dummy@test.com", ""})
    void existsByUserEmailIgnoreCase_shouldReturnFalse(String input) {
        //when
        var actual = underTest.existsByUserEmailIgnoreCase(input);
        //then
        assertFalse(actual);
    }

    @ParameterizedTest
    @DisplayName("test for existing user email in uppercase and mixed case")
    @ValueSource(strings = {"TEST@EMAIL.COM", "TEst@EMAIL.com"})
    void existsByUserEmailIgnoreCase_shouldReturnTrue(String email) {
        //given
        underTest.save(booking);
        //when
        var actual = underTest.existsByUserEmailIgnoreCase(email);
        //then
        assertTrue(actual);
    }

}