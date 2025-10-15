package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentServiceImplTest {
    private final UserService userService;
    private final CommentService commentService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserDto user1 = new UserDto(null, "John Doe", "john.doe@example.com");
    private final UserDto user2 = new UserDto(null, "Jane Doe", "jane.doe@example.com");

    @Test
    void createCommentWithNonExistentUser() {
        CommentDto commentDto = new CommentDto(null, "The best Digital Camera in the world", "Jane Doe", LocalDateTime.now());
        assertThatThrownBy(() ->
                commentService.createComment(755L, 321L, commentDto)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void createCommentWithNonExistentItem() {
        UserDto user = userService.createUser(user1);
        CommentDto commentDto = new CommentDto(null, "The best Digital Camera in the world", "John Doe", LocalDateTime.now());
        assertThatThrownBy(() ->
                commentService.createComment(755L, user.getId(), commentDto)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void createCommentWithEmptyText() {
        Long userId = userService.createUser(user1).getId();
        Long itemId = itemService.createItem(
                new ItemDto(null, "Camera", "Digital Camera", true, null, null, null, null, null),
                userId
        ).getId();
        CommentDto commentDto = new CommentDto(
                null,
                "",
                "John Doe",
                LocalDateTime.now()
        );
        assertThatThrownBy(() ->
                commentService.createComment(itemId, userId, commentDto)
        ).isInstanceOf(ValidateException.class);
    }

    @Test
    void createCommentWithoutText() {
        Long userId = userService.createUser(user1).getId();
        Long itemId = itemService.createItem(
                new ItemDto(null, "Camera", "Digital Camera", true, null, null, null, null, null),
                userId
        ).getId();
        CommentDto commentDto = new CommentDto(
                null,
                null,
                "John Doe",
                LocalDateTime.now()
        );
        assertThatThrownBy(() ->
                commentService.createComment(itemId, userId, commentDto)
        ).isInstanceOf(ValidateException.class);
    }

    @Test
    void createValidComment() {
       Long userId = userService.createUser(user1).getId();
       Long itemId = itemService.createItem(
                new ItemDto(null, "Camera", "Digital Camera", true, null, null, null, null, null),
                userId
        ).getId();
        CommentDto commentDto = new CommentDto(
                null,
                "Excellent item!",
                "John Doe",
                LocalDateTime.now()
        );
        CommentDto createdComment = commentService.createComment(itemId, userId, commentDto);
        assertThat(createdComment.getId()).isNotNull();
        assertThat(createdComment.getText()).isEqualTo("Excellent item!");
        assertThat(createdComment.getAuthorName()).isEqualTo("John Doe");
    }

    @Test
    void createCommentAfterBooking() {
        Long userId = userService.createUser(user1).getId();
        Long itemId = itemService.createItem(
                new ItemDto(null, "Camera", "Digital Camera", true, null, null, null, null, null),
                userId
        ).getId();
        bookingService.createBooking(
                userId,
                new BookingRequestDto(
                        itemId,
                        LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusDays(1)
                )
        );
        CommentDto commentDto = new CommentDto(
                null,
                "Great item!",
                "John Doe",
                LocalDateTime.now()
        );
        CommentDto createdComment = commentService.createComment(itemId, userId, commentDto);
        assertThat(createdComment.getId()).isNotNull();
    }
}