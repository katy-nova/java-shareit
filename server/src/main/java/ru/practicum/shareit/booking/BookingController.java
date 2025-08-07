package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody BookingCreateDto booking) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return bookingService.getBooking(bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "ALL") BookingState state,
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getBookingByBookerIdAndStatus(userId, state, from, size);
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> getBookingsByItemOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") BookingState state,
                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getBookingByItemOwnerIdAndStatus(userId, state, from, size);
    }

}
