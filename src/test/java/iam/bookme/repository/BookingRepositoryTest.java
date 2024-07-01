package iam.bookme.repository;

import iam.bookme.entity.Booking;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Running repository tests")
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository underTest;
    private Booking booking;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        //create the booking object
    }
    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindByBookingId() {
        // given
        // when
        //then
        fail("Not implemented yet");
    }

    @Test
    void shouldThrowException_WhenItDoesNot_FindByBookingId() {
        // given
        // when
        //then
        fail("Not implemented yet");
    }

    @Test
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