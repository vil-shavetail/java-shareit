package ru.practicum.shareit.booking.dto;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
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

    public Booking toEntity(@NonNull BookingDto dto, @NonNull Item item, @NonNull User booker) {
        return new Booking(
                dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                item,
                booker,
                dto.getStatus()
        );
    }
}
