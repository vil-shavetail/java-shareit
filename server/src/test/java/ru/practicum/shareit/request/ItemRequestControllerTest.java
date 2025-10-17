package ru.practicum.shareit.request;

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

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import java.util.Collections;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto testRequestDto;
    private ObjectMapper objectMapper;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        testRequestDto = new ItemRequestDto();
        testRequestDto.setId(1L);
        testRequestDto.setDescription("Test request");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.createRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(testRequestDto);

        ResultActions result = mockMvc.perform(
                post("/requests")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(testRequestDto))
        );

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("description").value("Test request"));
    }

    @Test
    void testGetItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(1L))
                .thenReturn(testRequestDto);

        ResultActions result = mockMvc.perform(
                get("/requests/1")
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("description").value("Test request"));
    }

    @Test
    void testGetAllUserItemRequests() throws Exception {
        List<ItemRequestDto> requests = Collections.singletonList(testRequestDto);
        when(itemRequestService.getAllUserItemRequests(1L))
                .thenReturn(requests);

        ResultActions result = mockMvc.perform(
                get("/requests")
                        .header(X_SHARER_USER_ID, 1L)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test request"));
    }

    @Test
    void testGetAllItemRequests() throws Exception {
        List<ItemRequestDto> requests = Collections.singletonList(testRequestDto);
        when(itemRequestService.getAllRequests())
                .thenReturn(requests);

        ResultActions result = mockMvc.perform(
                get("/requests/all")
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test request"));
    }
}