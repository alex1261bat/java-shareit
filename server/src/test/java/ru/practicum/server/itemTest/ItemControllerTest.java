package ru.practicum.server.itemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.item.*;
import ru.practicum.server.pageCreator.PageCreator;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    @MockBean
    private final ItemService itemService;
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final ItemDto itemDto = new ItemDto(1L, "item", "description", true,
            1L, null);
    private final ItemWithBookingDatesDto itemDtoWithBookingDates = new ItemWithBookingDatesDto(1L, "item",
            "description", true, null, null, null, null);

    @Test
    void saveNewItemTest() throws Exception {
        when(itemService.saveNewItem(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
        verify(itemService, times(1)).saveNewItem(1L, itemDto);
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
        verify(itemService, times(1)).update(1L, itemDto.getId(), itemDto);
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDtoWithBookingDates);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
        verify(itemService, times(1)).getById(1L, itemDto.getId());
    }

    @Test
    void getAllUserItemsTest() throws Exception {
        when(itemService.getUserItems(anyLong(), any())).thenReturn(Collections.singletonList(itemDtoWithBookingDates));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("getState", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
        verify(itemService, times(1))
                .getUserItems(1L, PageCreator.createPage(0, 5));
    }

    @Test
    void findAvailableItemsTest() throws Exception {
        when(itemService.findAvailableItems(anyString(), any())).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("getState", "0")
                        .param("text", "cook")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
        verify(itemService, times(1))
                .findAvailableItems("cook", PageCreator.createPage(0, 5));
    }

    @Test
    void addCommentTest() throws Exception {
        LocalDateTime testTime = LocalDateTime.of(2022, 1, 1, 1, 1, 1,
                1);
        CommentRequestDto commentRequestDto = new CommentRequestDto(1L, "comment",
                1L, 1L, testTime);
        CommentResponseDto commentResponseDto = new CommentResponseDto(1L, "comment",
                "authorName", testTime);

        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthorName()), String.class))
                .andExpect(jsonPath("$.created", is(commentResponseDto.getCreated().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Long.class));
        verify(itemService, times(1)).addComment(1L, 1L, commentRequestDto);
    }
}
