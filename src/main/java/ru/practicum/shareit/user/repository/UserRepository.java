package ru.practicum.shareit.user.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    public List<User> findAll();

    public Optional<User> findById(Long id);

    public User save(User user);

    public void deleteById(Long id);

    boolean emailExists(@NotBlank(message = "Email cannot be empty") @Email(message = "Invalid email format") String email);

    boolean existsById(Long id);
}
