package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId, Pageable pageRequest);

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) and i.available = true")
    List<Item> findAvailableItems(String text, Pageable pageRequest);

    List<Item> findAllByRequestId(Long id);

    List<Item> findAllByRequestIsIn(List<ItemRequest> itemRequestList);

    default Item getItemById(Long itemId) {
        return findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не существует"));
    }
}
