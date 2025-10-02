package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService{
    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        return null;
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllUserItemRequests(Long requesterId) {
        return List.of();
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        return List.of();
    }
}
