package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@UtilityClass
public final class ItemMapper {

    public ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                item.getOwner().getId(),
                null,
                null,
                null
        );
    }

    public ItemRequestAnswerDto toItemRequestAnswerDto(Item item) {
        return new ItemRequestAnswerDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }

    public Item toEntity(ItemDto itemDto, User owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequestId()
        );
    }
}