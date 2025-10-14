package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import java.time.LocalDateTime;

@JsonTest
class CommentDtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Excellent item!");
        comment.setAuthorName("Ivan Petrov");
        comment.setCreated(LocalDateTime.of(2023, 10, 14, 12, 0));

        String json = objectMapper.writeValueAsString(comment);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Excellent item!\"");
        assertThat(json).contains("\"authorName\":\"Ivan Petrov\"");
        assertThat(json).contains("\"created\":\"2023-10-14T12:00:00\"");
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\n" +
                      "    \"id\": 1,\n" +
                      "    \"text\": \"Excellent item!\",\n" +
                      "    \"authorName\": \"Ivan Petrov\",\n" +
                      "    \"created\": \"2023-10-14T12:00:00\"\n" +
                      "}\n";

        CommentDto comment = objectMapper.readValue(json, CommentDto.class);

        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getText()).isEqualTo("Excellent item!");
        assertThat(comment.getAuthorName()).isEqualTo("Ivan Petrov");
        assertThat(comment.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 14, 12, 0));
    }

    @Test
    void testEmptyObject() throws Exception {
        CommentDto comment = new CommentDto();

        String json = objectMapper.writeValueAsString(comment);

        assertThat(json).isEqualTo("{\"id\":null,\"text\":null,\"authorName\":null,\"created\":null}");
    }

    @Test
    void testNullValues() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(null);
        comment.setText(null);
        comment.setAuthorName(null);
        comment.setCreated(null);

        String json = objectMapper.writeValueAsString(comment);

        assertThat(json).isEqualTo("{\"id\":null,\"text\":null,\"authorName\":null,\"created\":null}");
    }

    @Test
    void testDeserializationWithMissingFields() throws Exception {
        String json = "{\n" +
                      "    \"text\": \"Excellent item!\",\n" +
                      "    \"authorName\": \"Ivan Petrov\"\n" +
                      "}\n";

        CommentDto comment = objectMapper.readValue(json, CommentDto.class);

        assertThat(comment.getId()).isNull();
        assertThat(comment.getText()).isEqualTo("Excellent item!");
        assertThat(comment.getAuthorName()).isEqualTo("Ivan Petrov");
        assertThat(comment.getCreated()).isNull();
    }

    @Test
    void testDateFormat() throws Exception {
        LocalDateTime date = LocalDateTime.of(2023, 10, 14, 12, 30, 45);
        CommentDto comment = new CommentDto();
        comment.setCreated(date);

        String json = objectMapper.writeValueAsString(comment);
        assertThat(json).contains("\"created\":\"2023-10-14T12:30:45\"");
    }

    @Test
    void testSpecialCharacters() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setText("Comment with character: \" ' < > &");
        comment.setAuthorName("User's Name");

        String json = objectMapper.writeValueAsString(comment);
        assertThat(json).contains("\"text\":\"Comment with character: \\\" ' < > &\"");
        assertThat(json).contains("\"authorName\":\"User's Name\"");
    }
}