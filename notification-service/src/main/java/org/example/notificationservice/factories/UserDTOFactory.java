package org.example.notificationservice.factories;

import com.github.javafaker.Faker;
import org.example.notificationservice.models.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDTOFactory {
    private final Faker faker = new Faker();

    public UserDTO createUserDTO(String userId) {
        return new UserDTO(
                userId,
                faker.name().fullName(), // User's name
                faker.internet().emailAddress() // User's email
        );
    }

}
