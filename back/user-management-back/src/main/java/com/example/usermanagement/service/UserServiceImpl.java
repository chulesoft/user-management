package com.example.usermanagement.service;

import com.example.usermanagement.dto.PageResponse;
import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.exception.DuplicateEmailException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public Mono<PageResponse<UserResponse>> findAll(String search, Pageable pageable) {
        log.debug("event=user.list page={} size={} sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return Mono.fromCallable(() -> {
                    Page<User> page;
                    if (search == null || search.isBlank()) {
                        page = userRepository.findAllByDeletedFalse(pageable);
                    } else {
                        page = userRepository.searchActive(search.trim(), pageable);
                    }
                    return mapper.toPageResponse(page, pageable.getPageNumber(), pageable.getPageSize());
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UserResponse> findById(Long id) {
        log.debug("event=user.get id={}", id);
        return Mono.fromCallable(() -> userRepository.findByIdAndDeletedFalse(id)
                        .map(mapper::toResponse)
                        .orElseThrow(() -> new UserNotFoundException(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UserResponse> create(UserRequest request, String actor) {
        log.debug("event=user.create.start email={} actor={}", request.email(), actor);
        return Mono.fromCallable(() -> {
                    if (userRepository.existsByEmailIgnoreCase(request.email())) {
                        throw new DuplicateEmailException(request.email());
                    }
                    User user = mapper.toNewEntity(request);
                    user.setCreatedBy(actor);
                    user.setUpdatedBy(actor);
                    User saved = userRepository.save(user);
                    log.debug("event=user.create.done id={} actor={}", saved.getId(), actor);
                    return mapper.toResponse(saved);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UserResponse> update(Long id, UserRequest request, String actor) {
        log.debug("event=user.update.start id={} actor={}", id, actor);
        return Mono.fromCallable(() -> {
                    User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new UserNotFoundException(id));
                    if (userRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
                        throw new DuplicateEmailException(request.email());
                    }
                    mapper.applyUpdates(user, request);
                    user.setUpdatedBy(actor);
                    User saved = userRepository.save(user);
                    log.debug("event=user.update.done id={} actor={}", saved.getId(), actor);
                    return mapper.toResponse(saved);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(Long id, String actor) {
        log.debug("event=user.delete.start id={} actor={}", id, actor);
        return Mono.fromRunnable(() -> {
                    User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new UserNotFoundException(id));
                    user.setDeleted(true);
                    user.setActive(false);
                    user.setDeletedAt(LocalDateTime.now());
                    user.setDeletedBy(actor);
                    user.setUpdatedBy(actor);
                    userRepository.save(user);
                    log.debug("event=user.delete.done id={} actor={}", id, actor);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
