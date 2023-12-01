package ru.practicum.mainService.comment.service;

import ru.practicum.mainService.comment.dto.CommentDto;
import ru.practicum.mainService.comment.dto.CommentUpdateAuthorRequest;
import ru.practicum.mainService.comment.dto.GetCommentsAdminRequest;
import ru.practicum.mainService.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto postCommentToEvent(long userId, NewCommentDto newCommentDto);

    List<CommentDto> getCommentsByAdmin(GetCommentsAdminRequest adminRequest, int from, int size);

    void deleteCommentByIdByAdmin(long commentId);

    void deleteCommentByIdByAuthor(long userId, long commentId);

    CommentDto updateCommentByAuthor(long userId, long commentId, CommentUpdateAuthorRequest updateAuthorRequest);

    List<CommentDto> getCommentsByAuthor(long userId, int from, int size);
}

