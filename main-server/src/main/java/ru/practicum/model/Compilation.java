package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations", schema = "public")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class Compilation {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "compilations_events", joinColumns = @JoinColumn(name = "compilation_id"))
    @Column(name = "event_id")
    List<Long> events;

    @Column(name = "pinned")
    Boolean pinned;

    @Column(name = "title")
    String title;
}
