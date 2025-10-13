package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader(X_SHARER_USER_ID) Long requesterId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(requesterId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @PathVariable Long requestId) {
        return itemRequestClient.getItemRequestById(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItemRequests(
            @RequestHeader(X_SHARER_USER_ID) Long requesterId) {
        return itemRequestClient.getAllUserItemRequests(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests()  {
        return itemRequestClient.getAllRequests();
    }
}
