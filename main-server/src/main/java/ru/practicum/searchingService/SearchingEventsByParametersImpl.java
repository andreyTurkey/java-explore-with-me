package ru.practicum.searchingService;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.dto.AdminParametersDto;
import ru.practicum.dto.PublicParametersDto;
import ru.practicum.model.Event;
import ru.practicum.model.State;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Primary
public class SearchingEventsByParametersImpl implements SearchingEventsByParameters {

    @PersistenceContext
    private EntityManager em;

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Event> getAdminEventsByParameters(AdminParametersDto adminParametersDto) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> event = criteriaQuery.from(Event.class);
        final List<Predicate> predicates = new ArrayList<>();

        LocalDateTime start;

        LocalDateTime end;

        if (adminParametersDto.getUsers() != null) {
            predicates.add(event.get("initiator").in(adminParametersDto.getUsers()));
        }
        if (adminParametersDto.getStates() != null) {
            predicates.add(event.get("state").in(adminParametersDto.getStates()));
        }
        if (adminParametersDto.getCategories() != null) {
            predicates.add(event.get("category").in(adminParametersDto.getCategories()));
        }
        if (adminParametersDto.getRangeStart() != null) {
            start = LocalDateTime.parse(adminParametersDto.getRangeStart(), dateTimeFormatter);
            predicates.add(criteriaBuilder.greaterThan(event.get("eventDate"), start));
        }
        if (adminParametersDto.getRangeEnd() != null) {
            end = LocalDateTime.parse(adminParametersDto.getRangeEnd(), dateTimeFormatter);
            predicates.add(criteriaBuilder.lessThan(event.get("eventDate"), end));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        List<Event> events = em.createQuery(criteriaQuery)
                .setFirstResult(adminParametersDto.getFrom())
                .setMaxResults(adminParametersDto.getSize())
                .getResultList();
        return events;
    }

    @Override
    public List<Event> getAllEventsByParameters(PublicParametersDto publicParametersDto) {

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> event = criteriaQuery.from(Event.class);
        final List<Predicate> predicates = new ArrayList<>();

        LocalDateTime start;
        LocalDateTime end;

        if (publicParametersDto.getText() != null) {
            predicates.add(criteriaBuilder.like((event.get("annotation")), "%" + publicParametersDto.getText() + "%"));
        }
        if (publicParametersDto.getCategories() != null) {
            predicates.add(event.get("category").in(publicParametersDto.getCategories()));
        }
        if (publicParametersDto.getPaid() != null) {
            predicates.add(criteriaBuilder.equal(event.get("paid"), publicParametersDto.getPaid()));
        }
        if (publicParametersDto.getRangeStart() != null) {
            start = LocalDateTime.parse(publicParametersDto.getRangeStart(), dateTimeFormatter);
            predicates.add(criteriaBuilder.greaterThan(event.get("eventDate"), start));
        }
        if (publicParametersDto.getRangeEnd() != null) {
            end = LocalDateTime.parse(publicParametersDto.getRangeEnd(), dateTimeFormatter);
            predicates.add(criteriaBuilder.lessThan(event.get("eventDate"), end));
        }
        if (publicParametersDto.getOnlyAvailable()) {
            Expression<Integer> confirmedRequests = event.get("confirmed_requests");
            Expression<Integer> limit = event.get("participantLimit");
            predicates.add(criteriaBuilder.notEqual(criteriaBuilder.diff(confirmedRequests, limit), 0));
        }

        predicates.add(criteriaBuilder.equal(event.get("state"), State.PUBLISHED));

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        List<Event> events = em.createQuery(criteriaQuery)
                .setFirstResult(publicParametersDto.getFrom())
                .setMaxResults(publicParametersDto.getSize())
                .getResultList();
        return events;
    }
}
