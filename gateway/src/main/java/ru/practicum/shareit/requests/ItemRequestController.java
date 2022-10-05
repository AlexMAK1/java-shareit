package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;

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
    public ResponseEntity<Object> getAllRequests(@RequestParam(name = "from", defaultValue = "0")
                                               Integer from, @RequestParam(name = "size",
            defaultValue = "10") Integer size, @RequestHeader(HEADER) long userId) {
        log.info("Get all requests from={}, size={}", from, size);
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
