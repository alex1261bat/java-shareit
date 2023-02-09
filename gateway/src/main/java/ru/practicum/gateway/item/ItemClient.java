package ru.practicum.gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.pageValidator.PageValidator;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveNewItem(ItemDto itemDto, Long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, Long itemId, Long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllUserItems(Long userId, Integer from, Integer size) {
        PageValidator.validatePage(from, size);

        Map<String, Object> parameters = Map.of(
                "getState", from,
                "size", size
        );
        return get("?getState={getState}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAvailableItems(String text, Integer from, Integer size) {
        PageValidator.validatePage(from, size);

        Map<String, Object> parameters = Map.of(
                "text", text,
                "getState", from,
                "size", size
        );
        return get("/search?text={text}&getState={getState}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> addComment(CommentRequestDto commentRequestDto, Long userId, Long itemId) {
        commentRequestDto.setCreated(LocalDateTime.now());
        commentRequestDto.setItemId(itemId);
        commentRequestDto.setAuthorId(userId);

        return post("/" + commentRequestDto.getItemId() + "/comment", commentRequestDto.getAuthorId(),
                commentRequestDto);
    }
}
