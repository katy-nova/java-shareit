package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AccessDenyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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

    @Transactional(readOnly = true)
    public ItemDto getItem(Long id) {
        Item item = findItemById(id);
        return itemMapping.toDto(item);
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId).stream().map(itemMapping::toDto).collect(Collectors.toList());
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
        if (itemUpdateDto.getName() != null && !itemUpdateDto.getName().equals(item.getName())) {
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

    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByText(text).stream().map(itemMapping::toDto).collect(Collectors.toList());
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
