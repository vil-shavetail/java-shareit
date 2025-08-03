package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    public ItemDto createItem(ItemDto itemDto, Long ownerId);

    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId);

    public ItemDto getItemById(Long itemId);

    public List<ItemDto> getAllUserItems(Long ownerId);

    public List<ItemDto> searchItems(String text);
}