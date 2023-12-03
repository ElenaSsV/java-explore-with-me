package ru.practicum.mainService.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.mainService.comment.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    List<Comment> findAllByEvent_IdOrderByCreatedDesc(long eventId);

    Page<Comment> findAllByEvent_Id(long eventId, Pageable page);

    List<Comment> findAllByEvent_IdIn(Set<Long> eventIds);

    boolean existsByIdAndAuthor_Id(long commentId, long authorId);

    Page<Comment> findAllByAuthor_Id(long authorId, Pageable page);

    Page<Comment> findAllByEvent_IdAndEdited(long eventId, boolean edited, Pageable page);
}
