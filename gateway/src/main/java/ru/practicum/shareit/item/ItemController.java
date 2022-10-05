package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto, @PathVariable("id") long id) {
        return itemClient.update(userId, id, itemDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getItem(@PathVariable("id") long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchItem(@RequestParam String text) {
        return itemClient.getSearchItem(text);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid CommentDto commentDto, @PathVariable("itemId") long itemId) {
        return itemClient.createComment(userId, commentDto,  itemId);
    }
}
