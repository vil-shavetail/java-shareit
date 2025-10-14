package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateUser() throws Exception {

        when(userService.createUser(userDto))
                .thenReturn(userDto);

        ResultActions result = mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto))
        );

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("Test User"))
                .andExpect(jsonPath("email").value("test@example.com"));
    }

    @Test
    void testGetUser() throws Exception {
        when(userService.getUserById(1L))
                .thenReturn(userDto);

        ResultActions result = mockMvc.perform(
                get("/users/1")
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("Test User"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<UserDto> users = List.of(userDto);
        when(userService.getAllUsers())
                .thenReturn(users);

        ResultActions result = mockMvc.perform(
                get("/users")
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test User"));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(1L, userDto))
                .thenReturn(userDto);

        ResultActions result = mockMvc.perform(
                patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto))
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("Test User"))
                .andExpect(jsonPath("email").value("test@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        ResultActions result = mockMvc.perform(
                delete("/users/1")
        );

        result.andExpect(status().isNoContent());
    }

}