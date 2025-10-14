package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.util.Arrays;

@JsonTest
class ItemRequestAnswerDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        ItemRequestAnswerDto answer = new ItemRequestAnswerDto();
        answer.setId(1L);
        answer.setName("Test Item");
        answer.setOwnerId(123L);

        String json = objectMapper.writeValueAsString(answer);

        for (String s : Arrays.asList("\"id\":1", "\"name\":\"Test Item\"", "\"ownerId\":123")) {
            assertThat(json).contains(s);
        }
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\n" +
                      "    \"id\": 1,\n" +
                      "    \"name\": \"Test Item\",\n" +
                      "    \"ownerId\": 123\n" +
                      "}\n";

        ItemRequestAnswerDto answer = objectMapper.readValue(json, ItemRequestAnswerDto.class);

        assertThat(answer.getId()).isEqualTo(1L);
        assertThat(answer.getName()).isEqualTo("Test Item");
        assertThat(answer.getOwnerId()).isEqualTo(123L);
    }

    @Test
    void testEmptyObject() throws Exception {
        ItemRequestAnswerDto answer = new ItemRequestAnswerDto();

        String json = objectMapper.writeValueAsString(answer);

        assertThat(json).isEqualTo("{\"id\":null,\"name\":null,\"ownerId\":null}");
    }

    @Test
    void testNullValues() throws Exception {
        ItemRequestAnswerDto answer = new ItemRequestAnswerDto();
        answer.setId(null);
        answer.setName(null);
        answer.setOwnerId(null);

        String json = objectMapper.writeValueAsString(answer);

        assertThat(json).isEqualTo("{\"id\":null,\"name\":null,\"ownerId\":null}");
    }

    @Test
    void testDeserializationWithMissingFields() throws Exception {
        String json = "{\n" +
                      "    \"name\": \"Test Item\"\n" +
                      "}\n";

        ItemRequestAnswerDto answer = objectMapper.readValue(json, ItemRequestAnswerDto.class);

        assertThat(answer.getId()).isNull();
        assertThat(answer.getName()).isEqualTo("Test Item");
        assertThat(answer.getOwnerId()).isNull();
    }

    @Test
    void testSpecialCharacters() throws Exception {
        ItemRequestAnswerDto answer = new ItemRequestAnswerDto();
        answer.setId(2L);
        answer.setName("Test \"Item\" with special characters");
        answer.setOwnerId(456L);

        String json = objectMapper.writeValueAsString(answer);

        for (String s : Arrays.asList("\"id\":2", "\"name\":\"Test \\\"Item\\\" with special characters\"", "\"ownerId\":456")) {
            assertThat(json).contains(s);
        }
    }

}