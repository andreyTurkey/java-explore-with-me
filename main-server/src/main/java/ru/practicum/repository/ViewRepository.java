package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.View;

public interface ViewRepository extends JpaRepository<View, Long> {

    Integer countViewByEventIdAndUserIp(Long eventId, String userIp);
}
