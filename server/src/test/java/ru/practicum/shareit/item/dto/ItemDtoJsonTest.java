package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setRequestId(123L);
        item.setOwnerId(456L);

        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Excellent item!");
        comment.setAuthorName("Ivan Petrov");

        item.setComments(List.of(comment));

        BookingRequestDto booking = new BookingRequestDto();
        booking.setItemId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));

        item.setLastBooking(booking);
        item.setNextBooking(booking);

        String json = objectMapper.writeValueAsString(item);

        for (String s : Arrays.asList("\"id\":1", "\"name\":\"Test Item\"", "\"description\":\"Item description\"", "\"available\":true", "\"requestId\":123", "\"ownerId\":456", "\"comments\"", "\"lastBooking\"", "\"nextBooking\"")) {
            assertThat(json).contains(s);
        }
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "    {\n" +
                      "        \"id\": 1,\n" +
                      "        \"name\": \"Test Item\",\n" +
                      "        \"description\": \"Item description\",\n" +
                      "        \"available\": true,\n" +
                      "        \"requestId\": 123,\n" +
                      "        \"ownerId\": 456,\n" +
                      "        \"comments\": [\n" +
                      "            {\n" +
                      "                \"id\": 1,\n" +
                      "                \"text\": \"Excellent item!\",\n" +
                      "                \"authorName\": \"Ivan Petrov\"\n" +
                      "            }\n" +
                      "        ],\n" +
                      "        \"lastBooking\": {\n" +
                      "            \"id\": 1,\n" +
                      "            \"itemId\": 1,\n" +
                      "            \"startDate\": \"2023-10-14T12:00:00\",\n" +
                      "            \"endDate\": \"2023-10-16T12:00:00\"\n" +
                      "        },\n" +
                      "        \"nextBooking\": {\n" +
                      "            \"id\": 1,\n" +
                      "            \"itemId\": 1,\n" +
                      "            \"startDate\": \"2023-10-14T12:00:00\",\n" +
                      "            \"endDate\": \"2023-10-16T12:00:00\"\n" +
                      "        }\n" +
                      "    }\n";

        ItemDto item = objectMapper.readValue(json, ItemDto.class);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Test Item");
        assertThat(item.getDescription()).isEqualTo("Item description");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getRequestId()).isEqualTo(123L);
        assertThat(item.getOwnerId()).isEqualTo(456L);
        assertThat(item.getComments().size()).isEqualTo(1);
        assertThat(item.getLastBooking().getItemId()).isEqualTo(1L);
        assertThat(item.getComments().get(0).getText()).isEqualTo("Excellent item!");
        assertThat(item.getComments().get(0).getAuthorName()).isEqualTo("Ivan Petrov");
    }

    @Test
    void testEmptyObject() throws Exception {
        ItemDto item = new ItemDto();

        String json = objectMapper.writeValueAsString(item);

        assertThat(json).isEqualTo("{\"id\":null,\"name\":null,\"description\":null,\"available\":null,\"requestId\":null,\"ownerId\":null,\"comments\":null,\"lastBooking\":null,\"nextBooking\":null}");
    }

    @Test
    void testNullValues() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(null);
        item.setName(null);
        item.setDescription(null);
        item.setAvailable(null);
        item.setRequestId(null);
        item.setOwnerId(null);
        item.setComments(null);
        item.setLastBooking(null);
        item.setNextBooking(null);

        String json = objectMapper.writeValueAsString(item);

        assertThat(json).isEqualTo("{\"id\":null,\"name\":null,\"description\":null,\"available\":null,\"requestId\":null,\"ownerId\":null,\"comments\":null,\"lastBooking\":null,\"nextBooking\":null}");
    }

    @Test
    void testDeserializationWithMissingFields() throws Exception {
        String json = "{\n" +
                      "    \"name\": \"Test Item\",\n" +
                      "    \"description\": \"Item description\",\n" +
                      "    \"comments\": [\n" +
                      "        {\n" +
                      "            \"text\": \"Excellent item!\",\n" +
                      "            \"authorName\": \"Ivan Petrov\"\n" +
                      "        }\n" +
                      "    ]\n" +
                      "}\n";

        ItemDto item = objectMapper.readValue(json, ItemDto.class);

        assertThat(item.getId()).isNull();
        assertThat(item.getName()).isEqualTo("Test Item");
        assertThat(item.getDescription()).isEqualTo("Item description");
        assertThat(item.getAvailable()).isNull();
        assertThat(item.getRequestId()).isNull();
        assertThat(item.getOwnerId()).isNull();
        assertThat(item.getComments().size()).isEqualTo(1);
        assertThat(item.getLastBooking()).isNull();
        assertThat(item.getNextBooking()).isNull();
    }

    @Test
    void testEmptyCommentsList() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setComments(List.of());

        String json = objectMapper.writeValueAsString(item);

        for (String s : Arrays.asList("\"id\":1", "\"comments\":[]")) {
            assertThat(json).contains(s);
        }
    }

    @Test
    void testNullComments() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setComments(null);

        String json = objectMapper.writeValueAsString(item);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"comments\":null");
    }

    @Test
    void testBooleanValues() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setAvailable(true);

        String json = objectMapper.writeValueAsString(item);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"available\":true");

        item.setAvailable(false);
        json = objectMapper.writeValueAsString(item);
        assertThat(json).contains("\"available\":false");
    }

}