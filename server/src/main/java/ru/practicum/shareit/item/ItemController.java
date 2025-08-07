package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithBookings> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByOwnerId(userId);
    }

    @GetMapping(path = "/{itemId}")
    public ItemDtoWithBookings getItem(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItem(itemId, userId);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Long itemId,
                              @Valid @RequestBody ItemUpdateDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemCreateDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.getItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@PathVariable Long itemId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody CommentCreateDto text) {
        return itemService.postComment(text, userId, itemId);
    }

    @GetMapping("/{itemId}/comment")
    public List<CommentDto> getComments(@PathVariable Long itemId) {
        return itemService.getCommentsByItemId(itemId);
    }
}
