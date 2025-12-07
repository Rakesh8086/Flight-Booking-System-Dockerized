package com.spring.login.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.login.model.ERole;
import com.spring.login.model.Role;
import com.spring.login.model.User;
import com.spring.login.model.response.MessageResponse;
import com.spring.login.model.response.UserInfoResponse;
import com.spring.login.repository.RoleRepository;
import com.spring.login.repository.UserRepository;
import com.spring.login.request.LoginRequest;
import com.spring.login.request.SignupRequest;
import com.spring.login.security.jwt.JwtUtils;
import com.spring.login.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600) // * used for public end points
public class AuthController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(
			@Valid @RequestBody SignupRequest signUpRequest) {
	    if(userRepository.existsByUsername(signUpRequest.getUsername())) {
	      return ResponseEntity.badRequest().body(new MessageResponse(
	    		  "Username is already taken"));
	    }

	    if(userRepository.existsByEmail(signUpRequest.getEmail())) {
	      return ResponseEntity.badRequest().body(new MessageResponse(
	    		  "Email is already in use"));
	    }

	    // Create new account
	    User user = new User(signUpRequest.getUsername(),
	                         signUpRequest.getEmail(),
	                         encoder.encode(signUpRequest.getPassword()));

	    Set<String> allRoles = signUpRequest.getRole();
	    Set<Role> roles = new HashSet<>(); // to remove duplicates

	    if(allRoles == null) {
	      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
	          .orElseThrow(() -> new RuntimeException("Role is not found."));
	      roles.add(userRole);
	    } 
	    else {
	    	for(String role:allRoles) {
	    	    if("ROLE_ADMIN".equals(role)) {
	    	        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
	    	            .orElseThrow(() -> new RuntimeException("Role is not found."));
	    	        roles.add(adminRole);

	    	    } 
	    	    else if("ROLE_MODERATOR".equals(role)) {
	    	        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
	    	            .orElseThrow(() -> new RuntimeException("Role is not found."));
	    	        roles.add(modRole);

	    	    } 
	    	    else {
	    	        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
	    	            .orElseThrow(() -> new RuntimeException("Role is not found."));
	    	        roles.add(userRole);
	    	    }
	    	}
	    }

	    user.setRoles(roles);
	    userRepository.save(user);

	    return ResponseEntity.ok(new MessageResponse("User registered successfully"));
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(
			@Valid @RequestBody LoginRequest loginRequest) {
		// auth the user
	    Authentication authentication = authenticationManager
	        .authenticate(new UsernamePasswordAuthenticationToken(
	        		loginRequest.getUsername(), loginRequest.getPassword()));
	    // store the authenticated user in security context of spring
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    // retrieve the details of user
	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	    // generate jwt token
	    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
	    // get roles
	    List<String> roles = userDetails.getAuthorities().stream()
	        .map(item -> item.getAuthority())
	        .collect(Collectors.toList());
	    // return the user details as response
	    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
	        .body(new UserInfoResponse(userDetails.getId(),
	                                   userDetails.getUsername(),
	                                   userDetails.getEmail(),
	                                   roles));
	  }
	
	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser() {
	    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
	    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
	        .body(new MessageResponse("You've been signed out!"));
	}  
}
