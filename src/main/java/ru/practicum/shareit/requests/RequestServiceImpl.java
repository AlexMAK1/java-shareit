package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    public RequestServiceImpl(RequestRepository requestRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }


    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, long userId) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Ошибка, валидация " +
                "не пройдена. Пользователя с данным id не существует"));
        log.error("Ошибка, валидация не пройдена. Пользователя с данным id не существует: {}", userId);
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = RequestConverter.toItemRequest(itemRequestDto, created, requestor);
        requestRepository.save(itemRequest);
        log.info("Сохраняем новый запрос: {}", itemRequest);
        return RequestConverter.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequests(long userId) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Ошибка, валидация " +
                "не пройдена. Пользователя с данным id не существует"));
        log.error("Ошибка, валидация не пройдена. Пользователя с данным id не существует: {}", userId);
        List<ItemRequest> requests = requestRepository.findByRequestor(requestor);
        for (ItemRequest itemRequest : requests) {
            Item item = itemRepository.findByRequestId(itemRequest.getId());
            List<Item> items = new ArrayList<>();
            if (item != null) {
                items.add(item);
            }
            itemRequest.setItems(items);
        }
        log.info("Находим все запросы пользователя: {} {}", userId, requests);
        return requests.stream()
                .sorted(this::compareAll)
                .map(RequestConverter::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(PageRequest pageRequest, long userId) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Ошибка, валидация " +
                "не пройдена. Пользователя с данным id не существует"));
            log.error("Ошибка, валидация не пройдена. Пользователя с данным id не существует: {}", userId);
        List<ItemRequest> requests = requestRepository.findAllByRequestor(requestor, pageRequest);
        for (ItemRequest itemRequest : requests) {
            if (itemRequest.getRequestor().getId().equals(userId)) {
                requests = new ArrayList<>();
                log.info("Находим все запросы пользователя: {} {}", userId, requests);
                return requests.stream()
                        .sorted(this::compareAll)
                        .map(RequestConverter::toItemRequestDto)
                        .collect(Collectors.toList());
            }
        }
        List<ItemRequest> itemRequestList = requestRepository.findAll();
        for (ItemRequest itemRequest : itemRequestList) {
            Item item = itemRepository.findByRequestId(itemRequest.getId());
            List<Item> items = new ArrayList<>();
            if (item != null) {
                items.add(item);
            }
            itemRequest.setItems(items);
        }
        log.info("Находим все запросы пользователя: {} {}", userId, requests);
        return itemRequestList.stream()
                .sorted(this::compareAll)
                .map(RequestConverter::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequest(Long requestId, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Ошибка, валидация " +
                "не пройдена. Пользователя с данным id не существует"));
        log.error("Ошибка, валидация не пройдена. Пользователя с данным id не существует: {}", userId);
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запроса с таким id" +
                " не существует"));
        Item item = itemRepository.findByRequestId(itemRequest.getId());
        List<Item> items = new ArrayList<>();
        if (item != null) {
            items.add(item);
        }
        itemRequest.setItems(items);
        log.info("Находим запрос с id: {} {}", requestId, itemRequest);
        return RequestConverter.toItemRequestDto(itemRequest);
    }

    private int compareAll(ItemRequest itemRequest1, ItemRequest itemRequest2) {
        return itemRequest1.getCreated().compareTo(itemRequest2.getCreated());
    }
}
