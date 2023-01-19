package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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

    @Override
    public ItemDto saveNewItem(Long userId, ItemDto itemDto) {
        User owner = getUserById(userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, owner)));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        getUserById(userId);
        Item item = getItemById(itemId);

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

        return ItemMapper.toItemDto(itemRepository.findById(itemId).get());
    }

    @Override
    public ItemWithBookingDatesDto getById(Long userId, Long itemId) {
        Item item = getItemById(itemId);
        return convertToItemWithBookingDatesDto(item, userId);
    }

    @Override
    public List<ItemWithBookingDatesDto> getUserItems(Long userId) {
        return itemRepository.findUserItems(userId).stream()
                .map(item -> convertToItemWithBookingDatesDto(item, userId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findAvailableItems(String text) {

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findAvailableItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        commentRequestDto.setCreated(LocalDateTime.now());
        commentRequestDto.setItemId(itemId);
        commentRequestDto.setAuthorId(userId);

        User author = getUserById(userId);
        Item item = getItemById(itemId);
        List<Booking> bookingList = bookingRepository.findAllUserBookings(commentRequestDto.getAuthorId(),
                commentRequestDto.getItemId(), commentRequestDto.getCreated());

        if (bookingList.isEmpty()) {
            throw new BookingValidationException("Пользователь с id=" + userId + " не брал вещи в аренду");
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

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не существует"));
    }
}
