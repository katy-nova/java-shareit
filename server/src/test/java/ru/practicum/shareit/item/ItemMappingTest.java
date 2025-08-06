package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemMappingTest {

    @Autowired
    private ItemMapping itemMapping; // Будет внедрена реализация ItemMappingImpl

    @Test
    void fromDto_shouldMapItemCreateDtoToItem() {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Item name");
        dto.setDescription("Item description");
        dto.setAvailable(true);
        dto.setRequestId(1L);

        Item result = itemMapping.fromDto(dto);

        assertNotNull(result);
        assertEquals("Item name", result.getName());
        assertEquals("Item description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void fromUpdateDto_shouldMapItemUpdateDtoToItem() {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Updated name");
        dto.setDescription("Updated description");
        dto.setAvailable(false);

        Item result = itemMapping.fromUpdateDto(dto);

        assertNotNull(result);
        assertEquals("Updated name", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void toDto_shouldMapItemToItemDto() {
        // Создаем владельца (автора комментария)
        User author = new User();
        author.setName("Author Name");

        // Создаем комментарий с автором
        Comment comment = new Comment();
        comment.setAuthor(author);

        // Создаем предмет с комментариями
        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setComments(Set.of(comment)); // Добавляем комментарий с автором

        ItemDto result = itemMapping.toDto(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test item", result.getName());
        assertEquals("Test description", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(1, result.getComments().size());

        // Дополнительная проверка комментария
        CommentDto commentDto = result.getComments().iterator().next();
        assertEquals("Author Name", commentDto.getAuthorName());
    }

    @Test
    void toDtoSimple_shouldMapItemToItemDtoSimple() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Simple item");

        ItemDtoSimple result = itemMapping.toDtoSimple(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Simple item", result.getName());
    }

    @Test
    void toDtoComment_shouldMapCommentToCommentDto() {
        User author = new User();
        author.setName("Author Name");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setCreated(Instant.now());
        comment.setAuthor(author);

        CommentDto result = itemMapping.toDtoComment(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test comment", result.getText());
        assertEquals("Author Name", result.getAuthorName());
    }

    @Test
    void toDtoForRequest_shouldMapItemToItemDtoForRequest() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Request item");
        item.setOwner(owner);

        ItemDtoForRequest result = itemMapping.toDtoForRequest(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Request item", result.getName());
        assertEquals(1L, result.getOwnerId());
    }
}