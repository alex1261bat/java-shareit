package ru.practicum.server.requestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.item.ItemDto;
import ru.practicum.server.pageCreator.PageCreator;
import ru.practicum.server.request.*;
import ru.practicum.server.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    @MockBean
    private final ItemRequestService itemRequestService;
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    private final User owner = new User(1L, "owner", "owner@mail");
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
        verify(itemRequestService, times(1)).saveNewItemRequest(1L, requestDto);
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
                .andExpect(jsonPath("$[0].description",
                        is(requestDtoResponse.getDescription()), String.class))
                .andExpect(jsonPath("$[0].id", is(requestDtoResponse.getId()), Long.class));
        verify(itemRequestService, times(1)).getAllByOwner(1L);
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
                .andExpect(jsonPath("$[0].description",
                        is(requestDtoResponse.getDescription()), String.class))
                .andExpect(jsonPath("$[0].id", is(requestDtoResponse.getId()), Long.class));
        verify(itemRequestService, times(1)).getAll(1L,
                PageCreator.createPage(0, 10));
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
        verify(itemRequestService, times(1))
                .getItemRequestById(1L, requestDtoResponse.getId());
    }
}
