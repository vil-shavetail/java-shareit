package ru.practicum.shareit.item;

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

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    private ObjectMapper objectMapper;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test comment");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        ResultActions result = mockMvc.perform(
                post("/items")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto))
        );
            result.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("Test Item"));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        ResultActions result = mockMvc.perform(
                patch("/items/1")
                        .header(ItemController.X_SHARER_USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto))
        );
            result.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1));
    }

    @Test
    void testGetAllUserItems() throws Exception {
        List<ItemDto> items = List.of(itemDto);
        when(itemService.getAllUserItems(anyLong()))
                .thenReturn(items);

        ResultActions result = mockMvc.perform(
                get("/items")
                        .header(ItemController.X_SHARER_USER_ID, 1L)
        );
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testSearchItems() throws Exception {
        List<ItemDto> items = List.of(itemDto);
        when(itemService.searchItems(anyString()))
                .thenReturn(items);

        ResultActions result = mockMvc.perform(
                get("/items/search")
                        .param("text", "test")
        );
            result.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Test Item"));
    }

    @Test
    void testCreateComment() throws Exception {
        when(commentService.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        ResultActions result = mockMvc.perform(
                post("/items/1/comment")
                        .header(ItemController.X_SHARER_USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto))
        );
            result.andExpect(status().isCreated())
            .andExpect(jsonPath("id").value(1))
            .andExpect(jsonPath("text").value("Test comment"));
    }
}