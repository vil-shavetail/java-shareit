package ru.practicum.shareit.booking.dto;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@UtilityClass
public final class BookingMapper {
    public BookingDto toDto(@NonNull Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
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
