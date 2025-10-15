package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Long userId;
    private Long itemId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        userId = userService.createUser(new UserDto(null, "Jane Doe", "jane.doe@example.com")).getId();
        itemId = itemService.createItem(
                new ItemDto(null, "Laptop", "Gaming Laptop", true, null, null, null, null, null),
                userId
        ).getId();

        startDate = LocalDateTime.now().plusDays(1);
        endDate = LocalDateTime.now().plusDays(2);
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateBookingSuccess() {
        BookingRequestDto request = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        BookingDto createdBooking = bookingService.createBooking(userId, request);
        assertThat(createdBooking.getId()).isNotNull();
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void testCreateBookingForUnavailableItem() {
        itemService.updateItem(itemId, new ItemDto(itemId,
                "Laptop",
                "Gaming Laptop",
                false,
                null,
                null,
                null,
                null,
                null), userId);
        BookingRequestDto request = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        assertThatThrownBy(() ->
                bookingService.createBooking(userId, request)
        ).isInstanceOf(ValidateException.class);
    }

    @Test
    void testUpdateBookingStatus() {
        BookingRequestDto request = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        BookingDto booking = bookingService.createBooking(userId, request);
        BookingDto updatedBooking = bookingService.updateBookingStatus(booking.getId(), userId, true);
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testUpdateBookingStatusByFakeItemId() {
        BookingRequestDto request = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        bookingService.createBooking(userId, request);
        assertThatThrownBy(() ->
                bookingService.updateBookingStatus(67L, userId, true)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testUpdateBookingStatusByFakeUser() {
        BookingRequestDto request = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        BookingDto booking = bookingService.createBooking(userId, request);
        assertThatThrownBy(() ->
                bookingService.updateBookingStatus(booking.getId(), 678L, true)
        ).isInstanceOf(ValidateException.class);
    }

    @Test
    void testGetBookingById() {
        BookingRequestDto request = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        BookingDto booking = bookingService.createBooking(userId, request);
        BookingDto retrievedBooking = bookingService.getBookingById(userId, booking.getId());
        assertThat(retrievedBooking.getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetBookingByIdByFakeId() {
        BookingRequestDto request = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        bookingService.createBooking(userId, request);
        assertThatThrownBy(() ->
                bookingService.getBookingById(userId, 654L)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testGetBookingByIdByFakeUserId() {
        BookingRequestDto request = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        BookingDto booking = bookingService.createBooking(userId, request);
        assertThatThrownBy(() ->
                bookingService.getBookingById(562L, booking.getId())
        ).isInstanceOf(ValidateException.class);
    }

    @Test
    void testGetUserBookings_ALL() {
        bookingService.createBooking(userId, new BookingRequestDto(itemId, startDate, endDate));
        bookingService.createBooking(userId, new BookingRequestDto(itemId, startDate.plusDays(3), endDate.plusDays(3)));
        List<BookingDto> allBookings = bookingService.getUserBookings(userId, BookingStatus.ALL);
        assertEquals(2, allBookings.size());
    }

    @Test
    void testGetUserBookings_PAST() {
        LocalDateTime pastStart = LocalDateTime.now().minusDays(2);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(1);
        BookingRequestDto pastRequest = new BookingRequestDto(
                itemId,
                pastStart,
                pastEnd
        );
        BookingDto pastBooking = bookingService.createBooking(userId, pastRequest);
        bookingService.updateBookingStatus(pastBooking.getId(), userId, true);
        List<BookingDto> pastBookings = bookingService.getUserBookings(userId, BookingStatus.PAST);
        assertEquals(1, pastBookings.size());
    }

    @Test
    void testGetUserBookings_CURRENT() {
        LocalDateTime currentStart = LocalDateTime.now().minusDays(1);
        LocalDateTime currentEnd = LocalDateTime.now().plusDays(1);
        BookingRequestDto currentRequest = new BookingRequestDto(
                itemId,
                currentStart,
                currentEnd
        );
        BookingDto currentBooking = bookingService.createBooking(userId, currentRequest);
        bookingService.updateBookingStatus(currentBooking.getId(), userId, true);
        List<BookingDto> currentBookings = bookingService.getUserBookings(userId, BookingStatus.CURRENT);
        assertEquals(1, currentBookings.size());
    }

    @Test
    void testGetUserBookings_FUTURE() {
        BookingRequestDto futureRequest = new BookingRequestDto(
                itemId,
                startDate.plusDays(5),
                endDate.plusDays(5)
        );
        BookingDto futureBooking = bookingService.createBooking(userId, futureRequest);
        bookingService.updateBookingStatus(futureBooking.getId(), userId, true);
        List<BookingDto> futureBookings = bookingService.getUserBookings(userId, BookingStatus.FUTURE);
        assertEquals(1, futureBookings.size());
    }

    @Test
    void testGetUserBookings_WAITING() {
        BookingRequestDto waitingRequest = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        bookingService.createBooking(userId, waitingRequest);
        List<BookingDto> waitingBookings = bookingService.getUserBookings(userId, BookingStatus.WAITING);
        assertEquals(1, waitingBookings.size());
    }

    @Test
    void testGetUserBookings_REJECTED() {
        BookingRequestDto rejectedRequest = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        BookingDto rejectedBooking = bookingService.createBooking(userId, rejectedRequest);
        bookingService.updateBookingStatus(rejectedBooking.getId(), userId, false);
        List<BookingDto> rejectedBookings = bookingService.getUserBookings(userId, BookingStatus.REJECTED);
        assertEquals(1, rejectedBookings.size());
    }

    @Test
    void testGetOwnerBookings_ALL() {
        bookingService.createBooking(userId, new BookingRequestDto(itemId, startDate, endDate));
        bookingService.createBooking(userId, new BookingRequestDto(itemId, startDate.plusDays(3), endDate.plusDays(3)));
        List<BookingDto> allOwnerBookings = bookingService.getOwnerBookings(userId, BookingStatus.ALL);
        assertEquals(2, allOwnerBookings.size());
    }

    @Test
    void testGetOwnerBookings_PAST() {
        LocalDateTime pastStart = LocalDateTime.now().minusDays(2);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(1);
        BookingRequestDto pastRequest = new BookingRequestDto(
                itemId,
                pastStart,
                pastEnd
        );
        BookingDto pastBooking = bookingService.createBooking(userId, pastRequest);
        bookingService.updateBookingStatus(pastBooking.getId(), userId, true);
        List<BookingDto> pastOwnerBookings = bookingService.getOwnerBookings(userId, BookingStatus.PAST);
        assertEquals(1, pastOwnerBookings.size());
    }

    @Test
    void testGetOwnerBookings_CURRENT() {
        LocalDateTime currentStart = LocalDateTime.now().minusDays(1);
        LocalDateTime currentEnd = LocalDateTime.now().plusDays(1);
        BookingRequestDto currentRequest = new BookingRequestDto(
                itemId,
                currentStart,
                currentEnd
        );
        BookingDto currentBooking = bookingService.createBooking(userId, currentRequest);
        bookingService.updateBookingStatus(currentBooking.getId(), userId, true);
        List<BookingDto> currentOwnerBookings = bookingService.getOwnerBookings(userId, BookingStatus.CURRENT);
        assertEquals(1, currentOwnerBookings.size());
    }

    @Test
    void testGetOwnerBookings_FUTURE() {
        BookingRequestDto futureRequest = new BookingRequestDto(
                itemId,
                startDate.plusDays(5),
                endDate.plusDays(5)
        );
        BookingDto futureBooking = bookingService.createBooking(userId, futureRequest);
        bookingService.updateBookingStatus(futureBooking.getId(), userId, true);
        List<BookingDto> futureOwnerBookings = bookingService.getOwnerBookings(userId, BookingStatus.FUTURE);
        assertEquals(1, futureOwnerBookings.size());
    }

    @Test
    void testGetOwnerBookings_WAITING() {
        BookingRequestDto waitingRequest = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        bookingService.createBooking(userId, waitingRequest);
        List<BookingDto> waitingOwnerBookings = bookingService.getOwnerBookings(userId, BookingStatus.WAITING);
        assertEquals(1, waitingOwnerBookings.size());
    }

    @Test
    void testGetOwnerBookings_REJECTED() {
        BookingRequestDto rejectedRequest = new BookingRequestDto(
                itemId,
                startDate,
                endDate
        );
        BookingDto rejectedBooking = bookingService.createBooking(userId, rejectedRequest);
        bookingService.updateBookingStatus(rejectedBooking.getId(), userId, false);
        List<BookingDto> rejectedOwnerBookings = bookingService.getOwnerBookings(userId, BookingStatus.REJECTED);
        assertEquals(1, rejectedOwnerBookings.size());
    }

    @Test
    void testGetUserBookings_NonExistentUser() {
        assertThatThrownBy(() ->
                bookingService.getUserBookings(599L, BookingStatus.ALL)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testGetOwnerBookings_NonExistentOwner() {
        assertThatThrownBy(() ->
                bookingService.getOwnerBookings(9999L, BookingStatus.ALL)
        ).isInstanceOf(NotFoundException.class);
    }

}