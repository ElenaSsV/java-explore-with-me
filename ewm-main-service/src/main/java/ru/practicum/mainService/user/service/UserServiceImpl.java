package ru.practicum.mainService.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.user.dto.NewUserRequest;
import ru.practicum.mainService.user.dto.UserDto;
import ru.practicum.mainService.user.mapper.UserMapper;
import ru.practicum.mainService.exception.DataConflictException;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.user.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto postNewUser(NewUserRequest newUserRequest) {
        log.info("Posting user={}", newUserRequest);

        User userToPost = UserMapper.toUser(newUserRequest);
        User postedUser;

        try {
            postedUser = userRepository.saveAndFlush(userToPost);
            log.info("Posted user={}", postedUser);
        } catch (DataIntegrityViolationException e) {
            throw new DataConflictException(e.getMessage());
        }

        return UserMapper.toUserDto(postedUser);
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Deleting user with id={}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User is not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsersByIds(Set<Long> ids, PageRequest pageRequest) {
        log.info("Retrieving user with ids={}, pageRequest={}", ids, pageRequest);

        if (ids != null) {
            return userRepository.findAllByIdIn(ids, pageRequest).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findAll(pageRequest).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }


}
