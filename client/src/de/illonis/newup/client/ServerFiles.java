package de.illonis.newup.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

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
				"list.php?channel=test"))).getFiles();
	}

	@Override
	public String getOverallHash() throws IOException {
		Scanner scanner = new Scanner(Networker.readFile(new URL(serverUrl,
				"all.php?channel=test")), "UTF-8");
		String hash = scanner.nextLine();
		scanner.close();
		return hash;
	}

	URL computeFileUrl(FileInfo file) throws MalformedURLException {
		return new URL(serverUrl, "file.php?channel=test&name=" + file.getFileName());
	}

}
