package ru.practicum.shareit.booking.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ItemsServiceImplTest {

    ItemService itemService;
    ItemRepository itemRepository;

    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;

    User user1 = new User(1L, "User1", "user1@mail");
    User user2 = new User(2L, "User2", "user2@mail");

    Item item1 = new Item("item1", "table", true, user1, 1L);
    ItemDto itemDto = new ItemDto(1L, "item1", "description itemDto", true, 1L);

    LocalDateTime start = LocalDateTime.of(2022, 9, 23, 8, 00);
    LocalDateTime end = LocalDateTime.of(2022, 9, 23, 9, 00);

    Booking booking = new Booking(start, end, item1, user1);

    Booking bookingLast = new Booking(start, end, item1, user1);

    Booking bookingNext = new Booking(start, end, item1, user1);

    Comment comment = new Comment(1L, "comment", item1, user1, LocalDateTime.now());

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemsServiceImpl(userRepository, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void create() {
        Item item = ItemConverter.toItem(itemDto, user1);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto itemDto1 = itemService.create(itemDto, user1.getId());

        verify(itemRepository, times(1)).save(item);
        assertNotNull(itemDto1);
        assertEquals("item1", itemDto1.getName());
    }

    @Test
    void update() {
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item1);
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(item1);

        Item item = ItemConverter.toItem(itemDto, user1);

        when(itemRepository.save(any()))
                .thenReturn(item);
        ItemDto itemDto1 = itemService.update(itemDto, user1.getId(), 1L);

        assertNotNull(itemDto1);
        assertEquals("description itemDto", itemDto1.getDescription());
    }

    @Test
    void getItemWithNull() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        ItemInfoDto itemInfoDto1 = itemService.getItem(1L, 1L);

        assertNotNull(itemInfoDto1);
        assertEquals("table", itemInfoDto1.getDescription());
        assertEquals(Collections.emptyList(), itemInfoDto1.getComments());
    }

    @Test
    void getItemWithBookingsNull() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findByItemAndAndEndBefore(any(), any()))
                .thenReturn(bookingLast);
        when(bookingRepository.findByItemAndAndEndAfter(any(), any()))
                .thenReturn(bookingNext);
        when(commentRepository.findByItem(any()))
                .thenReturn(comment);
        when(commentRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));

        ItemInfoDto itemInfoDto1 = itemService.getItem(1L, 2L);

        assertNotNull(itemInfoDto1);
        assertEquals("table", itemInfoDto1.getDescription());
        assertEquals("comment", itemInfoDto1.getComments().get(0).getText());
    }

    @Test
    void getItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findByItemAndAndEndBefore(any(), any()))
                .thenReturn(bookingLast);
        when(bookingRepository.findByItemAndAndEndAfter(any(), any()))
                .thenReturn(bookingNext);
        when(commentRepository.findByItem(any()))
                .thenReturn(comment);
        when(commentRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));

        ItemInfoDto itemInfoDto1 = itemService.getItem(1L, 1L);

        assertNotNull(itemInfoDto1);
        assertEquals("table", itemInfoDto1.getDescription());
        assertEquals("comment", itemInfoDto1.getComments().get(0).getText());
    }

    @Test
    void getItems() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findItemsByOwner(any()))
                .thenReturn(List.of(item1));
        when(bookingRepository.findByItemAndAndEndBefore(any(), any()))
                .thenReturn(bookingLast);
        when(bookingRepository.findByItemAndAndEndAfter(any(), any()))
                .thenReturn(bookingNext);
        when(commentRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));

        List<ItemInfoDto> items = itemService.getItems(1L);
        assertNotNull(items);
        assertEquals("table", items.get(0).getDescription());
        assertEquals("comment", items.get(0).getComments().get(0).getText());
    }

    @Test
    void getItemsBookingsNull() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findItemsByOwner(any()))
                .thenReturn(List.of(item1));

        List<ItemInfoDto> items = itemService.getItems(1L);
        assertNotNull(items);
    }


    @Test
    void getSearchItem() {
        when(itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableTrue(anyString()))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtoList = itemService.getSearchItem("tab");
        assertNotNull(itemDtoList);

    }

    @Test
    void getSearchItemBlank() {
        when(itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableTrue(anyString()))
                .thenReturn(Collections.emptyList());

        List<ItemDto> itemDtoList = itemService.getSearchItem("");
        assertNotNull(itemDtoList);

    }

    @Test
    void createComment() {
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item1);
        when(bookingRepository.findTopByItem(item1))
                .thenReturn(booking);
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user1);
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentDto = ItemConverter.toCommentDto(comment);
        CommentDto commentDto1 = itemService.createComment(commentDto, 1L, 1L);

        assertNotNull(commentDto1);
    }
}
