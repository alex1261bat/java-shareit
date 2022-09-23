package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class User {
    private final long id;
    @NotBlank
    private String name;
    @Email
    @UniqueElements
    private String email;
}
