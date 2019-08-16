package com.finreach.paymentservice.exception;

public class MyResourceNotFoundException extends RuntimeException {

	public MyResourceNotFoundException() {
        super();
    }
    public MyResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public MyResourceNotFoundException(String message) {
        super(message);
    }
    public MyResourceNotFoundException(Throwable cause) {
        super(cause);
    }
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getMessage() {
		return "Resource is not available.";
	}
}