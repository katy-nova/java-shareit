package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting items from user {}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting item from user {}", userId);
        return itemClient.getItem(id, userId);
    }

    @PatchMapping(path = "/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemUpdateDto itemDto
    ) {
        log.info("Updating item {}", itemId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemCreateDto itemDto) {
        log.info("Creating item {}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteItem(@PathVariable("itemId") Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Deleting item {}", itemId);
        return itemClient.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Searching for items with text {}", text);
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> postComment(@PathVariable Long itemId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody CommentCreateDto text) {
        log.info("Posting comment {}", text);
        return itemClient.postComment(itemId, userId, text);
    }

    @GetMapping("/{itemId}/comment")
    public ResponseEntity<Object> getComments(@PathVariable Long itemId) {
        log.info("Getting comments for item {}", itemId);
        return itemClient.getComments(itemId);
    }
}
