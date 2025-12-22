package com.spring.login.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.login.exception.IncorrectEmailIdException;
import com.spring.login.exception.IncorrectPasswordException;
import com.spring.login.model.User;
import com.spring.login.repository.UserRepository;
import com.spring.login.request.PasswordChangeRequest;

@Service
public class AuthenticationService {
	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	public AuthenticationService(UserRepository repo,
			PasswordEncoder encoder) {
    	this.userRepo = repo;
    	this.passwordEncoder = encoder;
    }
	
	public String changePassword(PasswordChangeRequest 
			passwordChangeRequest) {
		User user = userRepo.findByEmail(passwordChangeRequest.getEmail())
	            .orElseThrow(() -> 
	            new IncorrectEmailIdException(
	            		"Incorrect email Id is given"));
		if(!passwordEncoder.matches(
				passwordChangeRequest.getExistingPassword(),
	            user.getPassword())) {
	        throw new IncorrectPasswordException(
	        		"Existing password is incorrectly given");
	    }
		user.setPassword(passwordEncoder.encode(
				passwordChangeRequest.getNewPassword()));
	    userRepo.save(user);

	    return "Password changed successfully";
	}
}
