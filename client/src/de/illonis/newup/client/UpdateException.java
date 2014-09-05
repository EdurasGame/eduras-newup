package de.illonis.newup.client;

public class UpdateException extends Exception {

	private static final long serialVersionUID = 1L;

	public enum ErrorType {
		SERVER_NOT_FOUND, REMOTE_FILES_MISSING, DOWNLOAD_ERROR, DELETE_LOCAL_FAILED;
	}

	private final ErrorType type;

	UpdateException(ErrorType type, String message) {
		super(message);
		this.type = type;
	}

	/**
	 * @return type of error.
	 */
	public ErrorType getType() {
		return type;
	}

}
