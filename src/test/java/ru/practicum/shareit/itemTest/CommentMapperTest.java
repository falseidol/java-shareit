package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNull;

public class CommentMapperTest {
    CommentDto commentDto = null;
    User user = null;
    Item item = null;
    LocalDateTime created = null;

    @Test
    void fromDtoTest() {
        assertNull(CommentMapper.fromDto(commentDto, user, item, created));
    }

    @Test
    void commentAuthorNameCommentIsNullTest() {
        Comment comment = null;
        assertNull(CommentMapper.commentAuthorName(comment));
    }

    @Test
    void commentAuthorNameAuthorIsNullTest() {
        Comment comment = new Comment(1L, "text", item, null, LocalDateTime.now());
        assertNull(CommentMapper.commentAuthorName(comment));
    }

    @Test
    void commentAuthorNameUsersNameIsNullTest() {
        User user1 = new User(1L,null,"email@mail.ru");
        Comment comment = new Comment(1L, "text", null, user1, LocalDateTime.now());
        assertNull(CommentMapper.commentAuthorName(comment));
    }
}