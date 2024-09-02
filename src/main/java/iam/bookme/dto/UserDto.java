package iam.bookme.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
public class UserDto{

    private String firstName;
    private  String lastName;
    private String email;
    private String phoneNumber;
    private  Long id;
    private   OffsetDateTime createdOn;
    private   OffsetDateTime updatedOn;

}
