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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("Running repository tests")
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
    void shouldFindByBookingId() {
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
    void shouldReturnEmpty_WhenItDoesNot_FindByBookingId() {
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
    void shouldFindAllBookingsByUserEmail() {
        //given
        underTest.save(booking);
        var generatedId = booking.getBookingId();
        //when
        var actual = underTest.findAllByUserEmail(booking.getUserEmail());
        //then
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertAll(
                ()-> assertEquals("test@email.com", actual.get(0).getUserEmail()),
                ()-> assertEquals(generatedId, actual.get(0).getBookingId()),
                ()-> assertEquals(BookingStatus.PENDING, actual.get(0).getStatus()),
                ()-> assertEquals("This is a test booking.", actual.get(0).getComments())
        );
    }

    @Test
    void shouldReturnEmptyList_ForNonexistentEmail_FindAllByUserEmail() {
        // given
        underTest.save(booking);
        // when
        var actual = underTest.findAllByUserEmail("nonexistent@email.com");
        // then
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Disabled
    @Test
    void findAllByStartTimeAfter() {
    }

}