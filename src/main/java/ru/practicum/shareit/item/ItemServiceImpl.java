package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Override
    public ItemDto saveNewItem(Long userId, ItemDto itemDto) {
        User owner = UserMapper.toUser(userService.getById(userId));
        return ItemMapper.toItemDto(itemStorage.save(ItemMapper.toItem(itemDto, owner)));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userService.getById(userId);
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не существует"));

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

        itemStorage.save(item);

        return ItemMapper.toItemDto(itemStorage.findById(itemId).get());
    }

    @Override
    public ItemWithBookingDatesDto getById(Long userId, Long itemId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не существует"));

        return convertItem(item, userId);
    }

    @Override
    public List<ItemWithBookingDatesDto> getUserItems(Long userId) {
        return itemStorage.findUserItems(userId).stream()
                .map(item -> convertItem(item, userId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findAvailableItems(String text) {

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemStorage.findAvailableItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        commentRequestDto.setCreated(LocalDateTime.now());
        commentRequestDto.setItemId(itemId);
        commentRequestDto.setAuthorId(userId);

        User author = UserMapper.toUser(userService.getById(userId));
        Item item = itemStorage.findById(commentRequestDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Предмет с таким id не найден!"));
        List<Booking> bookingList = bookingStorage.findAllUserBookings(commentRequestDto.getAuthorId(),
                commentRequestDto.getItemId(), commentRequestDto.getCreated());

        if (bookingList.isEmpty()) {
            throw new BookingValidationException("Пользователь с id=" + userId + " не брал вещи в аренду");
        }

        Comment comment = commentStorage.save(CommentMapper.toComment(commentRequestDto, item, author));
        return CommentMapper.toCommentResponseDto(comment);
    }

    private ItemWithBookingDatesDto convertItem(Item item, long userId) {
        List<Booking> bookingList = bookingStorage.findAllByItemId(item.getId());
        List<CommentResponseDto> commentList = commentStorage.findAllByItemId(item.getId()).stream()
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
