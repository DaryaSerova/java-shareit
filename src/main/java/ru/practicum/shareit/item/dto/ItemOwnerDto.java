package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

@Data
@AllArgsConstructor
@Builder
public class ItemOwnerDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingCreateDto lastBooking;
    private BookingCreateDto nextBooking;
}

