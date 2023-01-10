package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemToRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class RequestRequestorDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemToRequestDto> items;
}
