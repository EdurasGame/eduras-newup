package de.illonis.newup.client;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class LocalFiles implements FileData {
	private final Path localPath;

	public LocalFiles(Path path) {
		this.localPath = path;
	}

	@Override
	public List<FileInfo> getFileList() {
		// TODO: implement
		return null;
	}

	@Override
	public String getOverallHash() {
		// TODO: implement
		return "";
	}

	void delete(FileInfo file) throws IOException {
		// TODO: implement
	}

	String computeLocalUrl(FileInfo file) {
		// TODO: implement
		return "";
	}

}
