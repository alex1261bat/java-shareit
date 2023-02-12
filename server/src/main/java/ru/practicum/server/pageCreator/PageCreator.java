package ru.practicum.server.pageCreator;

import org.springframework.data.domain.PageRequest;


public class PageCreator {

    public static PageRequest createPage(Integer from, Integer size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }
}
