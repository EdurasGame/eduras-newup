package de.illonis.newup.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ServerFiles implements FileData {

	private final URL serverUrl;
	private final String authToken;

	public ServerFiles(URL updateUrl, String authToken) {
		this.serverUrl = updateUrl;
		this.authToken = authToken;
	}

	@Override
	public List<FileInfo> getFileList() throws IOException {
		return new HashListFile(Networker.readFile(new URL(serverUrl,
				HASHLIST_FILENAME))).getFiles();
	}

	@Override
	public String getOverallHash() throws IOException {
		// TODO implement
		return null;
	}

	URL computeFileUrl(FileInfo file) {
		// TODO implement
		return null;
	}

}
