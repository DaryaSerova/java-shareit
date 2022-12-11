package ru.practicum.shareit.item.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

@Component
public class CommentMapper {

    public Comment toModel(CommentDto dto) {
        return Comment.builder()
                .id(dto.getId())
                .authorId(dto.getAuthorId())
                .text(dto.getText())
                .itemId(dto.getItemId())
                .created(dto.getCreated())
                .build();
    }

    public CommentDto toDto(Comment entity) {
        return CommentDto.builder()
                .id(entity.getId())
                .authorId(entity.getAuthorId())
                .text(entity.getText())
                .itemId(entity.getItemId())
                .created(entity.getCreated())
                .build();
    }
}
