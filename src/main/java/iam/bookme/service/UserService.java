package iam.bookme.service;

import iam.bookme.client.UserClient;
import iam.bookme.dto.BookingRequestDto;
import iam.bookme.dto.UserDto;
import iam.bookme.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;

    protected UserDto getUser(BookingRequestDto bookingRequestDto) {
        UserDto userDto = userClient.getUserFromUserServiceByEmail(bookingRequestDto.getUserEmail());
        if (userDto == null) {
            // if user not found, create new user
            log.info("User with email {} not found", bookingRequestDto.getUserEmail());

            userDto = createNewUser(bookingRequestDto);
        }
        return userDto;
    }

    private UserDto createNewUser(BookingRequestDto bookingRequestDto) {
        log.info("Creating new user with email {}", bookingRequestDto.getUserEmail());

        //TODO: replace placeholders John Doe later
        UserRequestDto userRequestDto = new UserRequestDto("John",
                "Doe",
                bookingRequestDto.getUserEmail(),
                "0277599576");
        return userClient.createUserInUserService(userRequestDto);
    }
}
