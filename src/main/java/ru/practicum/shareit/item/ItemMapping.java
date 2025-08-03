package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring", uses = BookingMapper.class)
public interface ItemMapping {

    Item fromDto(ItemCreateDto item);

    Item fromUpdateDto(ItemUpdateDto item);

    ItemDto toDto(Item item);

    ItemDtoSimple toDtoSimple(Item item);

    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "lastBooking", source = "lastBooking")
    ItemDtoWithBookings toDtoWithBookings(Item item);

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    CommentDto toDtoComment(Comment comment);
}
