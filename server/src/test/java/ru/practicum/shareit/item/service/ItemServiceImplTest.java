package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final UserDto user1 = new UserDto(null, "John Doe", "john.doe@example.com");
    private final UserDto user2 = new UserDto(null, "Jane Doe", "jane.dow@exemple.com");
    private final UserDto user3 = new UserDto(null, "John Wick", "john.wick@mail.com");

    @Test
    void testCreateItemSuccess() {
        UserDto existingUser3 = userService.createUser(user3);
        ItemDto itemDto = new ItemDto(null, "snorkel",
                "breathing tube for scuba diving",
                true,
                null,
                null,
                null,
                null,
                null);
        ItemDto snorkel = itemService.createItem(itemDto, existingUser3.getId());
        assertThat(snorkel.getId()).isNotNull();
        assertThat(snorkel.getName()).isEqualTo(itemDto.getName());
        assertThat(snorkel.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(snorkel.getAvailable()).isEqualTo(itemDto.getAvailable());
    }

    @Test
    void testUpdateItemSuccess() {
        UserDto existingUser3 = userService.createUser(user3);
        ItemDto itemDto = new ItemDto(null, "snorkel",
                "breathing tube for scuba diving",
                true,
                null,
                null,
                null,
                null,
                null);
        ItemDto snorkel = itemService.createItem(itemDto, existingUser3.getId());
        Long itemId = snorkel.getId();
        ItemDto updatedItemDto = new ItemDto(
                itemId,
                "Best Snorkel",
                "Best in the world breathing tube for scuba diving",
                false,
                null,
                null,
                null,
                null,
                null
        );
        ItemDto result = itemService.updateItem(itemId, updatedItemDto, existingUser3.getId());
        assertThat(result.getName()).isEqualTo("Best Snorkel");
        assertThat(result.getDescription()).isEqualTo("Best in the world breathing tube for scuba diving");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void testUpdateItemForbidden() {
        UserDto existingUser3 = userService.createUser(user3);
        ItemDto itemDto = new ItemDto(null, "snorkel",
                "Breathing tube for scuba diving",
                true,
                null,
                null,
                null,
                null,
                null);
        ItemDto snorkel = itemService.createItem(itemDto, existingUser3.getId());
        Long itemId = snorkel.getId();
        ItemDto updatedItemDto = new ItemDto(
                itemId,
                "Best snorkel",
                "Best in the world breathing tube for scuba diving",
                true,
                null,
                1L,
                null,
                null,
                null
        );
        UserDto anotherUser = userService.createUser(user2);
        assertThatThrownBy(() ->
                itemService.updateItem(itemId, updatedItemDto, anotherUser.getId())
        ).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void testGetItemByIdSuccess() {
        UserDto existingUser3 = userService.createUser(user3);
        ItemDto itemDto = new ItemDto(null, "snorkel",
                "Breathing tube for scuba diving",
                true,
                null,
                null,
                null,
                null,
                null);
        ItemDto snorkel = itemService.createItem(itemDto, existingUser3.getId());
        ItemDto item = itemService.getItemById(snorkel.getId());
        assertThat(item.getId()).isEqualTo(snorkel.getId());
        assertThat(item.getName()).isEqualTo(snorkel.getName());
        assertThat(item.getDescription()).isEqualTo(snorkel.getDescription());
    }

    @Test
    void testGetItemByIdNotFound() {
        assertThatThrownBy(() ->
                itemService.getItemById(599L)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testGetAllUserItems() {
        UserDto user = userService.createUser(user1);
        itemService.createItem(new ItemDto(
                null,
                "Laptop",
                "Gaming laptop",
                true,
                null,
                null,
                null,
                null,
                null
        ), user.getId());
        itemService.createItem(new ItemDto(
                null,
                "Camera",
                "Digital camera",
                false,
                null,
                null,
                null,
                null,
                null
        ), user.getId());
        List<ItemDto> items = itemService.getAllUserItems(user.getId());;
        assertThat(items.get(0).getName()).isIn("Laptop", "Camera");
        assertThat(items.get(1).getName()).isIn("Laptop", "Camera");
    }

    @Test
    void testGetAllUserItems_NoItems() {
        UserDto user = userService.createUser(user2);
        List<ItemDto> items = itemService.getAllUserItems(user.getId());
        assertTrue(items.isEmpty());
    }

    @Test
    void testSearchItems_Success() {
        UserDto existingUser3 = userService.createUser(user3);
        itemService.createItem(new ItemDto(
                null,
                "Camera",
                "Digital camera",
                true,
                null,
                null,
                null,
                null,
                null
        ), existingUser3.getId());
        itemService.createItem(new ItemDto(
                null,
                "Laptop",
                "Gaming laptop",
                true,
                null,
                null,
                null,
                null,
                null
        ), existingUser3.getId());
        List<ItemDto> results = itemService.searchItems("cam");
        assertThat(results.getFirst().getName()).isEqualTo("Camera");
    }

    @Test
    void testSearchItems_EmptyQuery() {
        List<ItemDto> results = itemService.searchItems("");
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchItems_NoResults() {
        List<ItemDto> results = itemService.searchItems("nonexistent");
        assertTrue(results.isEmpty());
    }
}