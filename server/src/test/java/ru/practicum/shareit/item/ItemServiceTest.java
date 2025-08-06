package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.AccessDenyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/booking.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldGetItemToOwner() {
        ItemDtoWithBookings item = itemService.getItem(1L, 1L);
        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals(true, item.getAvailable());
        assertEquals("Дрель", item.getName());
        assertEquals("Мощная дрель с набором сверл", item.getDescription());
        assertEquals(1L, item.getRequestId());
        assertEquals(1, item.getComments().size());
        assertEquals(LocalDateTime.of(2023, 6, 5, 11, 0), item.getLastBooking().getStart());
    }

    @Test
    void shouldGetItemToOtherUser() {
        ItemDtoWithBookings item = itemService.getItem(1L, 2L);
        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals(true, item.getAvailable());
        assertEquals("Дрель", item.getName());
        assertEquals("Мощная дрель с набором сверл", item.getDescription());
        assertEquals(1L, item.getRequestId());
        assertNull(item.getLastBooking());
    }

    @Test
    void shouldGetItemWithoutRequest() {
        ItemDtoWithBookings item = itemService.getItem(2L, 2L);
        assertNotNull(item);
        assertEquals(2L, item.getId());
        assertEquals(false, item.getAvailable());
        assertEquals("Велосипед", item.getName());
        assertEquals("Горный велосипед, 21 скорость", item.getDescription());
        assertNull(item.getRequestId());
    }

    @Test
    void shouldNotReturnNonExistedItem() {
        assertThrows(NotFoundException.class, () -> itemService.getItem(22L, 2L));
    }

    @Test
    void getItemsByOwnerId() {
        List<ItemDtoWithBookings> items = itemService.getItemsByOwnerId(1L);
        assertNotNull(items);
        assertEquals(3, items.size());
    }

    @Test
    void createItem() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setAvailable(true);
        itemCreateDto.setName("New Name");
        itemCreateDto.setDescription("New Description");
        ItemDto itemDto = itemService.createItem(itemCreateDto, 1L);
        assertNotNull(itemDto);
        assertEquals(10L, itemDto.getId());
        assertEquals(true, itemDto.getAvailable());
        assertEquals("New Name", itemDto.getName());
        assertEquals("New Description", itemDto.getDescription());
    }

    @Test
    void shouldNotCreateItemWithNonExistentOwner() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setAvailable(false);
        itemCreateDto.setName("New Name");
        itemCreateDto.setDescription("New Description");
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemCreateDto, 11L));
    }

    @Test
    void updateAvailable() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setAvailable(false);
        itemUpdateDto.setName("");
        itemUpdateDto.setDescription("Мощная дрель с набором сверл");
        ItemDto itemDto = itemService.updateItem(1L, itemUpdateDto, 1L);
        assertNotNull(itemDto);
        assertEquals(false, itemDto.getAvailable());
    }

    @Test
    void updateName() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("New Name");
        itemUpdateDto.setAvailable(true);
        ItemDto itemDto = itemService.updateItem(1L, itemUpdateDto, 1L);
        assertNotNull(itemDto);
        assertEquals("New Name", itemDto.getName());
    }

    @Test
    void updateDescription() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setDescription("New Description");
        itemUpdateDto.setName("Дрель");
        ItemDto itemDto = itemService.updateItem(1L, itemUpdateDto, 1L);
        assertNotNull(itemDto);
        assertEquals("New Description", itemDto.getDescription());
    }

    @Test
    void shouldNotUpdateItem() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setAvailable(false);
        assertThrows(AccessDenyException.class, () -> itemService.updateItem(1L, itemUpdateDto, 2L));
    }

    @Test
    void deleteItem() {
        itemService.deleteItem(1L, 1L);
        assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 1L));
    }

    @Test
    void getItemsByText() {
        List<ItemDto> items = itemService.getItemsByText("дре");
        assertNotNull(items);
        assertEquals(1, items.size());
        ItemDto itemDto = items.get(0);
        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals(true, itemDto.getAvailable());
        assertEquals("Дрель", itemDto.getName());
    }

    @Test
    void shouldGetEmptyList() {
        List<ItemDto> items = itemService.getItemsByText("");
        assertTrue(items.isEmpty());
    }

    @Test
    void shouldGetEmptyList2() {
        List<ItemDto> items = itemService.getItemsByText(null);
        assertTrue(items.isEmpty());
    }

    @Test
    void postComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("New Comment");
        CommentDto commentDto = itemService.postComment(commentCreateDto, 1L, 5L);
        assertNotNull(commentDto);
        assertEquals("New Comment", commentDto.getText());
        assertEquals(4L, commentDto.getId());
        assertEquals("Иван Иванов", commentDto.getAuthorName());
        assertNotNull(commentDto.getCreated());
    }

    @Test
    void shouldNotPostCommentToNonExistedItem() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("New Comment");
        assertThrows(IllegalStateException.class, () -> itemService.postComment(commentCreateDto, 2L, 15L));
    }

    @Test
    void shouldNotPostCommentToNotEndedBooking() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(bookingCreateDto, 3L);
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("New Comment");
        assertThrows(IllegalStateException.class, () -> itemService.postComment(commentCreateDto, 3L, 1L));
    }

    @Test
    void getCommentsByItemId() {
        List<CommentDto> comments = itemService.getCommentsByItemId(1L);
        assertNotNull(comments);
        assertEquals(1, comments.size());
        CommentDto commentDto = comments.get(0);
        assertNotNull(commentDto);
        assertEquals(1L, commentDto.getId());
        assertEquals("Отличная дрель, помогла быстро завершить ремонт!", commentDto.getText());
    }
}