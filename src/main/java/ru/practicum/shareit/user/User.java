package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class User {
    private Long id;
    private String name;
    private String email;
}