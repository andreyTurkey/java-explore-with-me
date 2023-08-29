package ru.practicum.model;

import ru.practicum.exception.NotAvailableException;

public enum State {

    PENDING,
    PUBLISHED,
    CANCELED,
    SEND_TO_REVIEW,
    CONFIRMED,
    CANCEL_REVIEW,
    REJECT_EVENT,
    REJECTED,
    PUBLISH_EVENT;

    public static State getState(String state) {
        switch (state) {
            case ("REJECT_EVENT"):
                return State.REJECT_EVENT;
            case ("PENDING"):
                return State.PENDING;
            case ("PUBLISHED"):
                return State.PUBLISHED;
            case ("CONFIRMED"):
                return State.CONFIRMED;
            case ("CANCELED"):
                return State.CANCELED;
            case ("SEND_TO_REVIEW"):
                return State.SEND_TO_REVIEW;
            case ("CANCEL_REVIEW"):
                return State.CANCEL_REVIEW;
            case ("REJECTED"):
                return State.REJECTED;
            case ("PUBLISH_EVENT"):
                return State.PUBLISH_EVENT;
            default:
                throw new NotAvailableException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
