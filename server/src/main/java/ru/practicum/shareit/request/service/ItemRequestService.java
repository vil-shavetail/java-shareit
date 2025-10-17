package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requesterId);

    ItemRequestDto getItemRequestById(Long requestId);

    List<ItemRequestDto> getAllUserItemRequests(Long requesterId);

    List<ItemRequestDto> getAllRequests();

}
