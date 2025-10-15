package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final UserDto user1 = new UserDto(null, "John Doe", "john.doe@example.com");
    private final UserDto user2 = new UserDto(null, "Jane Doe", "jane.doe@example.com");
    private final ItemRequestRepository itemRequestRepository;

    @Test
    void testCreateRequestSuccess() {
        UserDto user = userService.createUser(user1);
        ItemRequestDto itemRequest = new ItemRequestDto(null, "Need Digital Camera", null, LocalDateTime.now(), null);
        ItemRequestDto createdItemRequest = itemRequestService.createRequest(itemRequest, user.getId());
        assertThat(createdItemRequest.getId()).isNotNull();
        assertThat(createdItemRequest.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(createdItemRequest.getRequesterId()).isEqualTo(user.getId());
    }

    @Test
    void testCreateRequestWithEmptyDescription() {
        UserDto user = userService.createUser(user1);
        ItemRequestDto itemRequest = new ItemRequestDto(null, "", null, LocalDateTime.now(), null);
        assertThatThrownBy(() ->
                itemRequestService.createRequest(itemRequest, user.getId())
        ).isInstanceOf(ValidateException.class);
    }

    @Test
    void testCreateRequestWithoutDescription() {
        UserDto user = userService.createUser(user1);
        ItemRequestDto itemRequest = new ItemRequestDto(null, null, null, LocalDateTime.now(), null);
        assertThatThrownBy(() ->
                itemRequestService.createRequest(itemRequest, user.getId())
        ).isInstanceOf(ValidateException.class);
    }

    @Test
    void testCreateRequestWithNonExistentUser() {
        ItemRequestDto itemRequest = new ItemRequestDto(null, "Need Digital Camera", null, LocalDateTime.now(), null);
        assertThatThrownBy(() ->
                itemRequestService.createRequest(itemRequest, 654L)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testGetItemRequestByIdSuccess() {
        Long userId = userService.createUser(user1).getId();
        ItemRequestDto requestDto = new ItemRequestDto(null, "Need Digital Camera", null, LocalDateTime.now(), null);
        Long requestId = itemRequestService.createRequest(requestDto, userId).getId();
        ItemRequestDto request = itemRequestService.getItemRequestById(requestId);
        assertThat(request.getId()).isEqualTo(requestId);
        assertThat(request.getDescription()).isNotNull();
        assertThat(request.getRequesterId()).isEqualTo(userId);
    }

    @Test
    void testGetItemRequestByIdNotFound() {
        assertThatThrownBy(() ->
                itemRequestService.getItemRequestById(777L)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testGetAllUserItemRequestsSuccess() {
        Long userId = userService.createUser(user1).getId();
        ItemRequestDto requestDto = new ItemRequestDto(null,
                "Need Digital Camera",
                null,
                LocalDateTime.now(),
                null);
        itemRequestService.createRequest(requestDto, userId);
        ItemRequestDto secondRequest = new ItemRequestDto(
                null,
                "Need Laptop",
                null,
                LocalDateTime.now(),
                null
        );
        itemRequestService.createRequest(secondRequest, userId);
        List<ItemRequestDto> requests = itemRequestService.getAllUserItemRequests(userId);
        assertEquals(2, requests.size());
        assertThat(requests.get(0).getRequesterId()).isEqualTo(userId);
        assertThat(requests.get(1).getRequesterId()).isEqualTo(userId);
    }

    @Test
    void testGetAllUserItemRequestsNoRequests() {
        Long anotherUserId = userService.createUser(user2).getId();
        List<ItemRequestDto> requests = itemRequestService.getAllUserItemRequests(anotherUserId);
        assertTrue(requests.isEmpty());
    }


    @Test
    void testGetAllRequestsSuccess() {
        Long userId = userService.createUser(user1).getId();
        ItemRequestDto requestDto = new ItemRequestDto(null, "Need Digital Camera", null, LocalDateTime.now(), null);
        Long requestId = itemRequestService.createRequest(requestDto, userId).getId();
        itemRequestService.getItemRequestById(requestId);
        Long anotherUserId = userService.createUser(user2).getId();
        ItemRequestDto anotherRequest = new ItemRequestDto(
                null,
                "Need Phone",
                null,
                LocalDateTime.now(),
                null
        );
        itemRequestService.createRequest(anotherRequest, anotherUserId);
        List<ItemRequestDto> allRequests = itemRequestService.getAllRequests();
        assertEquals(2, allRequests.size());
    }

    @Test
    void getAllRequestsEmpty() {
        itemRequestRepository.deleteAll();
        List<ItemRequestDto> allRequests = itemRequestService.getAllRequests();
        assertTrue(allRequests.isEmpty());
    }
}