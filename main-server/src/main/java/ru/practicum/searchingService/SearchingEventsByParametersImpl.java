package ru.practicum.searchingService;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.model.Event;
import ru.practicum.model.State;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Primary
public class SearchingEventsByParametersImpl implements SearchingEventsByParameters {

    @PersistenceContext
    private EntityManager em;

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Event> getAdminEventsByParameters(Collection<Long> users,
                                                  Collection<State> states,
                                                  Collection<Integer> categories,
                                                  String rangeStart,
                                                  String rangeEnd,
                                                  Integer from,
                                                  Integer size) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> event = criteriaQuery.from(Event.class);
        final List<Predicate> predicates = new ArrayList<>();

        LocalDateTime start;

        LocalDateTime end;

        if (users != null) {
            predicates.add(event.get("initiator").in(users));
        }
        if (states != null) {
            predicates.add(event.get("state").in(states));
        }
        if (categories != null) {
            predicates.add(event.get("category").in(categories));
        }
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
            predicates.add(criteriaBuilder.greaterThan(event.get("eventDate"), start));
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
            predicates.add(criteriaBuilder.lessThan(event.get("eventDate"), end));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        List<Event> events = em.createQuery(criteriaQuery)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
        return events;
    }

    @Override
    public List<Event> getAllEventsByParameters(String text,
                                                Collection<Integer> categories,
                                                Boolean paid,
                                                String rangeStart,
                                                String rangeEnd,
                                                Boolean onlyAvailable,
                                                String sort,
                                                Integer from,
                                                Integer size) {

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> event = criteriaQuery.from(Event.class);
        final List<Predicate> predicates = new ArrayList<>();

        LocalDateTime start;
        LocalDateTime end;

        if (text != null) {
            predicates.add(criteriaBuilder.like((event.get("annotation")), "%" + text + "%"));
        }
        if (categories != null) {
            predicates.add(event.get("category").in(categories));
        }
        if (paid != null) {
            predicates.add(criteriaBuilder.equal(event.get("paid"), paid));
        }
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
            predicates.add(criteriaBuilder.greaterThan(event.get("eventDate"), start));
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
            predicates.add(criteriaBuilder.lessThan(event.get("eventDate"), end));
        }
        if (onlyAvailable) {
            Expression<Integer> confirmedRequests = event.get("confirmed_requests");
            Expression<Integer> limit = event.get("participantLimit");
            predicates.add(criteriaBuilder.notEqual(criteriaBuilder.diff(confirmedRequests, limit), 0));
        }

        predicates.add(criteriaBuilder.equal(event.get("state"), State.PUBLISHED));

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        List<Event> events = em.createQuery(criteriaQuery)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
        return events;
    }
}
