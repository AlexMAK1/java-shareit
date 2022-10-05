package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

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

    public ResponseEntity<Object> getItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto itemDto) {
        return post("/", userId, itemDto);
    }

    public ResponseEntity<Object> getItem(long userId, Long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> update(long userId, Long id, ItemDto itemDto) {
        return patch("/" + userId, id, itemDto);
    }

    public ResponseEntity<Object> getSearchItem(String text) {
        return get("/" + text);
    }

    public ResponseEntity<Object> createComment(long userId, CommentDto commentDto, Long itemId) {
        return post("/" + userId, itemId, commentDto);
    }
}
