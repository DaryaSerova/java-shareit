package ru.practicum.shareit.item.comment.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;

    private String text;

    private Long itemId;

    private Long authorId;

    private String authorName;

    private LocalDateTime created;


}
