package ru.practicum.shareit.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.util.Arrays;

@JsonTest
class UserDtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        String json = objectMapper.writeValueAsString(user);

        for (String s : Arrays.asList("\"id\":1", "\"name\":\"John Doe\"", "\"email\":\"john.doe@example.com\"")) {
            assertThat(json).contains(s);
        }
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\n" +
                "   \"id\": \"1\",\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"email\": \"john.doe@example.com\"\n" +
                "}";

        UserDto user = objectMapper.readValue(json, UserDto.class);

        // Проверяем результат
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testEmptyObject() throws Exception {
        UserDto user = new UserDto();

        String json = objectMapper.writeValueAsString(user);

        assertThat(json).isEqualTo("{\"id\":null,\"name\":null,\"email\":null}");
    }

    @Test
    void testNullValues() throws Exception {
        UserDto user = new UserDto();
        user.setId(null);
        user.setName(null);
        user.setEmail(null);

        String json = objectMapper.writeValueAsString(user);

        assertThat(json).isEqualTo("{\"id\":null,\"name\":null,\"email\":null}");
    }

    @Test
    void testDeserializationWithMissingFields() throws Exception {
        String json = "{\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"email\": \"john.doe@example.com\"\n" +
                "}";

        UserDto user = objectMapper.readValue(json, UserDto.class);

        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
    }
}