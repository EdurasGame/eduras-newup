package de.illonis.newup.client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Networker {
	private final ServerFiles server;
	private final LocalFiles local;

	Networker(ServerFiles server, LocalFiles local) {
		this.server = server;
		this.local = local;
	}

	InputStream readFile(URL url) throws IOException {
		return url.openStream();
	}

	void downloadFile(FileInfo fileInfo) throws IOException {
		URL serverUrl = server.computeFileUrl(fileInfo);
		ReadableByteChannel rbc = Channels.newChannel(serverUrl.openStream());
		try (FileOutputStream fos = new FileOutputStream(
				local.computeLocalUrl(fileInfo))) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
		rbc.close();
	}

}
