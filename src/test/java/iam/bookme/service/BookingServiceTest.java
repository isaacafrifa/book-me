package iam.bookme.service;

import iam.bookme.TestContext;
import iam.bookme.dto.BookingMapper;
import iam.bookme.entity.Booking;
import iam.bookme.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("Running bookingService tests")
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService underTest;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;

    private final TestContext testContext = new TestContext();
    private Booking booking;
    @Captor
    ArgumentCaptor<Booking> bookingArgumentCaptor;

    @BeforeEach
    void setUp() {
      booking = testContext.getTestBooking();
    }

    @Test
    void getAllBookings_shouldGetAllBookings() {
        //given
        Page<Booking> page = new PageImpl<>(Collections.singletonList(booking));
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDate"));
        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        given(bookingRepository.findAll(pageable)).willReturn(page);
        //when
        var actual = underTest.getAllBookings(0, 5, "desc", "createdDate");
        //then
        verify(bookingRepository).findAll(pageable);
        verify(bookingRepository).findAll(pageableCaptor.capture());
        var captorValue = pageableCaptor.getValue();
        assertEquals(5, captorValue.getPageSize());
        assertEquals(1, actual.getTotalElements(), "Expected to find one booking");
    }

    @Test
    void getAllBookings_shouldReturnEmptyPageWhenNoBookings() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDate"));
        given(bookingRepository.findAll(pageable)).willReturn(Page.empty());
        // when
        var actual = underTest.getAllBookings(0, 5, "desc", "createdDate");
        // then
        verify(bookingRepository).findAll(pageable);
        assertEquals(0, actual.getTotalElements(), "Expected no booking");
    }

}