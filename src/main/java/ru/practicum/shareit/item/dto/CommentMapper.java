package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public final class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto.CommentDtoBuilder commentDto = CommentDto.builder();

        commentDto.authorName(commentAuthorName(comment));
        commentDto.id(comment.getId());
        commentDto.text(comment.getText());

        return commentDto.build();
    }

    public static Comment fromDto(CommentDto commentDto, User user, Item item, LocalDateTime created) {
        if (commentDto == null && user == null && item == null && created == null) {
            return null;
        }

        Comment.CommentBuilder comment = Comment.builder();

        if (commentDto != null) {
            comment.text(commentDto.getText());
        }
        comment.author(user);
        comment.item(item);
        comment.created(created);

        return comment.build();
    }

    private static String commentAuthorName(Comment comment) {
        if (comment == null) {
            return null;
        }
        User author = comment.getAuthor();
        if (author == null) {
            return null;
        }
        String name = author.getName();
        if (name == null) {
            return null;
        }
        return name;
    }
}