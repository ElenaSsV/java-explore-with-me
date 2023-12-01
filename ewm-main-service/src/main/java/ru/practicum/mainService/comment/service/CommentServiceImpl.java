package ru.practicum.mainService.comment.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.comment.dto.CommentUpdateAuthorRequest;
import ru.practicum.mainService.comment.dto.GetCommentsAdminRequest;
import ru.practicum.mainService.comment.dto.NewCommentDto;
import ru.practicum.mainService.comment.model.Comment;
import ru.practicum.mainService.comment.model.CommentSort;
import ru.practicum.mainService.comment.model.QComment;
import ru.practicum.mainService.comment.repository.CommentRepository;
import ru.practicum.mainService.comment.dto.CommentDto;
import ru.practicum.mainService.comment.mapper.CommentMapper;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.model.State;
import ru.practicum.mainService.event.repository.EventRepository;
import ru.practicum.mainService.exception.ForbiddenOperation;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto postCommentToEvent(long userId, NewCommentDto newCommentDto) {
        log.info("Posting comment={} by user with id={}", newCommentDto, userId);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " is not found."));
        Event event = checkEvent(newCommentDto.getEventId());

        Comment newComment = new Comment();
        newComment.setAuthor(user);
        newComment.setEvent(event);
        newComment.setText(newCommentDto.getText());

        log.info("Saving new comment={}", newComment);

        return CommentMapper.toCommentDto(commentRepository.save(newComment));
    }

    @Override
    public List<CommentDto> getCommentsByAdmin(GetCommentsAdminRequest adminRequest, int from, int size) {
        log.info("Retrieving comments with the following params: adminRequest={}, from={}, size={}", adminRequest, from, size);

        QComment comment = QComment.comment;
        List<BooleanExpression> conditions = new ArrayList<>();

        if (adminRequest.hasEventId()) {
            checkIfEventExists(adminRequest.getEventId());
            conditions.add(comment.event.id.eq(adminRequest.getEventId()));
        }
        if (adminRequest.hasEdited()) {
            conditions.add(comment.edited.eq(adminRequest.getEdited()));
        }
        if (adminRequest.hasAuthorId()) {
            conditions.add(comment.author.id.eq(adminRequest.getAuthorId()));
        }

        if (adminRequest.hasRangeStart()) {
            conditions.add(comment.created.after(adminRequest.getRangeStart()));
        }

        if (adminRequest.hasRangeEnd()) {
            conditions.add(comment.created.before(adminRequest.getRangeEnd()));
        }

        if (adminRequest.hasText()) {
            conditions.add(comment.text.containsIgnoreCase(adminRequest.getText()));
        }

        PageRequest pageRequest = PageRequest.of(from, size, makeOrderByClause(adminRequest.getCommentSort()));

        Optional<BooleanExpression> finalCondition = conditions.stream()
                .reduce(BooleanExpression::and);

        return finalCondition.map(booleanExpression -> commentRepository.findAll(booleanExpression, pageRequest).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList())).orElseGet(() -> commentRepository.findAll(pageRequest).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public void deleteCommentByIdByAdmin(long commentId) {
        log.info("Deleting comment with id={} by admin", commentId);

        checkIfCommentExists(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByIdByAuthor(long userId, long commentId) {
        log.info("Deleting comment with id={} by user with id={}", commentId, userId);

        if (!commentRepository.existsByIdAndAuthor_Id(commentId, userId)) {
            throw new NotFoundException("Comment with id=" + commentId + " and author with id=" + userId + " is not found.");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public CommentDto updateCommentByAuthor(long userId, long commentId, CommentUpdateAuthorRequest updateAuthorRequest) {
        log.info("Updating comment with the following params; commentId={}, userId={}, updateAuthorRequest={}",
                commentId, userId, updateAuthorRequest);

        Comment commentToUpdate = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id=" + commentId + " is not found."));
        if (commentToUpdate.getAuthor().getId() != userId) {
            throw new NotFoundException("Comment with author with id=" + userId + " is not found.");
        }
        if (commentToUpdate.getEdited().equals(true)) {
            throw new ForbiddenOperation("Comment can be edited only once.");
        }
        commentToUpdate.setText(updateAuthorRequest.getText());
        commentToUpdate.setEdited(true);

        return CommentMapper.toCommentDto(commentRepository.save(commentToUpdate));
    }

    @Override
    public List<CommentDto> getCommentsByAuthor(long userId, int from, int size) {
        log.info("Retrieving comments by following params: userId={}, from={}, size={}", userId, from, size);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " is not found.");
        }
        PageRequest pageRequest = PageRequest.of(from, size);

        return commentRepository.findAllByAuthor_Id(userId, pageRequest).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private Event checkEvent(long eventId) {
        Event searchedEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " is not found."));
        if (!searchedEvent.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " is not found.");
        }
        return searchedEvent;
    }

    private void checkIfEventExists(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + eventId + " is not found.");
        }
    }

    private void checkIfCommentExists(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with id=" + commentId + " is not found.");
        }
    }

    private Sort makeOrderByClause(CommentSort sort) {
        switch (sort) {
            case OLDEST: return Sort.by("created").ascending();
            case NEWEST:
            default: return Sort.by("created").descending();
        }
    }
}
