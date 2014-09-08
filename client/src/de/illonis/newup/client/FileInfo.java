package de.illonis.newup.client;

public class FileInfo {
	private final String fileName;
	private final String hash;
	private final long fileSize;

	public FileInfo(String fileName, String hash, long fileSize) {
		this.fileName = fileName;
		this.hash = hash;
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public String getHash() {
		return hash;
	}

	/**
	 * 
	 * @return filesize in Kb.
	 */
	public long getFileSize() {
		return fileSize;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileInfo) {
			FileInfo other = (FileInfo) obj;
			return this.fileName.equals(other.getFileName());
		}
		return false;
	}

	public boolean hashEquals(FileInfo other) {
		return this.hash.equals(other.getHash());
	}

	public String toFileString() {
		return this.getHash() + " " + this.getFileSize() + " "
				+ this.getFileName();
	}
}
