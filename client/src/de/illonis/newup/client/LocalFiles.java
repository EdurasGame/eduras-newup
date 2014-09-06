package de.illonis.newup.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class LocalFiles implements FileData {
	private final Path localPath;

	public LocalFiles(Path path) {
		this.localPath = path;
	}

	@Override
	public List<FileInfo> getFileList() throws IOException {
		return new HashListFile(Files.newInputStream(
				localPath.resolve(HASHLIST_FILENAME), StandardOpenOption.READ))
				.getFiles();
	}

	@Override
	public String getOverallHash() throws IOException {
		// TODO: implement
		return "";
	}

	void delete(FileInfo file) throws IOException {
		Files.delete(localPath.resolve(file.getFileName()));
	}

	String computeLocalUrl(FileInfo file) {
		return localPath.resolve(file.getFileName()).toString();
	}
	
	boolean verify(FileInfo file) {
		// TODO: implement
		return false;		
	}

}
