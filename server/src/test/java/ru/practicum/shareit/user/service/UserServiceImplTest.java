package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService userService;
    private final UserRepository userRepository;

    @Test
    void testCreateUser() {
        UserDto user = new UserDto(null, "John Doe", "john.doe@example.com");
        UserDto createdUser = userService.createUser(user);
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getName()).isEqualTo(user.getName());
        assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void testUpdateUser() {
        UserDto johnDoe = new UserDto(null, "John Doe", "john.doe@example.com");
        UserDto existingUser = userService.createUser(johnDoe);
        UserDto updatedUserDto = new UserDto(existingUser.getId(),
                "Jane Doe",
                "jane.doe@example.com");

        UserDto result = userService.updateUser(existingUser.getId(), updatedUserDto);

        assertThat(result.getId()).isEqualTo(existingUser.getId());
        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    void testGetUserById() {
        UserDto user = new UserDto(null, "John Doe", "john.doe@example.com");
        UserDto createdUser = userService.createUser(user);
        Long userId = createdUser.getId();
        createdUser = userService.getUserById(userId);
        assertThat(createdUser.getId()).isEqualTo(userId);
        assertThat(createdUser.getName()).isEqualTo("John Doe");
        assertThat(createdUser.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testGetAllUsers() {
        UserDto johnDoe = new UserDto(null, "John Doe", "john.doe@example.com");
        UserDto jadeDoe = new UserDto(null, "Jade Doe", "jade.doe@example.com");
        userService.createUser(johnDoe);
        userService.createUser(jadeDoe);

        List<UserDto> userList = userService.getAllUsers();

        UserDto john = userList.get(0);
        assertThat(john).isNotNull()
                .hasFieldOrPropertyWithValue("email", "john.doe@example.com")
                .hasFieldOrPropertyWithValue("name", "John Doe");
        UserDto jade = userList.get(1);
        assertThat(jade).isNotNull()
                .hasFieldOrPropertyWithValue("email", "jade.doe@example.com")
                .hasFieldOrPropertyWithValue("name", "Jade Doe");
    }

    @Test
    void testDeleteUser() {
        UserDto user = new UserDto(null, "John Doe", "john.doe@example.com");
        UserDto createdUser = userService.createUser(user);
        Long userId = createdUser.getId();
        assertThat(userRepository.existsById(userId)).isTrue();
        userService.deleteUser(userId);
        assertThat(userRepository.existsById(userId)).isFalse();
    }

    @Test
    void testThrowNotFoundExceptionWhenUserNotFoundById() {
        Long userId = 563L;
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found with id: 563");
    }

    @Test
    void testThrowNotFoundExceptionWhenTryToDeleteNonExistentUser() {
        Long userId = 564L;
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found with id: 564");
    }

    @Test
    void testUpdateNonExistentUser() {
        UserDto updateDto = new UserDto(565L, "John Wick", "john.wick@example.com");
        assertThatThrownBy(() ->
                userService.updateUser(565L, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id: 565 not found");
    }

    @Test
    void testUpdateWithNullData() {
        UserDto johnDoe = new UserDto(null, "John Doe", "john.doe@example.com");
        UserDto existingUser = userService.createUser(johnDoe);
        UserDto updateDto = new UserDto(existingUser.getId(), null, null);
        UserDto result = userService.updateUser(existingUser.getId(), updateDto);
        assertThat(result.getName()).isEqualTo(johnDoe.getName());
        assertThat(result.getEmail()).isEqualTo(johnDoe.getEmail());
    }

    @Test
    void testUpdateWithDuplicateEmail() {
        UserDto johnDoe = new UserDto(null, "John Doe", "john.doe@example.com");
        UserDto existingUser = userService.createUser(johnDoe);
        UserDto jadeDoe = new UserDto(null, "Jade Doe", "jade.dow@example.com");
        userService.createUser(jadeDoe);
        UserDto updateDto = new UserDto(existingUser.getId(),
                "Joshua Doe",
                "jade.dow@example.com");
        assertThatThrownBy(() ->
                userService.updateUser(existingUser.getId(), updateDto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already in use: jade.dow@example.com");
    }

    @Test
    void testUpdateUserPartialName() {
        UserDto johnDoe = new UserDto(null, "John Doe", "john.doe@example.com");
        UserDto existingUser = userService.createUser(johnDoe);
        UserDto updatedUserDto = new UserDto(existingUser.getId(),
                "Jane Doe",
                null);
        UserDto result = userService.updateUser(existingUser.getId(), updatedUserDto);
        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getEmail()).isEqualTo(existingUser.getEmail());
    }

    @Test
    void testUpdateUserPartialEmail() {
        UserDto johnDoe = new UserDto(null, "John Doe", "john.doe@example.com");
        UserDto existingUser = userService.createUser(johnDoe);
        UserDto updatedUserDto = new UserDto(existingUser.getId(),
                null,
                "john.dow.new@example.com");
        UserDto result = userService.updateUser(existingUser.getId(), updatedUserDto);
        assertThat(result.getName()).isEqualTo(existingUser.getName());
        assertThat(result.getEmail()).isEqualTo("john.dow.new@example.com");
    }
}