package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.AccessDenyException;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookingServiceTest {

    @Autowired
    BookingService bookingService;

    BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void shouldCreateBooking() throws Exception {
        BookingDto bookingDto = bookingService.createBooking(bookingCreateDto, 2L);
        assertNotNull(bookingDto);
        assertEquals(bookingCreateDto.getItemId(), bookingDto.getItem().getId());
        assertEquals(bookingCreateDto.getStart(), bookingDto.getStart());
        assertEquals(bookingCreateDto.getEnd(), bookingDto.getEnd());
        assertEquals(2L, bookingDto.getBooker().getId());
    }

    @Test
    void shouldNotCreateBookingWithWrongUserId() throws Exception {
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingCreateDto, 12L));
    }

    @Test
    void shouldNotCreateBookingWithWrongItemId() throws Exception {
        bookingCreateDto.setItemId(100L);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingCreateDto, 2L));
    }

    @Test
    void shouldNotCreateBookingWithWrongStart() throws Exception {
        bookingCreateDto.setEnd(bookingCreateDto.getStart());
        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(bookingCreateDto, 2L));
    }

    @Test
    void shouldNotCreateBookingForUnavailableItem() throws Exception {
        bookingCreateDto.setItemId(2L);
        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(bookingCreateDto, 2L));
    }

    @Test
    void approveBooking() {
        BookingDto dto = bookingService.approveBooking(5L, 3L, true);
        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals(2L, dto.getBooker().getId());
        assertEquals("Гитара", dto.getItem().getName());
        assertEquals(Status.APPROVED, dto.getStatus());
    }

    @Test
    void rejectBooking() {
        BookingDto dto = bookingService.approveBooking(5L, 3L, false);
        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals(2L, dto.getBooker().getId());
        assertEquals("Гитара", dto.getItem().getName());
        assertEquals(Status.REJECTED, dto.getStatus());
    }

    @Test
    void shouldNotApproveNonexistentBooking() throws Exception {
        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(15L, 3L, false));
    }

    @Test
    void shouldNotApproveWithNotOwnerId() throws Exception {
        assertThrows(AccessDenyException.class, () -> bookingService.approveBooking(5L, 1L, false));
    }

    @Test
    void getBooking() {
        BookingDto dto = bookingService.getBooking(5L);
        //('2023-06-08 14:00:00', '2023-06-10 16:00:00', 2, 9, 0),  -- Гитара (id=9)
        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals(2L, dto.getBooker().getId());
        assertEquals(9L, dto.getItem().getId());
        assertEquals(LocalDateTime.of(2023, 6, 8, 14, 0), dto.getStart());
        assertEquals(LocalDateTime.of(2023, 6, 10, 16, 0), dto.getEnd());
        assertEquals(Status.WAITING, dto.getStatus());
    }

    @Test
    void shouldGetAllBookingByBookerIdAndStatus() {
        List<BookingDto> bookings = bookingService.getBookingByBookerIdAndStatus(1L, BookingState.ALL, 0, 10);
        assertEquals(3, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
        assertEquals(2L, bookings.get(1).getId());
        assertEquals(3L, bookings.get(2).getId());
    }

    @Test
    void shouldGetRejectedBookingByBookerIdAndStatus() {
        List<BookingDto> bookings = bookingService.getBookingByBookerIdAndStatus(1L, BookingState.REJECTED, 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(2L, bookings.get(0).getId());
    }

    @Test
    void shouldGetWaitingBookingByBookerIdAndStatus() {
        List<BookingDto> bookings = bookingService.getBookingByBookerIdAndStatus(2L, BookingState.WAITING, 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(5L, bookings.get(0).getId());
    }

    @Test
    void shouldGetWaitingEmptyListBookingByBookerIdAndStatus() {
        List<BookingDto> bookings = bookingService.getBookingByBookerIdAndStatus(1L, BookingState.WAITING, 0, 10);
        assertEquals(0, bookings.size());
    }

    @Test
    void shouldGetPastBookingByBookerIdAndStatus() {
        List<BookingDto> bookings = bookingService.getBookingByBookerIdAndStatus(1L, BookingState.PAST, 0, 10);
        assertEquals(3, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
        assertEquals(2L, bookings.get(1).getId());
        assertEquals(3L, bookings.get(2).getId());
    }

    @Test
    void shouldGetCurrentBookingByBookerIdAndStatus() {
        bookingCreateDto.setStart(LocalDateTime.now().minusDays(1));
        bookingService.createBooking(bookingCreateDto, 2L);
        List<BookingDto> bookings = bookingService.getBookingByBookerIdAndStatus(2L, BookingState.CURRENT, 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(7L, bookings.get(0).getId());
    }

    @Test
    void shouldGetFutureBookingByBookerIdAndStatus() {
        bookingService.createBooking(bookingCreateDto, 2L);
        List<BookingDto> bookings = bookingService.getBookingByBookerIdAndStatus(2L, BookingState.FUTURE, 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(7L, bookings.get(0).getId());
    }

    @Test
    void shouldNotGetAllBookingByNonExistedBookerIdAndStatus() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingByBookerIdAndStatus(33L, BookingState.ALL, 0, 10));
    }

    @Test
    void shouldNotGetAllBookingByNonExistedOwnerIdAndStatus() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingByItemOwnerIdAndStatus(33L, BookingState.ALL, 0, 10));
    }

    @Test
    void shouldGetAllBookingByItemOwnerIdAndStatus() {
        List<BookingDto> bookings = bookingService.getBookingByItemOwnerIdAndStatus(1L, BookingState.ALL, 0, 10);
        assertEquals(2, bookings.size());
        assertEquals(4L, bookings.get(0).getId());
        assertEquals(6L, bookings.get(1).getId());
    }

    @Test
    void shouldGetPastBookingByItemOwnerIdAndStatus() {
        List<BookingDto> bookings = bookingService.getBookingByItemOwnerIdAndStatus(1L, BookingState.PAST, 0, 10);
        assertEquals(2, bookings.size());
        assertEquals(4L, bookings.get(0).getId());
        assertEquals(6L, bookings.get(1).getId());
    }

    @Test
    void shouldGetRejectedBookingByItemOwnerIdAndStatus() {
        List<BookingDto> bookings = bookingService.getBookingByItemOwnerIdAndStatus(3L, BookingState.REJECTED, 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(2L, bookings.get(0).getId());
    }

    @Test
    void shouldGetWaitingBookingByItemOwnerIdAndStatus() {
        List<BookingDto> bookings = bookingService.getBookingByItemOwnerIdAndStatus(3L, BookingState.WAITING, 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(5L, bookings.get(0).getId());
    }

    @Test
    void shouldGetCurrentBookingByItemOwnerIdAndStatus() {
        bookingCreateDto.setStart(LocalDateTime.now().minusDays(1));
        bookingService.createBooking(bookingCreateDto, 2L);
        List<BookingDto> bookings = bookingService.getBookingByItemOwnerIdAndStatus(1L, BookingState.CURRENT, 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(7L, bookings.get(0).getId());
    }

    @Test
    void shouldGetFutureBookingByItemOwnerIdAndStatus() {
        bookingService.createBooking(bookingCreateDto, 2L);
        List<BookingDto> bookings = bookingService.getBookingByItemOwnerIdAndStatus(1L, BookingState.FUTURE, 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(7L, bookings.get(0).getId());
    }
}