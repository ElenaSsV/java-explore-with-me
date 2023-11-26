package ru.practicum.mainService.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.mainService.user.dto.NewUserRequest;
import ru.practicum.mainService.user.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {

    UserDto postNewUser(NewUserRequest newUserRequest);

    void deleteUserById(Long userId);

    List<UserDto> getUsersByIds(Set<Long> ids, PageRequest page);
}
