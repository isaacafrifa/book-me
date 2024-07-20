package iam.bookme.dto;

import iam.bookme.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    Booking toEntity(BookingDto bookingDto);

    BookingDto toDto(Booking booking);
}