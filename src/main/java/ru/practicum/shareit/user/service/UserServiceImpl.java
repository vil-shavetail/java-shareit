package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Creating a user. Input data: {}", userDto);
        User user = UserMapper.toEntity(userDto);
        UserDto createdUser = UserMapper.toDto(userRepository.save(user));
        log.info("The user was successfully created: {}", createdUser);
        return createdUser;
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        log.debug("User update. ID: {}, Input data: {}", id, userDto);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", id);
                    return new NotFoundException("User with id: " + id + "not found");
                });

        if (!existingUser.getEmail().equals(userDto.getEmail())
                && userRepository.emailExists(userDto.getEmail())) {
            log.warn("Trying to use a busy email: {}", userDto.getEmail());
            throw new DuplicateEmailException("Email already in use: " + userDto.getEmail());
        }

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        UserDto updatedUser = UserMapper.toDto(userRepository.save(existingUser));
        log.info("The user has been updated: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("User request by ID: {}", id);
        UserDto user = userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("A non-existent user was requested, ID: {}", id);
                    return new NotFoundException("User not found with id: " + id);
                });
        log.info("A user has been found: {}", user);
        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Request for all users");
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
        log.info("Users found: {}", users.size());
        return users;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting a user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("Attempt to delete a non-existent user ID: {}", id);
            throw new NotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User with ID {} successfully deleted", id);
    }

}
