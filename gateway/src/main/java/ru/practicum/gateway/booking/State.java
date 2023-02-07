package ru.practicum.gateway.booking;

public enum State {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static State getState(String stringState) {
        State state = null;

        for (State stateElem : values()) {
            if (stateElem.name().equalsIgnoreCase(stringState)) {
                state = stateElem;
            } else {
                throw new IllegalArgumentException("Unknown state: " + stringState);
            }
        }

        return state;
    }
}
