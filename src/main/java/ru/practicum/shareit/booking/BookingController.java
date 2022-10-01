package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private static final String STATE_VALUE = "ALL";

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @PostMapping()
    public BookingDto create(@RequestHeader(USER_ID_HEADER) long userId, @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("{id}")
    public BookingResponseDto update(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable("id") long id,
                                     @RequestParam Boolean approved) {
        return bookingService.update(id, userId, approved);
    }

    @GetMapping("{id}")
    public BookingResponseDto getBooking(@PathVariable("id") long id, @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getBooking(id, userId);
    }

    @GetMapping()
    public List<BookingResponseDto> getBookings(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                Integer from, @PositiveOrZero @RequestParam(name = "size",
            defaultValue = "10") Integer size, @RequestParam(defaultValue = STATE_VALUE) String state,
                                                @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get all requests from={}, size={}", from, size);
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("start").descending());
        return bookingService.getBookings(state, userId, pageRequest);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                     Integer from, @PositiveOrZero @RequestParam(name = "size",
            defaultValue = "10") Integer size, @RequestParam(defaultValue = STATE_VALUE) String state,
                                                     @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get all requests from={}, size={}", from, size);
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("start").descending());
        return bookingService.getOwnerBookings(state, userId, pageRequest);
    }
}
