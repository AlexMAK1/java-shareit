package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemConverter itemConverter;

    private final ItemService itemService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemConverter itemConverter, ItemService itemService) {
        this.itemConverter = itemConverter;
        this.itemService = itemService;
    }

    @PostMapping()
    public ItemDto create(@RequestHeader(HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
        Item item = itemConverter.toItem(itemDto);
        item.setUserId(userId);
        return itemConverter.toItemDto(itemService.create(item));
    }


    @PatchMapping("{id}")
    public ItemDto update(@RequestHeader(HEADER) Long userId, @RequestBody ItemDto itemDto, @PathVariable("id") long id) {
        Item item = itemConverter.toItem(itemDto);
        item.setUserId(userId);
        item.setId(id);
        return itemConverter.toItemDto(itemService.update(item));
    }

    @GetMapping("{id}")
    public ItemDto getItem(@PathVariable("id") long id) {
        return itemConverter.toItemDto(itemService.getItem(id));
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(HEADER) Long userId) {
        List<Item> items = itemService.getItems(userId);
        log.info("Находим все веши пользователя: {} {}", userId, items);
        return items.stream()
                .map(itemConverter::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public Collection<ItemDto> getSearchItem(@RequestParam String text) {
        List<Item> items = itemService.getSearchItem(text);
        return items.stream()
                .map(itemConverter::toItemDto)
                .collect(Collectors.toList());
    }
}
