package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    private static final String HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(HEADER) long userId) {
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Get booking with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getRequest(@RequestHeader(HEADER) long userId, @PathVariable("id") long id) {
        return itemRequestClient.getRequest(id, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER) long userId, @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }
}
