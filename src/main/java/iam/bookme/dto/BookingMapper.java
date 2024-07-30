package iam.bookme.dto;

import iam.bookme.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    @Mapping(source = "userEmail", target = "userEmail")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "comments", target = "comments")
    Booking toEntity(BookingRequestDto bookingRequestDto);

    BookingDto toDto(Booking booking);
}