package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserMapping;
import ru.practicum.shareit.user.UserRepository;

@Mapper(componentModel = "spring", uses = UserMapping.class)
public abstract class BookingMapper {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    //@Mapping(target = "booker", expression = "java(userRepository.findById(bookingCreateDto.getBookerId()).orElse(null))")
    @Mapping(target = "item", expression = "java(itemRepository.findById(bookingCreateDto.getItemId()).orElse(null))")
    public abstract Booking fromDto(BookingCreateDto bookingCreateDto);

    @Mapping(target = "booker", source = "booker")
    public abstract BookingDto toDto(Booking booking);

    public abstract BookingDtoSimple toDtoSimple(Booking booking);
}
