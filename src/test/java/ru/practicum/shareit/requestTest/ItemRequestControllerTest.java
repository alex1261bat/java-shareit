package ru.practicum.shareit.requestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    User owner = new User(1L, "owner", "owner@mail");
    private final ItemRequest itemRequest = new ItemRequest(1L, "text", owner, LocalDateTime.now());
    private final ItemDto itemDto = new ItemDto(1L, "item", "description", true,
            owner.getId(), itemRequest.getRequestor().getId());
    private final ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
    private final ItemRequestWithItemDtoListResponseDto requestDtoResponse =
            ItemRequestMapper.toItemRequestWithItemDtoListResponseDto(itemRequest, List.of(itemDto));

    @Test
    void saveNewItemRequestTest() throws Exception {
        when(itemRequestService.saveNewItemRequest(anyLong(), any())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        when(itemRequestService.getAllByOwner(anyLong())).thenReturn(List.of(requestDtoResponse));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(requestDtoResponse.getDescription()), String.class))
                .andExpect(jsonPath("$[0].id", is(requestDtoResponse.getId()), Long.class));
    }

    @Test
    void getAllTest() throws Exception {
        when(itemRequestService.getAll(anyLong(), any())).thenReturn(List.of(requestDtoResponse));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(requestDtoResponse.getDescription()), String.class))
                .andExpect(jsonPath("$[0].id", is(requestDtoResponse.getId()), Long.class));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(requestDtoResponse);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDtoResponse.getDescription()), String.class))
                .andExpect(jsonPath("$.id", is(requestDtoResponse.getId()), Long.class));
    }
}
