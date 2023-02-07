package ru.practicum.gateway.pageValidator;

import ru.practicum.gateway.exceptions.ValidationException;

public class PageValidator {

    public static void validatePage(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Параметры page нарушены: from=" + from + " size=" + size);
        }
    }
}
