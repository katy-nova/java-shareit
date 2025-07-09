package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
    private Long itemId;
    private Status status = Status.WAITING;
}
