package ru.practicum.shareit.user.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean emailExists(@NotBlank(message = "Email cannot be empty") @Email(message = "Invalid email format") String email);
}
