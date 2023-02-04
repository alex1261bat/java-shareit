package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long userId);

    @Query("select r from ItemRequest r where r.requestor.id <> ?1 order by r.created desc")
    List<ItemRequest> findAllByOtherUser(Long userId, Pageable pageable);

    default ItemRequest getItemRequestById(Long itemRequestId) {
        return findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + itemRequestId + " не существует"));
    }
}
