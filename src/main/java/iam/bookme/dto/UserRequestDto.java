package iam.bookme.dto;

/*
This class is used to transfer user data to the user service. It will be used in creating a new user if it does not already exist.
 */
public record UserRequestDto(
        String firstName,
        String lastName,
        String email,
        String phoneNumber) {
}
