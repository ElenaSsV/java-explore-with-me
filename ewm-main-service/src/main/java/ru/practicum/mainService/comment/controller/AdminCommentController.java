package ru.practicum.mainService.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.comment.dto.GetCommentsAdminRequest;
import ru.practicum.mainService.comment.service.CommentService;
import ru.practicum.mainService.comment.dto.CommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/comments")
@Slf4j
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(@RequestParam(name = "eventId", required = false) Long  eventId,
                                        @RequestParam(name = "edited", required = false) Boolean edited,
                                        @RequestParam(name = "authorId", required = false) Long authorId,
                                        @RequestParam(name = "text", required = false) String text,
                                        @RequestParam(name = "sort", required = false, defaultValue = "NEWEST") String commentSort,
                                        @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                        @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                        @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        log.info("Received request to get comments with the following params: eventId={}, edited={}, authorId={}, " +
                "text={}, commentSort={}, " +
                "from={}, size={}", eventId, edited, authorId, text, commentSort, from, size);

        return commentService.getCommentsByAdmin(GetCommentsAdminRequest.of(eventId, edited, authorId, text, commentSort,
                rangeStart, rangeEnd), from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable Long commentId) {
        log.info("Received request to delete comment with id={} by admin", commentId);
        commentService.deleteCommentByIdByAdmin(commentId);
    }
}
