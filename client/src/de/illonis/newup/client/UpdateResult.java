package de.illonis.newup.client;

public class UpdateResult {

	private final int newFilesAmount;
	private final long downloadSize;

	UpdateResult(int newFilesAmount, int downloadSize) {
		this.newFilesAmount = newFilesAmount;
		this.downloadSize = downloadSize;
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

}
