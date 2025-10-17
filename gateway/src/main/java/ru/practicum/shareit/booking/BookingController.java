package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-ID";
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody BookingRequestDto bookingRequest) {
        return bookingClient.createBooking(userId, bookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        return bookingClient.updateBookingStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") BookingStatus status) {
        return bookingClient.getUserBookings(userId, status);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingStatus status) {
        return bookingClient.getOwnerBookings(ownerId, status);
    }
}
