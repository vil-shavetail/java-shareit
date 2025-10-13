package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestAnswerDto {
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    private Long ownerId;
}
