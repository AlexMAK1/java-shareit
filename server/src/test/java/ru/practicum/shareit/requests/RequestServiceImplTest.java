package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemConverter;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestServiceImplTest {

    RequestServiceImpl requestService;

    RequestRepository requestRepository;

    UserRepository userRepository;

    ItemRepository itemRepository;

    User user1 = new User(1L, "User1", "user1@mail");
    User user2 = new User(2L, "User2", "user2@mail");
    Item item1 = new Item("item1", "table", true, user1, 1L);
    Item item2 = new Item("item2", "chair", true, user2, 2L);

    List<Item> items = List.of(item1, item2);
    List<ItemDto> itemDtos = items.stream()
            .map(ItemConverter::toItemDto)
            .collect(Collectors.toList());

    ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "request1", LocalDateTime.now(), itemDtos);
    ItemRequest itemRequest = RequestConverter.toItemRequest(itemRequestDto, LocalDateTime.now(), user1);

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        requestRepository = mock(RequestRepository.class);
        requestService = new RequestServiceImpl(requestRepository, itemRepository, userRepository);
    }

    @Test
    void create() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user1);
        when(requestRepository.save(any()))
                .thenReturn(itemRequest);

        ItemRequestDto newItemRequestDto = requestService.create(itemRequestDto, user1.getId());

        assertNotNull(newItemRequestDto);
        assertEquals("request1", newItemRequestDto.getDescription());
        assertEquals(1L, newItemRequestDto.getId());
    }

    @Test
    void getRequests() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user1);
        when(requestRepository.findByRequestor(user1))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(item1);
        List<ItemRequestDto> itemRequestDtos = requestService.getRequests(user1.getId());

        assertNotNull(itemRequestDtos);
        assertEquals("request1", itemRequestDtos.get(0).getDescription());

    }

    @Test
    void getAllRequestsBlank() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user1);
        when(requestRepository.findAllByRequestor(user1, PageRequest.ofSize(10)))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(item1);

        List<ItemRequestDto> itemRequestDtos = requestService.getAllRequests(PageRequest.ofSize(10), 1L);

        assertNotNull(itemRequestDtos);
    }

    @Test
    void getAllRequests() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user1);
        when(requestRepository.findAllByRequestor(user1, PageRequest.ofSize(10)))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(item1);
        when(requestRepository.findAll())
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> itemRequestDtos = requestService.getAllRequests(PageRequest.ofSize(10), 2L);

        assertNotNull(itemRequestDtos);
    }

    @Test
    void getAllRequestsEmpty() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("Ошибка, валидация " +
                        "не пройдена. Пользователя с данным id не существует"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userRepository.findById(5L));

        Assertions.assertEquals("Ошибка, валидация не пройдена. Пользователя с данным id " +
                "не существует", exception.getMessage());
    }

    @Test
    void getRequest() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("request1");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request1));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(item1);

        ItemRequestDto itemRequestDto = requestService.getRequest(request1.getId(), user1.getId());

        assertNotNull(itemRequestDto);
        assertEquals(request1.getId(), itemRequestDto.getId());
    }
}
