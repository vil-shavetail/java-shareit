package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryItemService implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        log.info("Creating a thing. Input data:  {}, ID owner: {}", itemDto, ownerId);
        if (!userRepository.existsById(ownerId)) {
            log.warn("An attempt to create a new item by non-existing user with id: {} failed.",  ownerId);
            throw new NotFoundException("User with id: " + ownerId + "not found");
        }
        Item item = itemMapper.toEntity(itemDto, ownerId);
        ItemDto createdItem = itemMapper.toDto(itemRepository.save(item));
        log.info("A new item has been created: {}", createdItem);
        return createdItem;
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        log.info("Updating a thing. ID: {}, Data: {}, Owner's ID: {}", itemId, itemDto, ownerId);
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!existingItem.getOwnerId().equals(ownerId)) {
            log.warn("An attempt to update the item failed. Item ID: {}, User ID: {}", itemId, ownerId);
            throw new ForbiddenException("Only owner can update item");
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(existingItem::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(existingItem::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(existingItem::setAvailable);

        ItemDto updatedItem = itemMapper.toDto(itemRepository.save(existingItem));
        log.info("The item has been updated: {}", updatedItem);
        return updatedItem;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Requesting an item by ID: {}", itemId);
        ItemDto item = itemRepository.findById(itemId)
                .map(itemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        log.info("Item found: {}", item);
        return item;
    }

    @Override
    public List<ItemDto> getAllUserItems(Long ownerId) {
        log.info("Request all the user's items. Owner's ID: {}", ownerId);
        List<ItemDto> items = itemRepository.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        log.info("Items found {} for user ID: {}", items.size(), ownerId);
        return items;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Search for items on request: '{}'", text);
        if (text.isBlank()) {
            log.info("Empty search query, we return an empty list");
            return Collections.emptyList();
        }
        List<ItemDto> results = itemRepository.searchAvailableItems(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        log.info("Found {} search results '{}'", results.size(), text);
        return results;
    }
}