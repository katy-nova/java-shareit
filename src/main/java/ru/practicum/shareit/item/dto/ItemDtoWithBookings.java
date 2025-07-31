package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoWithBookings {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingDtoSimple nextBooking;
    private BookingDtoSimple lastBooking;
    private List<CommentDto> comments = new ArrayList<>();
}
