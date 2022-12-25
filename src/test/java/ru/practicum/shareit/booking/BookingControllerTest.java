package ru.practicum.shareit.booking;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.practicum.shareit.Fixture.*;
import static ru.practicum.shareit.auth.AuthConstant.OWNER_ID;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void clearContext() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldAddBooking() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var item = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        String result = mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(generateBookingCreateDto(item.getId())))
                        .header(OWNER_ID, user2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();


        BookingDto savedBooking = mapper.readValue(result, BookingDto.class);
        assertNotNull(savedBooking);
        assertNotNull(savedBooking.getId());

    }


    @Test
    public void shouldApproveBooking() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var booking = bookingService.addBooking(user2.getId(), generateBookingCreateDto(itemId.getId()));


        String result = mvc.perform(patch("/bookings/" + booking.getId() + "?approved=true")
                        .content(mapper.writeValueAsString(user))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        BookingDto savedBooking = mapper.readValue(result, BookingDto.class);
        assertNotNull(savedBooking);
        assertNotNull(savedBooking.getId());
        assertTrue(savedBooking.getStatus() == BookingStatus.APPROVED);
    }


    @Test
    public void shouldGetBooking() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var booking = bookingService.addBooking(user2.getId(), generateBookingCreateDto(itemId.getId()));

        String result = mvc.perform(get("/bookings/" + booking.getId())
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        BookingDto savedBooking = mapper.readValue(result, BookingDto.class);
        assertNotNull(savedBooking);
        assertNotNull(savedBooking.getId());
        assertTrue(savedBooking.getId().equals(booking.getId()));
    }

    @Test
    public void shouldGetBookingForUserByAllState() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var booking = bookingService.addBooking(user2.getId(), generateBookingCreateDto(itemId.getId()));

        String result = mvc.perform(get("/bookings?state=ALL")
                        .header(OWNER_ID, user2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        var bookings = mapper.readValue(result, new TypeReference<List<BookingDto>>() {
         });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }


    @Test
    public void shouldGetBookingForUserByItemsByAllState() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var booking = bookingService.addBooking(user2.getId(), generateBookingCreateDto(itemId.getId()));

        String result = mvc.perform(get("/bookings/owner?state=ALL")
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        var bookings = mapper.readValue(result, new TypeReference<List<BookingDto>>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }
}
