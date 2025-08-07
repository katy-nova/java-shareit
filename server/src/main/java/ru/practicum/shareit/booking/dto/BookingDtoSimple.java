package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoSimple {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
}
