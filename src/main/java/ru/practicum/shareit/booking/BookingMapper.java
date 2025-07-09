package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Mapping(target = "booker", expression = "java(userRepository.findById(bookingCreateDto.getBookerId()).orElse(null))")
    @Mapping(target = "item", expression = "java(itemRepository.findById(bookingCreateDto.getItemId()).orElse(null))")
    public abstract Booking fromDto(BookingCreateDto bookingCreateDto);

    @Mapping(target = "bookerId", expression = "java(booking.getBooker().getId())")
    @Mapping(target = "itemId", expression = "java(booking.getItem().getId())")
    public abstract BookingDto toDto(Booking booking);
}
