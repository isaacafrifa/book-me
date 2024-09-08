package iam.bookme.service;

import iam.bookme.dto.BookingDto;
import iam.bookme.dto.BookingMapper;
import iam.bookme.dto.BookingRequestDto;
import iam.bookme.dto.BookingStatusDto;
import iam.bookme.dto.UserDto;
import iam.bookme.entity.Booking;
import iam.bookme.exception.ResourceAlreadyExistsException;
import iam.bookme.exception.ResourceNotFoundException;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Running bookingService tests")
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BookingServiceTest {

    @InjectMocks
    private BookingService underTest;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private BookingValidationService bookingValidationService;
    @Mock
    private UserService userService;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;

    /// This pattern (XXX) includes the 3-digit zone offset (e.g. +05:30 for India Standard Time).
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static final UUID BOOKING_ID = UUID.fromString("f81d4fae-7dec-11e4-9635-286e88f7c621");
    public static final BookingStatusDto PENDING = BookingStatusDto.PENDING;
    public static final int DURATION_IN_MINUTES = 45;
    public static final String BOOKING_COMMENT = "This is a test booking.";
    public static final String EMAIL = "test@example.com";
    public static final String CREATED_ON = "2022-08-01T10:00:00+00:00";
    public static final String UPDATED_ON = "2022-08-01T10:00:00+00:00";
    public static final String START_TIME = "2022-08-05T11:00:00+00:00";

    @Captor
    ArgumentCaptor<Booking> bookingArgumentCaptor;

    @BeforeEach
    void setUp() {
        booking = new Booking(
                1L,
                OffsetDateTime.parse(CREATED_ON, formatter),
                OffsetDateTime.parse(UPDATED_ON, formatter),
                OffsetDateTime.parse(START_TIME, formatter),
                DURATION_IN_MINUTES,
                PENDING,
                BOOKING_COMMENT);
        bookingRequestDto = createBookingRequestDto();
    }

    @Test
    void getAllBookings_shouldGetAllBookings() {
        //given
        Page<Booking> page = new PageImpl<>(Collections.singletonList(booking));
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDate"));
        given(bookingRepository.findAll(pageable)).willReturn(page);
        //when
        var actual = underTest.getAllBookings(0, 5, "desc", "createdOn");
        //then
        verify(bookingRepository).findAll(pageable);
        assertNotNull(actual, "Expected a non-null page of booking");
        assertEquals(1, actual.getTotalElements(), "Expected one booking");
    }

    @Test
    void getAllBookings_shouldReturnEmptyPageWhenNoBookings() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDate"));
        given(bookingRepository.findAll(pageable)).willReturn(Page.empty());
        // when
        var actual = underTest.getAllBookings(0, 5, "desc", "createdOn");
        // then
        verify(bookingRepository).findAll(pageable);
        assertEquals(0, actual.getTotalElements(), "Expected no booking");
    }

    @Test
    void createBooking_shouldSaveBooking_withNewUser() {
        // given
        var userDto = createUserDto();
        given(userService.getUser(any())).willReturn(userDto);
        given(bookingRepository.findByUserReferenceIdAndStartTime(userDto.id(), bookingRequestDto.getStartTime()))
                .willReturn(Optional.empty());
        given(bookingMapper.toEntity(bookingRequestDto)).willReturn(booking);
        given(bookingRepository.save(booking)).willReturn(booking);
        // when
        underTest.createBooking(bookingRequestDto);
        // then
        verify(bookingMapper).toEntity(bookingRequestDto);
        verify(bookingValidationService).validateBookingRequestDto(bookingRequestDto);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        verify(bookingMapper, times(1)).toDto(booking);
        Booking capturedBooking = bookingArgumentCaptor.getValue();
        assertEquals(booking.getUserReferenceId(), capturedBooking.getUserReferenceId());
        assertEquals(booking.getStartTime(), capturedBooking.getStartTime());
        assertEquals(PENDING, capturedBooking.getStatus());
        assertEquals(DURATION_IN_MINUTES, capturedBooking.getDurationInMinutes());
        assertEquals(BOOKING_COMMENT, capturedBooking.getComments());
    }

    @Test
    void createBooking_shouldSaveBooking_withCanceledStatus() {
        // given
        var userDto = createUserDto();
        booking.setStatus(BookingStatusDto.CANCELLED); // use BookingStatusDto
        given(userService.getUser(any())).willReturn(userDto);
        given(bookingRepository.findByUserReferenceIdAndStartTime(userDto.id(), bookingRequestDto.getStartTime()))
                .willReturn(Optional.of(booking));
        given(bookingMapper.toEntity(bookingRequestDto)).willReturn(booking);
        given(bookingRepository.save(booking)).willReturn(booking);
        // when
        underTest.createBooking(bookingRequestDto);
        // then
        verify(bookingMapper).toEntity(bookingRequestDto);
        verify(bookingValidationService).validateBookingRequestDto(bookingRequestDto);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        verify(bookingMapper, times(1)).toDto(booking);
        Booking capturedBooking = bookingArgumentCaptor.getValue();
        assertEquals(booking.getUserReferenceId(), capturedBooking.getUserReferenceId());
        assertEquals(booking.getStartTime(), capturedBooking.getStartTime());
        // Verify saved booking is PENDING
        assertEquals(BookingStatusDto.PENDING, capturedBooking.getStatus());
        assertEquals(DURATION_IN_MINUTES, capturedBooking.getDurationInMinutes());
        assertEquals(BOOKING_COMMENT, capturedBooking.getComments());
    }

    @Test
    void createBooking_shouldThrowExceptionWhenBookingAlreadyExists_withConfirmedStatus() {
        // given
        var userDto = createUserDto();
        // create a booking object with confirmed status
        booking.setStatus(BookingStatusDto.CONFIRMED);
        given(userService.getUser(any())).willReturn(userDto);
        given(bookingRepository.findByUserReferenceIdAndStartTime(userDto.id(), bookingRequestDto.getStartTime()))
                .willReturn(Optional.of(booking));

        // when + then
        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> underTest.createBooking(bookingRequestDto),
                "Should throw exception"
        );
        verify(bookingMapper, never()).toEntity(bookingRequestDto);
        verify(bookingRepository, never()).save(any());
        verify(bookingMapper, never()).toDto(any());
        verify(bookingValidationService).validateBookingRequestDto(any());
    }

    @Test
    void createBooking_shouldThrowExceptionWhenBookingAlreadyExists_withPendingStatus() {
        // Given
        var userDto = createUserDto();
        booking.setStatus(BookingStatusDto.PENDING); // Set existing booking to pending
        given(userService.getUser(any())).willReturn(userDto);
        given(bookingRepository.findByUserReferenceIdAndStartTime(userDto.id(), bookingRequestDto.getStartTime()))
                .willReturn(Optional.of(booking));

        // When + Then
        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> underTest.createBooking(bookingRequestDto),
                "Should throw exception for pending booking"
        );
        verify(bookingMapper, never()).toEntity(bookingRequestDto);
        verify(bookingRepository, never()).save(any());
        verify(bookingMapper, never()).toDto(any());
        verify(bookingValidationService).validateBookingRequestDto(any());
    }

    @Test
    void getBookingById_shouldReturnBookingDto() {
        // given
        BookingDto bookingDto = createDefaultBookingDto();
        given(bookingMapper.toDto(any())).willReturn(bookingDto);
        given(bookingRepository.findById(any())).willReturn(Optional.of(booking));
        // when
        var actualDto = underTest.getBookingById(booking.getBookingId());
        // then
        verify(bookingMapper).toDto(booking);
        verify(bookingRepository).findById(booking.getBookingId());
        assertNotNull(actualDto, "Expected a BookingDto to be returned");
    }

    @Test
    void getBookingById_shouldReturnNotFoundException() {
        //given
        given(bookingRepository.findById(BOOKING_ID)).willThrow(ResourceNotFoundException.class);
        //when + then
        assertThrows(ResourceNotFoundException.class,
                () -> underTest.getBookingById(BOOKING_ID),
                "Should throw booking not found exception"
        );
        verify(bookingMapper, never()).toDto(booking);
    }

    @Test
    void getBookingById_shouldThrowExceptionForNullId() {
        // when + then
        assertThrows(ResourceNotFoundException.class, () -> underTest.getBookingById(null));
    }

    @Test
    void getBookingById_shouldThrowExceptionForInvalidIdType() {
        // Given
        String invalidId = "invalid-string-id";
        // When + Then
        assertThrows(IllegalArgumentException.class, () -> underTest.getBookingById(UUID.fromString(invalidId)),
                "Should throw an exception");
    }

    @Test
    void updateBooking_shouldUpdateBooking() {
        // given
        given(bookingRepository.findById(any())).willReturn(Optional.of(booking));
        // when
        underTest.updateBooking(BOOKING_ID, bookingRequestDto);
        // then
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking capturedBooking = bookingArgumentCaptor.getValue();
        assertEquals(booking.getStartTime(), capturedBooking.getStartTime());
        assertEquals(PENDING, capturedBooking.getStatus());
        assertEquals(booking.getComments(), capturedBooking.getComments());
        assertEquals(booking.getDurationInMinutes(), capturedBooking.getDurationInMinutes());
    }

    @Test
    void updateBooking_shouldThrowExceptionForNonexistentId() {
        // given
        UUID nonExistentId = UUID.randomUUID();
        given(bookingRepository.findById(nonExistentId)).willReturn(Optional.empty());
        // when + then
        assertThrows(ResourceNotFoundException.class,
                () -> underTest.updateBooking(nonExistentId, bookingRequestDto),
                "Should throw an exception");
    }

    @Test
    void updateBooking_shouldNotUpdateBooking_withCanceledStatus() {
        // given
        booking.setStatus(BookingStatusDto.CANCELLED);
        given(bookingRepository.findById(any())).willReturn(Optional.of(booking));
        // when + then
        assertThrows(RuntimeException.class,
                () -> underTest.updateBooking(BOOKING_ID, bookingRequestDto),
                "Should throw an 'Booking cannot be updated' exception");
        verify(bookingMapper, never()).toDto(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBooking_shouldNotUpdateBooking_withConfirmedStatus() {
        // given
        booking.setStatus(BookingStatusDto.CONFIRMED);
        given(bookingRepository.findById(any())).willReturn(Optional.of(booking));
        // when + then
        assertThrows(RuntimeException.class,
                () -> underTest.updateBooking(BOOKING_ID, bookingRequestDto),
                "Should throw an 'Booking cannot be updated' exception");
        verify(bookingMapper, never()).toDto(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void deleteBooking_shouldDeleteBooking() {
        // given
        given(bookingRepository.existsById(any())).willReturn(true);
        // when
        underTest.deleteBooking(BOOKING_ID);
        // then
        verify(bookingRepository).deleteById(BOOKING_ID);
    }

    @Test
    void deleteBooking_shouldThrowExceptionForNonexistentId() {
        // given
        UUID nonExistentId = UUID.randomUUID();
        given(bookingRepository.existsById(nonExistentId)).willReturn(false);
        // when + then
        assertThrows(ResourceNotFoundException.class,
                () -> underTest.deleteBooking(nonExistentId),
                "Should throw an exception");
        verify(bookingRepository, never()).deleteById(nonExistentId);
    }

    private BookingDto createDefaultBookingDto() {
        var defaultBookingDto = new BookingDto();
        defaultBookingDto.setBookingId(BOOKING_ID);
        defaultBookingDto.setUserId(booking.getUserReferenceId());
        defaultBookingDto.setCreatedDate(booking.getCreatedDate());
        defaultBookingDto.setUpdatedDate(booking.getUpdatedDate());
        defaultBookingDto.setStartTime(booking.getStartTime());
        defaultBookingDto.setBookingStatus(booking.getStatus());
        defaultBookingDto.setComments(booking.getComments());
        return defaultBookingDto;
    }

    private BookingRequestDto createBookingRequestDto() {
        var defaultBookingRequestDto = new BookingRequestDto();
        defaultBookingRequestDto.setUserEmail(EMAIL);
        defaultBookingRequestDto.setStartTime(booking.getStartTime());
        defaultBookingRequestDto.setComments(booking.getComments());
        return defaultBookingRequestDto;
    }

    private UserDto createUserDto() {
        return new UserDto("John", "Doe", EMAIL,
                "0244599000", 1L, OffsetDateTime.now(), OffsetDateTime.now());
    }
}