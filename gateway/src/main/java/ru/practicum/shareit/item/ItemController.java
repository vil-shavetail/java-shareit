package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemClient.getAllUserItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createComment(@RequestHeader(X_SHARER_USER_ID)
            @RequestAttribute("userId") Long authorId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        return itemClient.createComment(itemId, authorId, commentDto);
    }
}