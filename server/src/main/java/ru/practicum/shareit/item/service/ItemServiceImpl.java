package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
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
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        log.info("Updating item: {}, with data: {}, for user: {}", itemId, itemDto, ownerId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + " not found"));

        if (!item.getOwner().getId().equals(ownerId)) {
            log.warn("Unauthorized update attempt for item: {}, by user: {}", itemId, ownerId);
            throw new ForbiddenException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(item);
        log.info("Item updated successfully: {}", updatedItem.getId());
        return ItemMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Getting item by id: {}", itemId);
        LocalDateTime currentDateTime = LocalDateTime.now();
        BookingRequestDto lastBooking = null;
        BookingRequestDto nextBooking = null;
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + " not found"));

        log.info("Item found: {}", item.getId());
        ItemDto itemDto = ItemMapper.toDto(item);
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toDto)
                .toList();
        List<Booking> bookings = bookingRepository.findBookingsByItemIdOrderByStartDesc(itemId);
        if (bookings.size() == 1) {
            Booking singleBooking = bookings.getFirst();
            if (singleBooking.getStatus() == BookingStatus.APPROVED) {
                if (currentDateTime.isAfter(singleBooking.getStart())
                        && currentDateTime.isBefore(singleBooking.getEnd())) {
                    return null;
                }
            }
        } else {
            for (Booking booking : bookings) {
                if (booking.getStatus() == BookingStatus.APPROVED) {
                    if (booking.getEnd().isBefore(currentDateTime) && (lastBooking == null ||
                            booking.getEnd().isAfter(lastBooking.getEnd()))) {
                        lastBooking = BookingMapper.bookingRequestToDto(booking);
                    }
                    if (booking.getStart().isAfter(currentDateTime) && (nextBooking == null ||
                            booking.getStart().isBefore(nextBooking.getStart()))) {
                        nextBooking = BookingMapper.bookingRequestToDto(booking);
                    }
                }
            }
        }
        itemDto.setComments(comments);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllUserItems(Long ownerId) {
        log.info("Getting all items for user: {}", ownerId);

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<ItemDto> itemsDto = items.stream()
                .map(ItemMapper::toDto)
                .toList();

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
                .toList();

        log.info("Found {} search results for query: '{}'", resultsDto.size(), text);
        return resultsDto;
    }
}
