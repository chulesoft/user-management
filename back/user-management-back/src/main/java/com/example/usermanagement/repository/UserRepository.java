package com.example.usermanagement.repository;

import com.example.usermanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByIdAndDeletedFalse(Long id);

    Page<User> findAllByDeletedFalse(Pageable pageable);

    @Query("""
           select u from User u
           where u.deleted = false and (
                 lower(u.firstName) like lower(concat('%', :q, '%'))
              or lower(u.lastName) like lower(concat('%', :q, '%'))
              or lower(u.email) like lower(concat('%', :q, '%'))
           )
           """)
    Page<User> searchActive(@Param("q") String q, Pageable pageable);
}
