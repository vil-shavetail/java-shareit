package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
    public BookingDto createBooking(Long userId, BookingRequestDto request) {
        log.info("Creating booking for user: {}, item: {}", userId, request.getItemId());
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ValidateException("Item is unavailable for booking.");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
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
            throw new ValidateException("The booking cannot be confirmed because its status is not WAITING.");
        }
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidateException("User is not the owner of the item");
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
            throw new ValidateException("User is not authorized to view this booking");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long bookerId, BookingStatus status) {
        log.info("Getting user bookings for user: {}, status: {}", bookerId, status);
        LocalDateTime currentDateTime = LocalDateTime.now();
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Can't get all bookings for a non-existent booker with id: " + bookerId + "."));

        List<Booking> bookings = switch (status) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(booker.getId());
            case PAST -> bookingRepository.findByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
                    booker.getId(),
                    BookingStatus.APPROVED,
                    currentDateTime);
            case CURRENT ->
                    bookingRepository.findCurrentBookingsByBooker(
                            booker.getId(),
                            BookingStatus.APPROVED,
                            currentDateTime
                    );
            case FUTURE -> bookingRepository.findByBookerIdAndStatusAndStartAfterOrderByStartAsc(
                    booker.getId(),
                    BookingStatus.APPROVED,
                    currentDateTime
            );
            case WAITING ->
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.REJECTED);
            case null, default -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker.getId(), status);
        };
        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingStatus status) {
        log.info("Getting owner bookings for owner: {}, status: {}", ownerId, status);
        LocalDateTime currentDateTime = LocalDateTime.now();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Can't get all bookings for a non-existent owner with id: " + ownerId + "."));
        List<Booking> bookings = switch (status) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId());
            case PAST -> bookingRepository.findByItemOwnerIdAndStatusAndEndBeforeOrderByStartDesc(
                    owner.getId(),
                    BookingStatus.APPROVED,
                    currentDateTime);
            case CURRENT ->
                    bookingRepository.findCurrentBookingsByItemOwner(
                            owner.getId(),
                            BookingStatus.APPROVED,
                            currentDateTime
                    );
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStatusAndStartAfterOrderByStartAsc(
                    owner.getId(),
                    BookingStatus.APPROVED,
                    currentDateTime
            );
            case WAITING ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), BookingStatus.REJECTED);
            case null, default -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(owner.getId(), status);
        };
        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }
}