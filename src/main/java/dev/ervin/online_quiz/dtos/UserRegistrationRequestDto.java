package dev.ervin.online_quiz.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestDto {
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters long")
    @NotBlank(message = "Username is not provided, blank or empty")
    private String username;

    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters long")
    @NotBlank(message = "Password is not provided, blank or empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter and one digit")
    private String password;

    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters long")
    @NotBlank(message = "Password is not provided, blank or empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter and one digit")
    private String confirmPassword;

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters long")
    @NotBlank(message = "Name is not provided, blank or empty")
    private String name;

    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters long")
    @NotBlank(message = "Surname is not provided, blank or empty")
    private String surname;

    @Max(value = 1, message = "Role must be 1 character long")
    @NotNull(message = "Role is not provided, blank or empty")
    @PositiveOrZero
    private Short role;

    @Size(min = 5, max = 50, message = "Email must be between 5 and 50 characters long")
    @NotBlank(message = "Email is not provided, blank or empty")
    @Email(message = "Email is not valid")
    private String email;

    // Custom validation for password and confirmPassword matching
    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
