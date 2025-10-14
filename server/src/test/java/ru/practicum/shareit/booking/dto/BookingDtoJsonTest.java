package ru.practicum.shareit.booking.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Arrays;

@JsonTest
class BookingDtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto createTestBooking() {
        UserDto booker = new UserDto(1L, "Booker", "booker@example.com");
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setRequestId(123L);
        item.setOwnerId(456L);


        return new BookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                booker,
                BookingStatus.APPROVED
        );
    }

    @Test
    void testSerialization() throws Exception {
        BookingDto booking = createTestBooking();

        String json = objectMapper.writeValueAsString(booking);

        for (String s : Arrays.asList("\"id\":1", "\"status\":\"APPROVED\"", "\"booker\"", "\"item\"")) {
            assertThat(json).contains(s);
        }
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\n" +
                      "    \"id\": 1,\n" +
                      "    \"start\": \"2025-10-14T12:00:00\",\n" +
                      "    \"end\": \"2025-10-16T12:00:00\",\n" +
                      "    \"status\": \"WAITING\",\n" +
                      "    \"booker\": {\n" +
                      "        \"id\": 1,\n" +
                      "        \"name\": \"Booker\",\n" +
                      "        \"email\": \"booker@example.com\"\n" +
                      "    },\n" +
                      "    \"item\": {\n" +
                      "        \"id\": 1,\n" +
                      "        \"name\": \"Item Name\",\n" +
                      "        \"description\": \"Description\"\n" +
                      "    }\n" +
                      "}\n";

        BookingDto booking = objectMapper.readValue(json, BookingDto.class);

        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(booking.getBooker().getName()).isEqualTo("Booker");
        assertThat(booking.getItem().getName()).isEqualTo("Item Name");
    }

    @Test
    void testEmptyObject() throws Exception {
        BookingDto booking = new BookingDto();

        String json = objectMapper.writeValueAsString(booking);

        assertThat(json).isEqualTo("{\"id\":null,\"start\":null,\"end\":null,\"item\":null,\"booker\":null,\"status\":null}");
    }

    @Test
    void testNullValues() throws Exception {
        BookingDto booking = createTestBooking();
        booking.setItem(null);
        booking.setBooker(null);
        booking.setStart(null);
        booking.setEnd(null);
        booking.setStatus(null);

        String json = objectMapper.writeValueAsString(booking);

        assertThat(json).isEqualTo("{\"id\":1,\"start\":null,\"end\":null,\"item\":null,\"booker\":null,\"status\":null}");
    }

    @Test
    void testDateSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 10, 14, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 16, 12, 0);

        BookingDto booking = new BookingDto();
        booking.setStart(start);
        booking.setEnd(end);

        String json = objectMapper.writeValueAsString(booking);

        for (String s : Arrays.asList("\"start\":\"2025-10-14T12:00:00\"", "\"end\":\"2025-10-16T12:00:00\"")) {
            assertThat(json).contains(s);
        }
    }

    @Test
    void testDateDeserialization() throws Exception {
        String json = "{\n" +
                      "    \"start\": \"2025-10-14T12:00:00\",\n" +
                      "    \"end\": \"2025-10-16T12:00:00\"\n" +
                      "}\n";

        BookingDto booking = objectMapper.readValue(json, BookingDto.class);

        assertThat(booking.getStart()).isEqualTo(LocalDateTime.of(2025, 10, 14, 12, 0));
        assertThat(booking.getEnd()).isEqualTo(LocalDateTime.of(2025, 10, 16, 12, 0));
    }

    @Test
    void testEnumSerialization() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setStatus(BookingStatus.APPROVED);

        String json = objectMapper.writeValueAsString(booking);

        assertThat(json).contains("\"status\":\"APPROVED\"");
    }

}