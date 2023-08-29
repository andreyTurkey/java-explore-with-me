package ru.practicum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ViewStatsDto {

    String app;

    String uri;

    Long hits;
}

