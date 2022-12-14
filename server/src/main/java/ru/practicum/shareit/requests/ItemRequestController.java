package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;

    private static final String HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(HEADER) long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(name = "from", defaultValue = "0")
                                               Integer from, @RequestParam(name = "size",
            defaultValue = "10") Integer size, @RequestHeader(HEADER) long userId) {
        log.info("Get all requests from={}, size={}", from, size);
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return requestService.getAllRequests(pageRequest, userId);
    }

    @GetMapping("{id}")
    public ItemRequestDto getRequest(@RequestHeader(HEADER) long userId, @PathVariable("id") long id) {
        return requestService.getRequest(id, userId);
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader(HEADER) long userId, @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.create(itemRequestDto, userId);
    }
}
