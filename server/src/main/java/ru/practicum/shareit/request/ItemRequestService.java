package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapping itemRequestMapping;

    @Transactional
    public ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, Long userId) {
        User user = getUserById(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setDescription(itemRequestCreateDto.getDescription());
        return itemRequestMapping.toDto(itemRequestRepository.saveAndFlush(itemRequest));
    }

    public List<ItemRequestDto> getItemsByUser(Long userId, Integer from, Integer size) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRequestRepository.findByRequesterOrderByCreatedDesc(user, pageable).stream()
                .map(itemRequestMapping::toDto)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllBesidesUsers(Long userId, Integer from, Integer size) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRequestRepository.findByRequesterNotOrderByCreatedDesc(user, pageable).stream()
                .map(itemRequestMapping::toDto)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getItem(Long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Request not found"));
        return itemRequestMapping.toDto(itemRequest);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

}
