package ru.practicum.shareit.item.dto;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@UtilityClass
public class CommentMapper {
    public static CommentDto toDto(@NonNull Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getId(),
                comment.getCreated()
        );
    }

    public static Comment toEntity(@NonNull CommentDto dto,
        @NonNull Item item,
        @NonNull User author) {
        return new Comment(
                dto.getId(),
                dto.getText(),
                item,
                author,
                dto.getCreated());
    }
}
