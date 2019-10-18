package de.perfectpattern.print.imposition.exception;

import java.io.IOException;

public class CorruptPdfException extends IOException {

	/**
	 * Custom constructor.
	 */
	public CorruptPdfException() {
		super();
	}

	/**
	 * Custom constructor.
	 */
	public CorruptPdfException(String message) {
		super(message);
	}

	/**
	 * Custom constructor.
	 */
	public CorruptPdfException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * Custom constructor.
	 */
	public CorruptPdfException(Throwable throwable) {
		super(throwable);
	}
}
