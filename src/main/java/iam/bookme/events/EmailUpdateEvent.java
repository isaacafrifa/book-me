package iam.bookme.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailUpdateEvent {
    private Long userId;
    private String oldEmail;
    private String newEmail;
    private String updatedAt;
}
