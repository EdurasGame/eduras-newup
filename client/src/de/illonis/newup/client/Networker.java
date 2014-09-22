package de.illonis.newup.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Networker {
	private final ServerFiles server;
	private final LocalFiles local;

	Networker(ServerFiles server, LocalFiles local) {
		this.server = server;
		this.local = local;
	}

	static InputStream readFile(URL url) throws IOException {
		return url.openStream();
	}

	void downloadFile(FileInfo fileInfo) throws IOException {
		URL serverUrl = server.computeFileUrl(fileInfo);
		Path localFile = local.computeLocalUrl(fileInfo);
		Files.createDirectories(localFile.getParent());
		try (InputStream in = serverUrl.openStream()) {
			Files.copy(in, localFile, StandardCopyOption.REPLACE_EXISTING);
		}
	}
}
