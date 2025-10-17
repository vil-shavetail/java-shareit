package ru.practicum.shareit.booking.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;
import java.util.Arrays;

@JsonTest
class BookingRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto request = new BookingRequestDto(
                1L,
                now,
                now.plusDays(2)
        );

        String json = objectMapper.writeValueAsString(request);

        for (String s : Arrays.asList("\"itemId\":1", "\"start\":\"" + now + "\"", "\"end\":\"" + now.plusDays(2) + "\"")) {
            assertThat(json).contains(s);
        }
    }

    @Test
    void testDeserialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String json = String.format("{\n" +
                                    "    \"itemId\": 1,\n" +
                                    "    \"start\": \"%s\",\n" +
                                    "    \"end\": \"%s\"\n" +
                                    "}\n", now, now.plusDays(2));

        BookingRequestDto request = objectMapper.readValue(json, BookingRequestDto.class);

        assertThat(request.getItemId()).isEqualTo(1L);
        assertThat(request.getStart()).isEqualTo(now);
        assertThat(request.getEnd()).isEqualTo(now.plusDays(2));
    }

    @Test
    void testEmptyObject() throws Exception {
        BookingRequestDto request = new BookingRequestDto();

        String json = objectMapper.writeValueAsString(request);

        assertThat(json).isEqualTo("{\"itemId\":null,\"start\":null,\"end\":null}");
    }

    @Test
    void testNullValues() throws Exception {
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(null);
        request.setStart(null);
        request.setEnd(null);

        String json = objectMapper.writeValueAsString(request);

        assertThat(json).isEqualTo("{\"itemId\":null,\"start\":null,\"end\":null}");
    }

    @Test
    void testDeserializationWithMissingFields() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String json = String.format("{\n" +
                                    "    \"start\": \"%s\",\n" +
                                    "    \"end\": \"%s\"\n" +
                                    "}\n", now, now.plusDays(2));

        BookingRequestDto request = objectMapper.readValue(json, BookingRequestDto.class);

        assertThat(request.getItemId()).isNull();
        assertThat(request.getStart()).isEqualTo(now);
        assertThat(request.getEnd()).isEqualTo(now.plusDays(2));
    }
}
