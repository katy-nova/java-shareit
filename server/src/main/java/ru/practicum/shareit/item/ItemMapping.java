package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.BookingMapping;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;

@Mapper(componentModel = "spring", uses = BookingMapping.class)
public abstract class ItemMapping {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Mapping(target = "request", expression = "java(getRequestFromId(item))")
    abstract Item fromDto(ItemCreateDto item);

    ItemRequest getRequestFromId(ItemCreateDto item) {
        if (item.getRequestId() == null) {
            return null;
        }
        return itemRequestRepository.findById(item.getRequestId()).orElse(null);
    }

    abstract Item fromUpdateDto(ItemUpdateDto item);

    abstract ItemDto toDto(Item item);

    abstract ItemDtoSimple toDtoSimple(Item item);

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    abstract CommentDto toDtoComment(Comment comment);

    @Mapping(target = "ownerId", expression = "java(item.getOwner().getId())")
    abstract ItemDtoForRequest toDtoForRequest(Item item);
}
