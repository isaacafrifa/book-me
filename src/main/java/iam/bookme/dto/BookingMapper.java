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

    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "comments", target = "comments")
    Booking toEntity(BookingRequestDto bookingRequestDto);

    @Mapping(source = "bookingId", target = "bookingId")
    @Mapping(source = "userReferenceId", target = "userId")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updatedDate", target = "updatedDate")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "status", target = "bookingStatus")
    @Mapping(source = "comments", target = "comments")
    BookingDto toDto(Booking booking);
}