package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HttpHeaders;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getRequestsByUserId(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId,
                                                    @RequestParam Integer from,
                                                    @RequestParam Integer size) {
        return itemRequestService.getItemsByUser(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsBesidesUsers(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId,
                                                           @RequestParam Integer from,
                                                           @RequestParam Integer size) {
        return itemRequestService.getAllBesidesUsers(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId,
                                        @RequestBody ItemRequestCreateDto request) {
        return itemRequestService.create(request, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable("requestId") Long requestId) {
        return itemRequestService.getItem(requestId);
    }
}
