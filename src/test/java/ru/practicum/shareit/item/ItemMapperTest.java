package ru.practicum.shareit.item;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserPersistService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.Fixture.generateBooking;
import static ru.practicum.shareit.Fixture.generateItemWithComments;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private CommentMapper commentMapper;

    @MockBean
    private UserPersistService userPersistService;


    @Test
    public void shouldMapItemToItemDto() {
        var item = generateItemWithComments();

        var result = itemMapper.toItemDto(item);

        assertTrue(item.getId().equals(result.getId()));
        assertTrue(item.getName().equals(result.getName()));
        assertTrue(item.getDescription().equals(result.getDescription()));
        assertTrue(item.getAvailable().equals(result.getAvailable()));
        assertTrue(item.getRequestId().equals(result.getRequestId()));
        assertTrue(item.getOwnerId().equals(result.getOwnerId()));
        assertNotNull(item.getComments());
        assertEquals(1, item.getComments().size());
    }

    @Test
    public void shouldMapItemToItemDtoWithBooking() {
        var item = generateItemWithComments();
        when(userPersistService.findUserById(any()))
                .thenReturn(Optional.of(User.builder().name("test").build()));


        var result = itemMapper.toItemDto(item,
                List.of(generateBooking(1L), generateBooking(2L)), commentMapper, userPersistService);

        assertTrue(item.getId().equals(result.getId()));
        assertTrue(item.getName().equals(result.getName()));
        assertTrue(item.getDescription().equals(result.getDescription()));
        assertTrue(item.getAvailable().equals(result.getAvailable()));
        assertTrue(item.getRequestId().equals(result.getRequestId()));
        assertTrue(item.getOwnerId().equals(result.getOwnerId()));
    }


    @Test
    public void shouldMapItemToItemToRequestDto() {
        var item = generateItemWithComments();


        var result = itemMapper.itemToItemToRequestDto(item);

        assertTrue(item.getId().equals(result.getId()));
        assertTrue(item.getName().equals(result.getName()));
        assertTrue(item.getDescription().equals(result.getDescription()));
        assertTrue(item.getAvailable().equals(result.getAvailable()));
        assertTrue(item.getRequestId().equals(result.getRequestId()));
        assertTrue(item.getOwnerId().equals(result.getOwnerId()));
    }

    @Test
    public void shouldMapItemToItemOwnerDto() {
        var item = generateItemWithComments();
        var result = itemMapper.toItemOwnerDto(item, List.of(generateBooking(1L), generateBooking(2L)));
        assertTrue(item.getId().equals(result.getId()));
        assertTrue(item.getName().equals(result.getName()));
        assertTrue(item.getDescription().equals(result.getDescription()));
        assertTrue(item.getAvailable().equals(result.getAvailable()));
    }
}
