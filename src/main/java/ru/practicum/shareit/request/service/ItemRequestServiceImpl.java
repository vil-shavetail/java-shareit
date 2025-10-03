package ru.practicum.shareit.request.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        log.info("Creating request for item, author: {}", requesterId);
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty()) {
            throw new ValidationException("The description of the item request cannot be empty");
        }
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest itemRequest = ItemRequestMapper.toEntity(itemRequestDto, requester);
        itemRequest.setCreated(currentDateTime);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        log.info("Created item request: {}", savedItemRequest.getId());
        return ItemRequestMapper.toDto(savedItemRequest);
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId, Long requesterId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllUserItemRequests(Long requesterId) {
        return List.of();
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        log.info("Request for all itemRequests");
        List<ItemRequestDto> itemRequests = itemRequestRepository.findAll().stream()
                .map(ItemRequestMapper::toDto)
                .toList();
        log.info("ItemRequests found: {}", itemRequests.size());
        return itemRequests;
    }
}
