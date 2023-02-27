package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    private UserDto userDtoToSave;
    private User returnedUser;
    private User expectedUser;
    private User userBeforeUpdate;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        userDtoToSave = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build();
        returnedUser = User.builder()
                .name("returnedUserName")
                .email("email@mail.com")
                .build();
        expectedUser = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build();
        userBeforeUpdate = User.builder()
                .id(userId)
                .name("name")
                .email("email@mail.com")
                .build();
    }

    @Test
    void createUser() {
        when(userRepository.save(Mockito.any()))
                .thenReturn(returnedUser);
        UserDto userDtoActual = userServiceImpl.createUser(userDtoToSave);
        verify(userRepository, Mockito.times(1))
                .save(UserMapper.mapToNewUser(userDtoToSave));
        assertEquals("returnedUserName", userDtoActual.getName());
    }

    @Test
    void findByIdTest() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));
        User actualUser = UserMapper.mapToNewUser(userServiceImpl.findById(userId));
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void findByIdIfNotFoundThrowException() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> userServiceImpl.findById(userId));
        assertEquals(exception.getMessage(),"Пользователь не найден");
    }

    @Test
    void updateUser() {
        UserDto userUpdate = UserDto.builder()
                .id(userId)
                .name("name2")
                .email("email@mail.com2")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(userBeforeUpdate));
        userServiceImpl.updateUser(userUpdate, userId);
        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertEquals(userId, capturedUser.getId());
        assertEquals("name2", capturedUser.getName());
        assertEquals("email@mail.com2", capturedUser.getEmail());
    }

    @Test
    void updateWhenUserHaveEmptyFields() {
        UserDto userUpdate = UserDto.builder()
                .id(userId)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(userBeforeUpdate));
        userServiceImpl.updateUser(userUpdate, userId);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertEquals(userId, capturedUser.getId());
        assertEquals("name", capturedUser.getName());
        assertEquals("email@mail.com", capturedUser.getEmail());
    }

    @Test
    void findAllReturnsEmptyList() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());
        List<UserDto> all = userServiceImpl.findAll();
        verify(userRepository, Mockito.times(1))
                .findAll();
        assertTrue(all.isEmpty());
    }

    @Test
    void deleteUser() {
        userServiceImpl.deleteUser(userId);
        verify(userRepository, Mockito.times(1))
                .deleteById(userId);
    }
}