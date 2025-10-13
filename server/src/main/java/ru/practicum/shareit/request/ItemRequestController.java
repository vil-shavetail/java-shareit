package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(
            @RequestHeader(X_SHARER_USER_ID) Long requesterId,
            @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(itemRequestDto, requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(
            @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserItemRequests(
            @RequestHeader(X_SHARER_USER_ID) Long requesterId) {
        return itemRequestService.getAllUserItemRequests(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests()  {
        return itemRequestService.getAllRequests();
    }
}
