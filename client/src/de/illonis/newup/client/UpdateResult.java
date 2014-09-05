package de.illonis.newup.client;

public class UpdateResult {

	private final int newFilesAmount;
	private final long downloadSize;
	private final int deleteLocalAmount;

	UpdateResult(int newFilesAmount, long downloadSize, int deleteLocalAmount) {
		this.newFilesAmount = newFilesAmount;
		this.downloadSize = downloadSize;
		this.deleteLocalAmount = deleteLocalAmount;
	}

	/**
	 * Returns total filesize of all files.
	 * 
	 * @return filesize in Kb.
	 */
	public long getDownloadSize() {
		return downloadSize;
	}

	/**
	 * Returns number of files that are new.
	 * 
	 * @return number of new files.
	 */
	public int getNewFilesAmount() {
		return newFilesAmount;
	}

	/**
	 * Returns number of local files that are obsolete.
	 * 
	 * @return number of old files.
	 */
	public int getDeleteLocalAmount() {
		return deleteLocalAmount;
	}

}