package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;
import ru.practicum.model.State;
import ru.practicum.searchingService.SearchingEventsByParameters;

import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, SearchingEventsByParameters {

    List<Event> findAllByIdIn(Collection<Long> eventIds);

    Event getEventByAnnotation(String annotation);

    Event findAllByInitiatorIdAndId(Long userId, Long eventId);

    @Query("SELECT e from Event e WHERE e.initiator.id=:userId")
    Page<Event> getEventByInitiatorIdAndPageable(@Param("userId") Long userId, Pageable page);

    @Query("SELECT e from Event e WHERE e.initiator.id=:userId")
    List<Event> getEventByInitiatorId(@Param("userId") Long userId);

    Boolean existsByCategoryId(Integer id);

    Page<Event> findByInitiatorIdAndState(Long userId, State state, Pageable page);
}
