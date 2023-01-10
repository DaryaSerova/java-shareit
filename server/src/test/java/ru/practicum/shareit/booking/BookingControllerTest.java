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
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Fixture.*;
import static ru.practicum.shareit.booking.BookingController.OWNER_ID;


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
    public void shouldApproveBookingThrowBookingNotFoundException() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var booking = bookingService.addBooking(user2.getId(), generateBookingCreateDto(itemId.getId()));


        mvc.perform(patch("/bookings/" + booking.getId() + "?approved=true")
                        .content(mapper.writeValueAsString(user))
                        .header(OWNER_ID, 99L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingNotFoundException));

    }

    @Test
    public void shouldApproveBookingThrowBookingStatusBadRequestException() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var bookingCreateDto = generateBookingCreateDto(itemId.getId());

        var booking = bookingService.addBooking(user2.getId(), bookingCreateDto);

        bookingService.approveBooking(user.getId(), booking.getId(), true);

        mvc.perform(patch("/bookings/" + booking.getId() + "?approved=true")
                        .content(mapper.writeValueAsString(user))
                        .header(OWNER_ID, 99L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof BookingStatusBadRequestException));

    }

    @Test
    public void shouldCreateBookingThrowItemUnavailableException() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemDto = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        itemDto.setAvailable(false);

        itemService.updateItem(user.getId(), itemDto, itemDto.getId());

        var bookingCreateDto = generateBookingCreateDto(itemDto.getId());

        bookingCreateDto.setStart(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(OWNER_ID, user2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemUnavailableException));

    }

    @Test
    public void shouldCreateBookingThrowBookingDateBadRequestExceptionByStartDate() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var bookingCreateDto = generateBookingCreateDto(itemId.getId());

        bookingCreateDto.setStart(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(OWNER_ID, user2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof BookingDateBadRequestException));

    }


    @Test
    public void shouldCreateBookingThrowBookingDateBadRequestExceptionByEndDate() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var bookingCreateDto = generateBookingCreateDto(itemId.getId());

        bookingCreateDto.setEnd(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(OWNER_ID, user2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof BookingDateBadRequestException));

    }

    @Test
    public void shouldCreateBookingThrowBookingDateBadRequestExceptionByStartAndEndDate() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var bookingCreateDto = generateBookingCreateDto(itemId.getId());

        bookingCreateDto.setEnd(LocalDateTime.now().minusDays(10));


        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(OWNER_ID, user2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof BookingDateBadRequestException));

    }

    @Test
    public void shouldCreateBookingThrowBookingOwnerNotFoundException() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var bookingCreateDto = generateBookingCreateDto(itemId.getId());

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof BookingOwnerNotFoundException));

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
    public void shouldThrowBookingNotFoundExceptionWhenGetBooking() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var booking = bookingService.addBooking(user2.getId(), generateBookingCreateDto(itemId.getId()));

        mvc.perform(get("/bookings/" + 100L)
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingNotFoundException));
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
    public void shouldGetBookingForUserByWaitingState() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var createBookingDto = generateBookingCreateDto(itemId.getId());

        var booking = bookingService.addBooking(user2.getId(), createBookingDto);

        String result = mvc.perform(get("/bookings?state=WAITING")
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
    public void shouldGetBookingForUserByFutureState() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var createBookingDto = generateBookingCreateDto(itemId.getId());

        createBookingDto.setStart(createBookingDto.getStart().plusDays(1));

        createBookingDto.setEnd(createBookingDto.getEnd().plusDays(12));

        var booking = bookingService.addBooking(user2.getId(), createBookingDto);

        String result = mvc.perform(get("/bookings?state=FUTURE")
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
    public void shouldGetBookingForUserByRejectedState() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var createBookingDto = generateBookingCreateDto(itemId.getId());

        var booking = bookingService.addBooking(user2.getId(), createBookingDto);

        bookingService.approveBooking(user.getId(), booking.getId(), false);

        String result = mvc.perform(get("/bookings?state=REJECTED")
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
    public void shouldThrowBookingStateBadRequestExceptionWhenGetBookingForUserByAllState() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var booking = bookingService.addBooking(user2.getId(), generateBookingCreateDto(itemId.getId()));

        mvc.perform(get("/bookings?state=TEST_INCORRECT")
                        .header(OWNER_ID, user2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof BookingStateBadRequestException));


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

    @Test
    public void shouldThrowBookingStateBadRequestExceptionWhenGetBookingForUserByItemsByAllState() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var booking = bookingService.addBooking(user2.getId(), generateBookingCreateDto(itemId.getId()));

        mvc.perform(get("/bookings/owner?state=TEST_INCORRECT")
                        .header(OWNER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof BookingStateBadRequestException));

    }

    @Test
    public void shouldGetBookingForUserByItemsByFutureState() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var createBookingDto = generateBookingCreateDto(itemId.getId());

        createBookingDto.setStart(createBookingDto.getStart().plusDays(1));

        createBookingDto.setEnd(createBookingDto.getEnd().plusDays(12));

        var booking = bookingService.addBooking(user2.getId(), createBookingDto);

        String result = mvc.perform(get("/bookings/owner?state=FUTURE")
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


    @Test
    public void shouldGetBookingForUserByItemsByRejectedState() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var createBookingDto = generateBookingCreateDto(itemId.getId());

        var booking = bookingService.addBooking(user2.getId(), createBookingDto);

        bookingService.approveBooking(user.getId(), booking.getId(), false);

        String result = mvc.perform(get("/bookings/owner?state=REJECTED")
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


    @Test
    public void shouldGetBookingForUserByItemsByWaitingState() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");

        user2 = userService.createUser(user2);

        var itemId = itemService.addItem(user.getId(), generateItemDto(user.getId()));

        var createBookingDto = generateBookingCreateDto(itemId.getId());

        var booking = bookingService.addBooking(user2.getId(), createBookingDto);

        String result = mvc.perform(get("/bookings/owner?state=WAITING")
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
