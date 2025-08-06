package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam Integer from,
                                                    @RequestParam Integer size) {
        return itemRequestService.getItemsByUser(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsBesidesUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam Integer from,
                                                           @RequestParam Integer size) {
        return itemRequestService.getAllBesidesUsers(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestCreateDto request) {
        return itemRequestService.create(request, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable("requestId") Long requestId) {
        return itemRequestService.getItem(requestId);
    }
}
