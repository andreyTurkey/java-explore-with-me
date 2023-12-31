package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByIdIn(Collection<Long> ids, Pageable page);

    List<User> findAllByIdIn(Collection<Long> ids);

    List<User> findAllBy(Pageable page);

    List<User> findAllBy();

    User findByEmail(String email);

    Boolean existsByName(String name);
}

