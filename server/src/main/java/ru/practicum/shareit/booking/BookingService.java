package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.AccessDenyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;

    @Transactional
    public BookingDto createBooking(BookingCreateDto createDto, Long userId) {
        Booking booking = bookingMapper.fromDto(createDto);
        // вот эти вот 2 проверки тут только ради того, чтобы выкидывалась 404 ошибка, потому что в обычном случае
        // валидация будет выкидывать 400, если юзер или пользователь не будут найдены, но тогда тесты не проходят
        boolean existsUser = userRepository.existsById(userId);
        if (!existsUser) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (booking.getItem() == null) {
            throw new NotFoundException("Некорректные данные");
        }
        //
        if (booking.getEnd().equals(booking.getStart())) {
            throw new IllegalStateException("Срок аренды вещи не может быть нулевым");
        }
        boolean available = booking.getItem().getAvailable();
        if (!available) {
            throw new IllegalStateException("Вещь не доступна для бронирования");
        }
        booking.setBooker(findUserById(userId));
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto approveBooking(Long id, Long userId, boolean approved) {
        Booking booking = findBooking(id);
        checkBooking(booking, userId);
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingDto getBooking(Long id) {
        return bookingMapper.toDto(findBooking(id));
    }

    public List<BookingDto> getBookingByBookerIdAndStatus(Long bookerId, BookingState status, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        checkUser(bookerId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        bookings = switch (status) {
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartAsc(bookerId, Status.REJECTED, pageable);
            case ALL -> bookingRepository.findAllByBookerId(bookerId, pageable);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByEndAsc(bookerId, now, pageable);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartAsc(bookerId, now, pageable);
            case CURRENT -> bookingRepository.findCurrentBookingsByBooker(bookerId, now, pageable);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartAsc(bookerId, Status.WAITING, pageable);
        };
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    public List<BookingDto> getBookingByItemOwnerIdAndStatus(Long itemOwnerId, BookingState status, Integer from, Integer size) {
        checkUser(itemOwnerId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        bookings = switch (status) {
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartAsc(itemOwnerId, Status.REJECTED, pageable);
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartAsc(itemOwnerId, pageable);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByEndAsc(itemOwnerId, now, pageable);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartAsc(itemOwnerId, now, pageable);
            case CURRENT -> bookingRepository.findCurrentBookingsByItemOwnerId(itemOwnerId, now, pageable);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartAsc(itemOwnerId, Status.WAITING, pageable);
        };
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    private Booking findBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id '%s' не найдено", id)));
    }

    private void checkBooking(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDenyException("Подтвердить бронирование может только владелец вещи");
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private void checkUser(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
