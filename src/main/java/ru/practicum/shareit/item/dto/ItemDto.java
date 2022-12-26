package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Long ownerId;

    private Boolean available;

    private Long requestId;

    private BookingCreateDto lastBooking;

    private BookingCreateDto nextBooking;

    private List<CommentDto> comments = new ArrayList<>();
}

