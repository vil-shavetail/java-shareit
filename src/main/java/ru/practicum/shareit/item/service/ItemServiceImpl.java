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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        log.info("Creating item for user: {}, item data: {}", ownerId, itemDto);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id: " + ownerId + " not found"));

        Item item = ItemMapper.toEntity(itemDto, owner);
        Item savedItem = itemRepository.save(item);

        log.info("Item created successfully: {}", savedItem.getId());
        return ItemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        log.info("Updating item: {}, with data: {}, for user: {}", itemId, itemDto, ownerId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + " not found"));

        if (!item.getOwner().getId().equals(ownerId)) {
            log.warn("Unauthorized update attempt for item: {}, by user: {}", itemId, ownerId);
            throw new ForbiddenException("Only owner can update item");
        }

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        Item updatedItem = itemRepository.save(item);
        log.info("Item updated successfully: {}", updatedItem.getId());
        return ItemMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Getting item by id: {}", itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + " not found"));

        log.info("Item found: {}", item.getId());
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllUserItems(Long ownerId) {
        log.info("Getting all items for user: {}", ownerId);

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<ItemDto> itemsDto = items.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} items for user: {}", itemsDto.size(), ownerId);
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Searching items by text: '{}'", text);

        if (text.isBlank()) {
            log.info("Empty search query, returning empty list");
            return Collections.emptyList();
        }

        List<Item> searchResults = itemRepository.searchAvailableItems(text);
        List<ItemDto> resultsDto = searchResults.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} search results for query: '{}'", resultsDto.size(), text);
        return resultsDto;
    }
}
