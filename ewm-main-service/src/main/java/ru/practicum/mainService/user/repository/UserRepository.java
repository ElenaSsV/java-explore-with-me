package ru.practicum.mainService.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainService.user.model.User;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByIdIn(Set<Long> ids, Pageable page);
}
