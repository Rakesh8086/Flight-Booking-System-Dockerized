package com.spring.login.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
	@NotBlank
	@Size(min = 1, max = 20)
	private String username;
	@NotBlank
    @Size(max = 50)
    @Email
    private String email;
	private Set<String> role;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
    message = "Password must contain at least 1 uppercase, 1 lowercase, 1 number, 1 special character and be at least 6 characters long")
    private String password;
}
