package ru.practicum.shareit.item;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.service.BookingPersistService;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.exception.CommentBadRequestException;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemEmptyAvailableException;
import ru.practicum.shareit.item.exceptions.ItemEmptyNameException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exceptions.UserNotOwnerItemException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Fixture.*;
import static ru.practicum.shareit.item.ItemController.OWNER_ID;


@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @MockBean
    private BookingPersistService bookingPersistService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    public void clearContext() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldAddItem() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);


        String result = mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(generateItemDto(user.getId())))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ItemDto savedItem = mapper.readValue(result, ItemDto.class);
        assertNotNull(savedItem);
        assertNotNull(savedItem.getId());
    }
    @Test
    public void shouldThrowItemEmptyAvailableExceptionWhenAddItemByEmptyAvailableStatus() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);
        var item = generateItemDto(user.getId());
        item.setAvailable(null);


        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ItemEmptyAvailableException));
    }

    @Test
    public void shouldThrowItemEmptyAvailableExceptionWhenAddItemByEmptyName() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);
        var item = generateItemDto(user.getId());
        item.setName(null);


        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ItemEmptyNameException));
    }

    @Test
    public void shouldThrowItemEmptyAvailableExceptionWhenAddItemByEmptyDescription() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);
        var item = generateItemDto(user.getId());
        item.setDescription(null);


        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ItemEmptyNameException));
    }

    @Test
    public void shouldUpdateItem() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var item = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        item.setName("test value changed");
        String result = mvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(item))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ItemDto savedItem = mapper.readValue(result, ItemDto.class);
        assertNotNull(savedItem);
        assertNotNull(savedItem.getId());
        assertEquals(savedItem.getName(), item.getName());
    }

    @Test
    public void shouldThrowUserNotOwnerItemExceptionWhenUpdateItem() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var item = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        item.setName("test value changed");
        mvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(item))
                        .header(OWNER_ID, 99L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserNotOwnerItemException));
    }

    @Test
    public void shouldThrowItemNotFoundExceptionWhenUpdateItem() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var item = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        item.setName("test value changed");
         mvc.perform(patch("/items/" + 100L)
                        .content(mapper.writeValueAsString(item))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ItemNotFoundException));
    }


    @Test
    public void shouldGetItemById() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var item = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        String result = mvc.perform(get("/items/" + item.getId())
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        ItemDto savedItem = mapper.readValue(result, ItemDto.class);
        assertNotNull(savedItem);
        assertNotNull(savedItem.getId());
        assertEquals(savedItem.getId(), item.getId());
    }
    @Test
    public void shouldThrowItemNotFoundExceptionWhenGetItemById() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        mvc.perform(get("/items/" + 99L)
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ItemNotFoundException));
    }

    @Test
    public void shouldGetItemByOwnerId() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var item = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        String result = mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        var items = mapper.readValue(result, new TypeReference<List<ItemDto>>() {
        });
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }


    @Test
    public void shouldGetAvailableItemsByName() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var item = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        String result = mvc.perform(get("/items/search?text=" + item.getName())
                        .content(mapper.writeValueAsString(item))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        var items = mapper.readValue(result, new TypeReference<List<ItemDto>>() {
        });
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    public void shouldAddItemComment() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();

        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var item = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        when(bookingPersistService.findBookingByItemIdAndStatusNotInAndStartBefore(any(), any(), any()))
                .thenReturn(List.of(generateBooking(item.getId())));

        String result = mvc.perform(post("/items/" + item.getId() + "/comment")
                        .content(mapper.writeValueAsString(generateCommentDto(user.getId(), item.getId())))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        CommentDto savedComment = mapper.readValue(result, CommentDto.class);
        assertNotNull(savedComment);
        assertNotNull(savedComment.getId());
    }

    @Test
    public void shouldThrowCommentBadRequestExceptionByEmptyText() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();

        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var item = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        when(bookingPersistService.findBookingByItemIdAndStatusNotInAndStartBefore(any(), any(), any()))
                .thenReturn(List.of(generateBooking(item.getId())));
        var comment = generateCommentDto(user.getId(), item.getId());
        comment.setText(null);
        mvc.perform(post("/items/" + item.getId() + "/comment")
                        .content(mapper.writeValueAsString(comment))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof CommentBadRequestException));

    }

    @Test
    public void shouldThrowCommentBadRequestExceptionByEmptyBooking() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();

        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var item = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var comment = generateCommentDto(user.getId(), item.getId());
        mvc.perform(post("/items/" + item.getId() + "/comment")
                        .content(mapper.writeValueAsString(comment))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof CommentBadRequestException));

    }
}
