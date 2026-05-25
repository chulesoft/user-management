package com.example.usermanagement.service;

import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.exception.DuplicateEmailException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock UserRepository userRepository;

    UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(userRepository, new UserMapper());
    }

    @Test
    void findAllReturnsPagedUsers() {
        when(userRepository.findAllByDeletedFalse(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(user())));

        StepVerifier.create(service.findAll(null, PageRequest.of(0, 10)))
                .expectNextMatches(page -> page.totalElements() == 1 && page.content().get(0).email().equals("omar@example.com"))
                .verifyComplete();
    }

    @Test
    void findAllSearchUsesRepositorySearch() {
        when(userRepository.searchActive(eq("omar"), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(user())));

        StepVerifier.create(service.findAll(" omar ", PageRequest.of(0, 10)))
                .expectNextMatches(page -> page.totalElements() == 1)
                .verifyComplete();

        verify(userRepository, never()).findAllByDeletedFalse(any());
    }

    @Test
    void findByIdReturnsUserWhenExists() {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user()));
        StepVerifier.create(service.findById(1L)).expectNextMatches(u -> u.id().equals(1L)).verifyComplete();
    }

    @Test
    void findByIdFailsWhenMissing() {
        when(userRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        StepVerifier.create(service.findById(99L)).expectError(UserNotFoundException.class).verify();
    }

    @Test
    void createSavesNormalizedUserAndAuditFields() {
        when(userRepository.existsByEmailIgnoreCase("Omar@Example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            u.setCreatedAt(LocalDateTime.now());
            u.setUpdatedAt(LocalDateTime.now());
            u.setVersion(0L);
            return u;
        });

        StepVerifier.create(service.create(new UserRequest(" Omar ", " Garcia ", "Omar@Example.com", " 8112345678 ", null), "admin"))
                .expectNextMatches(u -> u.email().equals("omar@example.com")
                        && u.phone().equals("8112345678")
                        && u.createdBy().equals("admin")
                        && u.updatedBy().equals("admin"))
                .verifyComplete();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("omar@example.com");
    }

    @Test
    void createFailsOnDuplicateEmail() {
        when(userRepository.existsByEmailIgnoreCase("omar@example.com")).thenReturn(true);
        StepVerifier.create(service.create(new UserRequest("Omar", "Garcia", "omar@example.com", null, true), "admin"))
                .expectError(DuplicateEmailException.class)
                .verify();
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteSoftDeletesUser() {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user()));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(service.delete(1L, "admin")).verifyComplete();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().isDeleted()).isTrue();
        assertThat(captor.getValue().getDeletedBy()).isEqualTo("admin");
    }

    @Test
    void updateUpdatesUserAndAuditFields() {
        User existing = user();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("new@example.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRequest req = new UserRequest("New", "Name", "new@example.com", "1234567890", true);
        StepVerifier.create(service.update(1L, req, "admin"))
                .expectNextMatches(u -> u.email().equals("new@example.com") && u.updatedBy().equals("admin"))
                .verifyComplete();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("new@example.com");
        assertThat(captor.getValue().getUpdatedBy()).isEqualTo("admin");
    }

    @Test
    void updateFailsOnDuplicateEmail() {
        User existing = user();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("taken@example.com", 1L)).thenReturn(true);

        UserRequest req = new UserRequest("Omar", "Garcia", "taken@example.com", null, true);
        StepVerifier.create(service.update(1L, req, "admin"))
                .expectError(DuplicateEmailException.class)
                .verify();
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateFailsWhenUserNotFound() {
        when(userRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        UserRequest req = new UserRequest("Omar", "Garcia", "omar@example.com", null, true);
        StepVerifier.create(service.update(99L, req, "admin"))
                .expectError(UserNotFoundException.class)
                .verify();
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteFailsWhenUserNotFound() {
        when(userRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        StepVerifier.create(service.delete(99L, "admin"))
                .expectError(UserNotFoundException.class)
                .verify();
        verify(userRepository, never()).save(any());
    }

    private User user() {
        return User.builder()
                .id(1L)
                .firstName("Omar")
                .lastName("Garcia")
                .email("omar@example.com")
                .phone("8112345678")
                .active(true)
                .deleted(false)
                .createdBy("seed")
                .updatedBy("seed")
                .version(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
