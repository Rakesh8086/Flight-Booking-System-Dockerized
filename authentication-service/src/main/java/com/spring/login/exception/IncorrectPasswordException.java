package com.spring.login.exception;

public class IncorrectPasswordException extends RuntimeException {
	public IncorrectPasswordException(String message) {
	    super(message);
	}
}