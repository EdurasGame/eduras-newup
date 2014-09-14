package de.illonis.newup.client;

import java.util.Date;

public class UpdateResult {

	private final int newFilesAmount;
	private final long downloadSize;
	private final int deleteLocalAmount;
	private final String notice;
	private final String serverVersion;
	private final String serverTag;
	private final Date serverReleaseDate;
	private final Date localReleaseDate;
	private final String localVersion;
	private final String localTag;

	UpdateResult(int newFilesAmount, long downloadSize, int deleteLocalAmount,
			String note, ServerFiles server, LocalFiles local) {
		this.newFilesAmount = newFilesAmount;
		this.downloadSize = downloadSize;
		this.deleteLocalAmount = deleteLocalAmount;
		this.notice = note;
		this.serverTag = server.getTag();
		this.serverVersion = server.getVersion();
		this.serverReleaseDate = server.getReleaseDate();
		this.localReleaseDate = local.getReleaseDate();
		this.localTag = local.getTag();
		this.localVersion = local.getVersion();
	}

	/**
	 * Returns tag of current version on server.
	 * 
	 * @return current tag on server.
	 */
	public String getServerTag() {
		return serverTag;
	}

	/**
	 * Returns version number of current version on server.
	 * 
	 * @return current version on server.
	 */
	public String getServerVersion() {
		return serverVersion;
	}

	/**
	 * @return releasedate of current version on server.
	 */
	public Date getServerReleaseDate() {
		return serverReleaseDate;
	}

	/**
	 * @return releasedate of local version before update.
	 */
	public Date getLocalReleaseDate() {
		return localReleaseDate;
	}

	/**
	 * Returns tag of version on client before update.
	 * 
	 * @return tag on client before update.
	 */
	public String getLocalTag() {
		return localTag;
	}

	/**
	 * Returns version number of local version on client before update.
	 * 
	 * @return version on client before update.
	 */
	public String getLocalVersion() {
		return localVersion;
	}

	/**
	 * Returns the notice for this update.
	 * 
	 * @return a text that should be displayed to the user after update.
	 */
	public String getNotice() {
		return notice;
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
