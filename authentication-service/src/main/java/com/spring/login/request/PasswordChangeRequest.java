package com.spring.login.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
	@NotBlank
    @Size(max = 50)
    @Email
    private String email;
	@NotBlank
	@Size(min = 6, max = 40)
	private String existingPassword;
	@NotBlank
	@Size(min = 6, max = 40)
	private String newPassword;
}
