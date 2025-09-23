package ru.practicum.shareit.booking.service;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, @Valid BookingRequestDto request) {
        log.info("Creating booking for user: {}, item: {}", userId, request.getItemId());
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ValidationException("Item is unavailable for booking.");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")));
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created: {}", savedBooking.getId());
        return BookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, Long ownerId, boolean approved) {
        log.info("Updating booking status for booking: {}, approved: {}", bookingId, approved);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("The booking cannot be confirmed because its status is not WAITING.");
        }
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("User is not the owner of the item");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(updatedBooking);

    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.info("Getting booking details for user: {}, booking: {}", userId, bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("User is not authorized to view this booking");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long bookerId, BookingStatus status) {
        log.info("Getting user bookings for user: {}, status: {}", bookerId, status);
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new ValidationException("Can't get all bookings for a non-existent booker with id: " + bookerId + "."));

        List<Booking> bookings;
        if (BookingStatus.ALL == status) {
            bookings = bookingRepository.findByBookerId(booker.getId());
        } else {
            bookings = bookingRepository.findByBookerIdAndStatus(booker.getId(), status);
        }
        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingStatus status) {
        log.info("Getting owner bookings for owner: {}, status: {}", ownerId, status);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ValidationException("Can't get all bookings for a non-existent owner with id: " + ownerId + "."));
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatus(owner.getId(), status);
        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }
}