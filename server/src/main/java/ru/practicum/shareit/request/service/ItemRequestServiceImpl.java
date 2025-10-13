package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        log.info("Creating request for item, author: {}", requesterId);
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty()) {
            throw new ValidateException("The description of the item request cannot be empty");
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
    public ItemRequestDto getItemRequestById(Long requestId) {
        log.info("Getting item request by id: {}", requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).
                orElseThrow(() -> new NotFoundException("Item request with id: " + requestId + " not found"));
        log.info("Item request found: {}", itemRequest.getId());
        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
        List<ItemRequestAnswerDto> answers = itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toItemRequestAnswerDto)
                .toList();
        itemRequestDto.setItems(answers);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllUserItemRequests(Long requesterId) {
        log.info("Getting all item requests for user: {}", requesterId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(requesterId);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<Item> allItems = itemRepository.findAllByRequestIdIn(requestIds);
        Map<Long, List<Item>> itemsByRequestId = allItems.stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
        List<ItemRequestDto> requestDtos = requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.toDto(request);
                    List<ItemRequestAnswerDto> answers = itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())
                            .stream()
                            .map(ItemMapper::toItemRequestAnswerDto)
                            .toList();
                    dto.setItems(answers);
                    return dto;
                })
                .toList();
        log.info("Found {} items requests for user: {}", requestDtos.size(), requesterId);
        return requestDtos;
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
