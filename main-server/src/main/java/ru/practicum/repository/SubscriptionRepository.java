package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;
import ru.practicum.model.State;
import ru.practicum.model.Subscription;
import ru.practicum.model.User;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Subscription findByInitiatorIdAndSubscriberId(Long initiatorId, Long subscriberId);

    @Query("select e from Subscription s join Event e on s.initiator.id = e.initiator.id " +
            " where e.state like :state and s.subscriber.id =:subscriberId")
    List<Event> findBySubscriberId(@Param("subscriberId") Long subscriberId,
                                   @Param("state") State state,
                                   Pageable page);

    void deleteAllByInitiatorId(Long initiatorId);

    void deleteBySubscriberIdAndInitiatorId(Long subscriberId, Long initiatorId);

    @Query("select u from Subscription s join User u on s.initiator.id = u.id where s.subscriber.id =:subscriberId")
    List<User> getStarters(@Param("subscriberId") Long subscriberId,
                           Pageable page);

    @Query("select u from Subscription s join User u on s.subscriber.id = u.id where s.initiator.id =:initiatorId")
    List<User> getSubscribers(@Param("initiatorId") Long initiatorId,
                           Pageable page);
}
