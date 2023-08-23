package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

//@Embeddable
@Entity
@Table(name = "events", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Event {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation", nullable = false)
    @Size(min = 20, max = 2000, message = "Проверьте длину строки")
    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "confirmed_requests")
    Integer confirmedRequests;

    @Size(min = 20, max = 7000, message = "Проверьте длину строки")
    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "created_on")
    LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_initiator")
    User initiator;

    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "location_id")
    Location location;

    @Column(name = "paid", nullable = false)
    Boolean paid;

    @Column(name = "participant_limit")
    @Min(value = 0)
    Integer participantLimit;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Проверьте длину строки")
    @Column(name = "title")
    String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    State state;

    @Column(name = "views", nullable = false)
    Integer views;

    @Column(name = "participation_available")
    Boolean participationAvailable;
}
