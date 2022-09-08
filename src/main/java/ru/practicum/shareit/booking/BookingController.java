package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.List;

@RestController
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
    public BookingDto create(@RequestHeader(USER_ID_HEADER) long userId, @Valid @RequestBody BookingDto bookingDto)
            throws ValidationException {
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
    public List<BookingResponseDto> getBookings(@RequestParam(defaultValue = STATE_VALUE) String state, @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestParam(defaultValue = STATE_VALUE) String state, @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getOwnerBookings(state, userId);
    }
}
