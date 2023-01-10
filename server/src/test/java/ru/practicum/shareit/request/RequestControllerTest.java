package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestorDto;
import ru.practicum.shareit.request.exceptions.RequestEmptyNameException;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Fixture.*;
import static ru.practicum.shareit.request.RequestController.REQUESTOR_ID;

@SpringBootTest
@AutoConfigureMockMvc
public class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @BeforeEach
    public void clearContext() {
        requestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldAddRequest() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var request = generateRequestDto(user.getId());

        String result = mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header(REQUESTOR_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();


        RequestDto savedRequest = mapper.readValue(result, RequestDto.class);
        assertNotNull(savedRequest);
        assertNotNull(savedRequest.getId());
    }

    @Test
    public void shouldThrowRequestEmptyNameExceptionWhenAddRequest() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var request = generateRequestDto(user.getId());
        request.setDescription(null);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header(REQUESTOR_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof RequestEmptyNameException));

    }

    @Test
    public void shouldGetRequestById() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var request = generateRequestDto(user.getId());
        request = requestService.addRequest(user.getId(), request);
        var item = generateItemDto(user.getId());
        item.setRequestId(request.getId());
        item = itemService.addItem(user.getId(), generateItemDto(user.getId()));


        String result = mvc.perform(get("/requests/" + request.getId())
                        .header(REQUESTOR_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();


        RequestDto savedRequest = mapper.readValue(result, RequestDto.class);
        assertNotNull(savedRequest);
        assertNotNull(savedRequest.getId());
    }

    @Test
    public void shouldThrowRequestNotFoundExceptionWhenGetRequestById() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);


        mvc.perform(get("/requests/" + 99L)
                        .header(REQUESTOR_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof RequestNotFoundException));
    }

    @Test
    public void shouldGetAllRequestByRequestorId() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var request = generateRequestDto(user.getId());

        request = requestService.addRequest(user.getId(), request);

        String result = mvc.perform(get("/requests")
                        .header(REQUESTOR_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        var requests = mapper.readValue(result, new TypeReference<List<RequestRequestorDto>>() {
        });
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertTrue(request.getId().equals(requests.get(0).getId()));
    }

    @Test
    public void shouldGetAllRequest() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var request = generateRequestDto(user.getId());
        request = requestService.addRequest(user.getId(), request);

        String result = mvc.perform(get("/requests/all")
                        .header(REQUESTOR_ID, 999L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        var requests = mapper.readValue(result, new TypeReference<List<RequestRequestorDto>>() {
        });
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertTrue(request.getId().equals(requests.get(0).getId()));
    }
}
