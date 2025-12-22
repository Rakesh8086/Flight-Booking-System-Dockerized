package com.spring.login.exception;

public class IncorrectEmailIdException extends RuntimeException {
	public IncorrectEmailIdException(String message) {
	    super(message);
	}
}