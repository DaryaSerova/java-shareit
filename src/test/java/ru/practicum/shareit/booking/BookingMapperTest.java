package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.Fixture.*;


@SpringBootTest
@AutoConfigureMockMvc
public class BookingMapperTest {

    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;


    @Test
    public void shouldMapBookingDtoToBooking() {
        var bookingDto = generateBookingDto(1L);


        var result = bookingMapper.toModel(bookingDto);

        assertTrue(bookingDto.getId().equals(result.getId()));
        assertTrue(bookingDto.getStatus().equals(result.getStatus()));
        assertTrue(bookingDto.getEnd().equals(result.getEnd()));
        assertTrue(bookingDto.getStart().equals(result.getStart()));
    }

    @Test
    public void shouldMapBookingCreateDtoToBooking() {
        var bookingDto = generateBookingCreateDto(1L);
        var user = generateUserDto();
        var item = generateItemDto(1L);

        var result = bookingMapper.toModel(
                bookingDto,
                user, item, userMapper, itemMapper);

        assertTrue(bookingDto.getId().equals(result.getId()));
        assertTrue(bookingDto.getStatus().equals(result.getStatus()));
        assertTrue(bookingDto.getEnd().equals(result.getEnd()));
        assertTrue(bookingDto.getStart().equals(result.getStart()));
    }
}
