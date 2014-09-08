package de.illonis.newup.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class ServerFiles implements FileData {

	private final URL serverUrl;
	private final String authToken;
	private final String releaseChannel;

	public ServerFiles(URL updateUrl, String releaseChannel, String authToken) {
		this.serverUrl = updateUrl;
		this.releaseChannel = releaseChannel;
		this.authToken = authToken;
	}

	@Override
	public List<FileInfo> getFileList() throws IOException {
		return new HashListFile(Networker.readFile(new URL(serverUrl,
				"list.php?channel=" + releaseChannel + "&token=" + authToken)))
				.getFiles();
	}

	@Override
	public String getOverallHash() throws IOException {
		Scanner scanner = new Scanner(Networker.readFile(new URL(serverUrl,
				"all.php?channel=" + releaseChannel + "&token=" + authToken)),
				"UTF-8");
		String hash = scanner.nextLine();
		scanner.close();
		return hash;
	}

	URL computeFileUrl(FileInfo file) throws MalformedURLException {
		return new URL(serverUrl, "file.php?channel=" + releaseChannel
				+ "&name=" + file.getFileName() + "&token=" + authToken);
	}

}
