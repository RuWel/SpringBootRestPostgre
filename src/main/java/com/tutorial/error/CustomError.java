package com.tutorial.error;

import com.tutorial.model.Tutorial;

public class CustomError extends Tutorial {
	private String errorMessage;

	public CustomError(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
