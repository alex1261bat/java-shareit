package ru.practicum.server.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingRepository;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.exceptions.ValidationException;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.request.ItemRequestRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto saveNewItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.getUserById(userId);
        ItemRequest itemRequest = null;

        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.getItemRequestById(itemDto.getRequestId());
        }

        Item item = ItemMapper.toItem(itemDto, owner, itemRequest);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userRepository.getUserById(userId);
        Item item = itemRepository.getItemById(itemId);

        if (!(item.getOwner().getId().equals(userId))) {
            throw  new NotFoundException("Вещь с id=" + itemId + " не принадлежит пользователю с id=" + userId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(item);

        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public ItemWithBookingDatesDto getById(Long userId, Long itemId) {
        Item item = itemRepository.getItemById(itemId);

        return convertToItemWithBookingDatesDto(item, userId);
    }

    @Override
    public List<ItemWithBookingDatesDto> getUserItems(Long userId, Pageable pageRequest) {
        return itemRepository.findAllByOwnerId(userId, pageRequest).stream()
                .map(item -> convertToItemWithBookingDatesDto(item, userId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findAvailableItems(String text, Pageable pageRequest) {

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findAvailableItems(text, pageRequest).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        User author = userRepository.getUserById(userId);
        Item item = itemRepository.getItemById(itemId);
        List<Booking> bookingList = bookingRepository.findAllUserBookings(commentRequestDto.getAuthorId(),
                commentRequestDto.getItemId(), commentRequestDto.getCreated());

        if (bookingList.isEmpty()) {
            throw new ValidationException("Пользователь с id=" + userId + " не брал вещи в аренду");
        }

        Comment comment = commentRepository.save(CommentMapper.toComment(commentRequestDto, item, author));

        return CommentMapper.toCommentResponseDto(comment);
    }

    private ItemWithBookingDatesDto convertToItemWithBookingDatesDto(Item item, long userId) {
        List<Booking> bookingList = bookingRepository.findAllByItemId(item.getId());
        List<CommentResponseDto> commentList = commentRepository.findAllByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentResponseDto).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        Booking current = null;
        Booking next = null;

        if (item.getOwner().getId() == userId) {
            for (Booking booking : bookingList) {
                if (booking.getStart().isBefore(now)) {
                    if ((current == null) || (current.getStart().isBefore(booking.getStart())))
                        current = booking;
                    continue;
                }

                if (booking.getStart().isAfter(now)) {
                    if ((next == null) || (next.getStart().isAfter(booking.getStart()))) {
                        next = booking;
                    }
                }
            }
        }
        return ItemMapper.toItemDtoWithBookingDates(item, current, next, commentList);
    }
}
