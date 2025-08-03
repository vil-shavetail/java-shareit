package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    public UserDto createUser(UserDto userDto);

    public UserDto updateUser(Long id, UserDto userDto);

    public UserDto getUserById(Long id);

    public List<UserDto> getAllUsers();

    public void deleteUser(Long id);
}
