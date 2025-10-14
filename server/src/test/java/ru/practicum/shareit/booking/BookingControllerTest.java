package ru.practicum.shareit.booking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper objectMapper;
    private BookingRequestDto bookingRequestDto;
    private BookingDto bookingDto;
    private ItemDto itemDto;
    private UserDto userDto;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-ID";

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now());
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setStatus(BookingStatus.WAITING);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(bookingDto);

        ResultActions result = mockMvc.perform(
                post("/bookings")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
        );
            result.andExpect(status().isCreated())
            .andExpect(jsonPath("id").value(1))
            .andExpect(jsonPath("status").value("WAITING"));
    }

    @Test
    void testUpdateBookingStatus() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        ResultActions result = mockMvc.perform(
                patch("/bookings/1")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("approved", "true")
        );
            result.andExpect(status().isOk())
            .andExpect(jsonPath("id").value(1))
            .andExpect(jsonPath("status").value("APPROVED"));
    }

    @Test
    void testGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        ResultActions result = mockMvc.perform(
                get("/bookings/1")
                        .header(X_SHARER_USER_ID, 1L)
        );
            result.andExpect(status().isOk())
            .andExpect(jsonPath("id").value(1))
            .andExpect(jsonPath("item.name").value("Test Item"));
    }

    @Test
    void testGetUserBookings() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.getUserBookings(anyLong(), any(BookingStatus.class)))
                .thenReturn(bookings);

        ResultActions result = mockMvc.perform(
                get("/bookings")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("status", "ALL")
        );
            result.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetOwnerBookings() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.getOwnerBookings(anyLong(), any(BookingStatus.class)))
                .thenReturn(bookings);

        ResultActions result = mockMvc.perform(
                get("/bookings/owner")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("status", "ALL")
        );
            result.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].status").value("WAITING"));
    }
}