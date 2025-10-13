package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, BookingRequestDto request);

    BookingDto updateBookingStatus(Long bookingId, Long ownerId, boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, BookingStatus status);

    List<BookingDto> getOwnerBookings(Long ownerId, BookingStatus status);
}
