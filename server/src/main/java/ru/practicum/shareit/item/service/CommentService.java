package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;

public interface CommentService {
    CommentDto createComment(Long itemId, Long authorId, CommentDto commentDto);
}
