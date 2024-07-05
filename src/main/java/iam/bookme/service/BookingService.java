package iam.bookme.service;

import iam.bookme.repository.BookingRepository;

public record BookingService (BookingRepository bookingRepository) {
}
