package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HttpHeaders;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getUsersRequests(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestClient.getUsersRequests(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsBesidesUsers(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestClient.getIRequestsBesidesUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@Positive @PathVariable("requestId") Long requestId) {
        return requestClient.getRequest(requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestCreateDto request,
                                                @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return requestClient.createRequest(userId, request);
    }
}
