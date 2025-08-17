package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Override
    public BookingDto createBooking(Long userId, BookingRequestDto request) {
        return null;
    }

    @Override
    public BookingDto updateBookingStatus(Long ownerId, Long bookingId, boolean approved) {
        return null;
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        return null;
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingStatus status) {
        return List.of();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingStatus status) {
        return List.of();
    }
}
