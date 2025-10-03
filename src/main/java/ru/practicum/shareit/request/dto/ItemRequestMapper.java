package ru.practicum.shareit.request.dto;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDto toDto(@NonNull ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester().getId(),
                itemRequest.getCreated()
        );
    }

    public ItemRequest toEntity(@NonNull ItemRequestDto requestDto,
        @NonNull User requester) {
        return new ItemRequest(
                requestDto.getId(),
                requestDto.getDescription(),
                requester,
                requestDto.getCreated()
        );
    }
}
