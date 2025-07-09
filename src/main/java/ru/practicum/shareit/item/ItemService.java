package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.AccessDenyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapping itemMapping;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public ItemDtoWithBookings getItem(Long id, Long userId) {
        Item item = findItemById(id);
        ItemDtoWithBookings dto = new ItemDtoWithBookings();
        // просто по приколу, здесь можно было также сделать через маппер и join fetch
        dto.setId(id);
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        dto.setComments(item.getComments().stream().map(itemMapping::toDtoComment).toList());
        if (item.getOwner().getId().equals(userId)) {
            dto.setLastBooking(bookingRepository.findFirstByItemIdAndEndBeforeNow(id));
            dto.setNextBooking(bookingRepository.findFirstByItemIdAndStartAfterNow(id));
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ItemDtoWithBookings> getItemsByOwnerId(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId).stream().map(itemMapping::toDtoWithBookings).collect(Collectors.toList());
    }

    public ItemDto createItem(ItemCreateDto itemDto, Long ownerId) {
        Item item = itemMapping.fromDto(itemDto);
        User owner = findUserById(ownerId);
        item.setOwner(owner);
        return itemMapping.toDto(itemRepository.save(item));
    }

    public ItemDto updateItem(Long id, ItemUpdateDto itemUpdateDto, Long ownerId) {
        Item item = findItemById(id);
        checkItem(item, ownerId);
        if (itemUpdateDto.getAvailable() != null && !itemUpdateDto.getAvailable().equals(item.getAvailable())) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        if (itemUpdateDto.getName() != null && !itemUpdateDto.getName().isEmpty() && !itemUpdateDto.getName().equals(item.getName())) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null && !itemUpdateDto.getDescription().equals(item.getDescription())) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        return itemMapping.toDto(itemRepository.save(item));
    }

    public void deleteItem(Long id, Long ownerId) {
        Item item = findItemById(id);
        checkItem(item, ownerId);
        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByText(text).stream().map(itemMapping::toDto).collect(Collectors.toList());
    }

    public CommentDto postComment(CommentCreateDto dto, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalStateException("Item not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdOrderByStartAsc(userId, itemId)
                .orElseThrow(() -> new IllegalStateException(String.format("Пользователь с id: %s не брал в аренду вещи с id: %d", userId, itemId)));
        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Срок аренды еще не истек");
        }
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        return itemMapping.toDtoComment(commentRepository.save(comment));
    }

    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream().map(itemMapping::toDtoComment).collect(Collectors.toList());
    }

    private void checkItem(Item item, Long ownerId) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new AccessDenyException("Редактировать вещь может только её владелец");
        }
    }

    private Item findItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Владелец не найден"));
    }
}
