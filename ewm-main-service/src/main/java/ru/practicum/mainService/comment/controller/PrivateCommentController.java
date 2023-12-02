package ru.practicum.mainService.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.comment.dto.CommentDto;
import ru.practicum.mainService.comment.dto.UpdateCommentDto;
import ru.practicum.mainService.comment.dto.NewCommentDto;
import ru.practicum.mainService.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
@Slf4j
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto postCommentToEvent(@PathVariable Long userId,
                                         @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Received request to post new comment with the following params: userId={}, newCommentDtp={}",
                userId, newCommentDto);
        return commentService.postCommentToEvent(userId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentByAuthor(@PathVariable Long userId,
                                            @PathVariable Long commentId,
                                            @RequestBody @Valid UpdateCommentDto updateAuthorRequest) {
        log.info("Received request to update comment with the following params: userId={}. commentId={}," +
                "updateAuthorRequest={}", userId, commentId, updateAuthorRequest);
        return commentService.updateCommentByAuthor(userId, commentId, updateAuthorRequest);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCommentByAuthor(@PathVariable Long userId,
                                      @PathVariable Long commentId) {
        log.info("Received request to delete comment with the following params: userId={}, commentId={}", userId, commentId);
        commentService.deleteCommentByIdByAuthor(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getCommentsByAuthor(@PathVariable Long userId,
                                                @RequestParam(name = "from", defaultValue = "0") int from,
                                                @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Received request to get comments with the following params: userId={}, from={}, size={}", userId, from, size);
        return commentService.getCommentsByAuthor(userId, from, size);
    }
}
