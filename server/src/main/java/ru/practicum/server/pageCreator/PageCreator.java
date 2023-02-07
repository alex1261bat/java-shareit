package ru.practicum.server.pageCreator;

import org.springframework.data.domain.PageRequest;


public class PageCreator {

    public static PageRequest createPage(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }
}
