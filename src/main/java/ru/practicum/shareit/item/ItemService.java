package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

interface ItemService {

    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long id);

    ItemInfoDto getItem(long id, long userId);

    List<ItemInfoDto> getItems(long userId);

    List<ItemDto> getSearchItem(String text);

    CommentDto createComment(CommentDto commentDto, long userId, long itemId);
}
