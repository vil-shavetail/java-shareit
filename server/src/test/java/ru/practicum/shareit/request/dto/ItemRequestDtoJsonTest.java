package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemRequestAnswerDto;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Test description");
        request.setRequesterId(123L);
        request.setCreated(LocalDateTime.now());

        ItemRequestAnswerDto answer = new ItemRequestAnswerDto();
        answer.setId(1L);
        answer.setName("Test item");

        request.setItems(List.of(answer));

        String json = objectMapper.writeValueAsString(request);

        for (String s : Arrays.asList("\"id\":1", "\"description\":\"Test description\"", "\"requesterId\":123", "\"created\"", "\"items\"")) {
            assertThat(json).contains(s);
        }
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\n" +
                      "    \"id\": 1,\n" +
                      "    \"description\": \"Test description\",\n" +
                      "    \"requesterId\": 123,\n" +
                      "    \"created\": \"2023-10-14T12:00:00\",\n" +
                      "    \"items\": [\n" +
                      "        {\n" +
                      "            \"id\": 1,\n" +
                      "            \"itemId\": 1\n" +
                      "        }\n" +
                      "    ]\n" +
                      "}\n";

        ItemRequestDto request = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Test description");
        assertThat(request.getRequesterId()).isEqualTo(123L);
        assertThat(request.getItems().size()).isEqualTo(1);
    }

    @Test
    void testEmptyObject() throws Exception {
        ItemRequestDto request = new ItemRequestDto();

        String json = objectMapper.writeValueAsString(request);

        assertThat(json).isEqualTo("{\"id\":null,\"description\":null,\"requesterId\":null,\"created\":null,\"items\":null}");
    }

    @Test
    void testNullValues() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(null);
        request.setDescription(null);
        request.setRequesterId(null);
        request.setCreated(null);
        request.setItems(null);

        String json = objectMapper.writeValueAsString(request);

        assertThat(json).isEqualTo("{\"id\":null,\"description\":null,\"requesterId\":null,\"created\":null,\"items\":null}");
    }

    @Test
    void testDeserializationWithMissingFields() throws Exception {
        String json = "{\n" +
                      "    \"description\": \"Test description\",\n" +
                      "    \"items\": [\n" +
                      "        {\n" +
                      "            \"id\": 1\n" +
                      "        }\n" +
                      "    ]\n" +
                      "}\n";

        ItemRequestDto request = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(request.getId()).isNull();
        assertThat(request.getDescription()).isEqualTo("Test description");
        assertThat(request.getRequesterId()).isNull();
        assertThat(request.getCreated()).isNull();
        assertThat(request.getItems().get(0).getId()).isEqualTo(1L);
    }

}