package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "views", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ViewId.class)
public class View {

    @Id
    @Column(name = "event_id", nullable = false)
    Long eventId;

    @Id
    @Column(name = "user_ip", nullable = false)
    String userIp;
}
