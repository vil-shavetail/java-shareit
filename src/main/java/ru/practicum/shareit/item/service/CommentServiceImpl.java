package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDto createComment(Long itemId, Long authorId, CommentDto commentDto) {
        log.info("Creating comment for item: {}, author: {}", itemId, authorId);
        if (commentDto.getText() == null || commentDto.getText().isEmpty()) {
            throw new ValidationException("The text of the comment cannot be empty");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Comment comment = CommentMapper.toEntity(commentDto, item, author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created: {}", savedComment.getId());
        return CommentMapper.toDto(savedComment);
    }
}