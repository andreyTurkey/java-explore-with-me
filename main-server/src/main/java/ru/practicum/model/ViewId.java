package ru.practicum.model;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class ViewId implements Serializable {

    Long eventId;

    String userIp;
}
