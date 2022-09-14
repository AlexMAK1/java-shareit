package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    private static final String HEADER = "X-Sharer-User-Id";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public ItemDto create(@RequestHeader(HEADER) long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("{id}")
    public ItemDto update(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto, @PathVariable("id") long id) {
        return itemService.update(itemDto, userId, id);
    }

    @GetMapping("{id}")
    public ItemInfoDto getItem(@PathVariable("id") long id, @RequestHeader(HEADER) long userId) {
        return itemService.getItem(id, userId);
    }

    @GetMapping
    public List<ItemInfoDto> getItems(@RequestHeader(HEADER) long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getSearchItem(@RequestParam String text) {
        return itemService.getSearchItem(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto create(@RequestHeader(HEADER) long userId, @Valid @RequestBody CommentDto commentDto, @PathVariable("itemId") long itemId) {
        return itemService.createComment(commentDto, userId, itemId);
    }
}
