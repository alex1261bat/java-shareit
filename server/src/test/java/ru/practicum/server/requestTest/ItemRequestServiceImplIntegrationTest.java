package ru.practicum.server.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemDto;
import ru.practicum.server.item.ItemService;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final ItemService itemService;
    private final UserRepository userRepository;

    @Test
    void saveNewItemRequestTest() {
        User user = userRepository.save(new User(1L, "name1", "email1@mail"));
        ItemDto itemDto = makeItemDto();
        itemService.saveNewItem(user.getId(), itemDto);

        TypedQuery<Item> query = entityManager
                .createQuery("Select i from Item i where i.name = :itemName and i.owner= :owner", Item.class);
        Item item = query.setParameter("itemName", itemDto.getName()).setParameter("owner", user)
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getOwner(), equalTo(user));
    }

    private ItemDto makeItemDto() {
        ItemDto dto = new ItemDto();
        dto.setName("itemName");
        dto.setDescription("text");
        dto.setAvailable(true);

        return dto;
    }
}
