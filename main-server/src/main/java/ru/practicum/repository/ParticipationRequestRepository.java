package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ParticipationRequest;

import java.util.Collection;
import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    ParticipationRequest findByEvent_IdAndRequester_Id(Long eventId,  Long userId);

    List<ParticipationRequest> findAllByIdIn(Collection<Long> ids);

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    List<ParticipationRequest> findAllByEventId(Long eventId);
}

