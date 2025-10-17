package ru.practicum.shareit.booking.dto;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

@UtilityClass
public final class BookingMapper {
    public BookingDto toDto(@NonNull Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toDto(booking.getItem()),
                UserMapper.toDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public BookingRequestDto bookingRequestToDto(@NonNull Booking booking) {
        return new BookingRequestDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}
