package ru.practicum.shareit.pageValidator;

import org.springframework.data.domain.PageRequest;

import javax.validation.ValidationException;

public class PageValidator {

    public static PageRequest validatePage(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Параметры page нарушены: from=" + from + " size=" + size);
        } else {
            int page = from / size;
            return PageRequest.of(page, size);
        }
    }
}
