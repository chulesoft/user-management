package com.example.usermanagement.repository;

import com.example.usermanagement.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired EntityManager entityManager;

    @Test
    void verifiesEmailQueriesIgnoringCase() {
        User saved = userRepository.save(User.builder()
                .firstName("Omar")
                .lastName("Garcia")
                .email("Omar@Example.com")
                .active(true)
                .deleted(false)
                .createdBy("test")
                .updatedBy("test")
                .version(0L)
                .build());

        assertThat(userRepository.existsByEmailIgnoreCase("omar@example.com")).isTrue();
        assertThat(userRepository.existsByEmailIgnoreCaseAndIdNot("omar@example.com", saved.getId())).isFalse();
        assertThat(userRepository.existsByEmailIgnoreCaseAndIdNot("omar@example.com", 999L)).isTrue();
        assertThat(userRepository.findByEmailIgnoreCase("OMAR@EXAMPLE.COM")).isPresent();
    }

    @Test
    void optimisticLockPreventsLostUpdates() {
        User saved = userRepository.save(User.builder()
                .firstName("Ana")
                .lastName("Lopez")
                .email("ana@ex.com")
                .active(true)
                .deleted(false)
                .createdBy("test")
                .updatedBy("test")
                .version(0L)
                .build());

        entityManager.flush();
        entityManager.clear();

        User u1 = userRepository.findById(saved.getId()).orElseThrow();
        User u2 = userRepository.findById(saved.getId()).orElseThrow();

        u1.setLastName("Lopez-1");
        userRepository.saveAndFlush(u1);

        u2.setLastName("Lopez-2");
        assertThatThrownBy(() -> userRepository.saveAndFlush(u2))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }
}
