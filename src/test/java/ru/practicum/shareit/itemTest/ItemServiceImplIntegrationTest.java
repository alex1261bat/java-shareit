package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;

    @Test
    void getByIdTest() {
        User user = userRepository.save(new User(1L, "name1", "email1@mail"));
        ItemRequestDto requestDto = makeItemDto(user.getId(), LocalDateTime.now());
        itemRequestService.saveNewItemRequest(user.getId(), requestDto);

        TypedQuery<ItemRequest> query = entityManager.createQuery("Select r from ItemRequest r " +
                "where r.description = " + ":description and r.requestor.id = :userId", ItemRequest.class);
        ItemRequest request = query.setParameter("description", requestDto.getDescription())
                .setParameter("userId", user.getId()).getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(request.getRequestor(), equalTo(user));
    }

    private ItemRequestDto makeItemDto(Long requestorId, LocalDateTime created) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("description");
        dto.setRequestorId(requestorId);
        dto.setCreated(created);
        return dto;
    }
}
