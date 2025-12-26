package com.spring.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.login.request.PasswordChangeRequest;
import com.spring.login.service.AuthenticationService;

import jakarta.validation.Valid;

// @CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
// @CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
public class TestController {
  private final AuthenticationService authService;
  TestController(AuthenticationService service){
	this.authService = service;
  }
  
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public String userAccess() {
    return "User Content.";
  }

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR')")
  public String moderatorAccess() {
    return "Moderator Board.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }
  
  @PutMapping("/change/password")
  // @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<String> changePassword(@Valid @RequestBody 
		PasswordChangeRequest passwordChangeRequest) {
	    String message = authService.changePassword(
			  passwordChangeRequest);
	    return ResponseEntity.ok(message); 
  }
}

