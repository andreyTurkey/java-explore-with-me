package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;
import ru.practicum.model.State;
import ru.practicum.model.Subscription;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Subscription findByInitiatorIdAndSubscriberId(Long initiatorId, Long subscriberId);

    @Query("select e from Subscription s join Event e on s.initiator.id = e.initiator.id where e.state like :state and s.initiator.id in (" +
            " select s.initiator.id from Subscription s where s.subscriber.id =:subscriberId)")
    List<Event> findBySubscriberId(@Param("subscriberId") Long subscriberId,
                                   @Param("state") State state,
                                   Pageable page);
}
