package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

@Data
@NoArgsConstructor
public class ItemOwnerDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingCreateDto lastBooking;

    private BookingCreateDto nextBooking;
}

