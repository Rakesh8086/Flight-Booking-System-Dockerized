package com.spring.login.security.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.login.exception.PasswordExpiredException;
import com.spring.login.model.User;
import com.spring.login.repository.UserRepository;
import com.spring.login.exception.PasswordExpiredException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;
  private static final long PASSWORD_EXPIRY_MINS = 5;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    if(isPasswordExpired(user)) {
        throw new PasswordExpiredException("Password expired");
    }

    return UserDetailsImpl.build(user);
  }
  
  private boolean isPasswordExpired(User user) {
	  System.out.println("*******" + user.getPasswordChangedAt());
	  System.out.println("#######" + LocalDateTime.now());
      return user.getPasswordChangedAt()
                 .plusMinutes(PASSWORD_EXPIRY_MINS)
                 .isBefore(LocalDateTime.now());
  }
}