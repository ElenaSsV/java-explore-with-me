package ru.practicum.mainService.comment.mapper;

import ru.practicum.mainService.comment.dto.CommentDto;
import ru.practicum.mainService.comment.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setCreated(comment.getCreated());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setText(comment.getText());
        dto.setEdited(comment.getEdited());

        return dto;
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }
        List<CommentDto> dtos = new ArrayList<>();

        for (Comment comment : comments) {
            dtos.add(toCommentDto(comment));
        }
        return dtos;
    }
}
