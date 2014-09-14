package de.illonis.newup.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerFiles implements FileData {

	private final URL serverUrl;
	private final String authToken;
	private final String releaseChannel;
	private String notice;
	private String version;
	private String tag;
	private Date released;

	public ServerFiles(URL updateUrl, String releaseChannel, String authToken) {
		this.serverUrl = updateUrl;
		this.releaseChannel = releaseChannel;
		this.authToken = authToken;
		notice = "";
		version = "";
		tag = "";
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
		try {
			version = scanner.nextLine();
			tag = scanner.nextLine();
			released = DATE_FORMAT.parse(scanner.nextLine());
			notice = scanner.nextLine();
		} catch (NoSuchElementException | ParseException e) {
			notice = "";
		}
		scanner.close();
		return hash;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public String getVersion() {
		return version;
	}

	String getNotice() {
		return notice;
	}

	URL computeFileUrl(FileInfo file) throws MalformedURLException {
		return new URL(serverUrl, "file.php?channel=" + releaseChannel
				+ "&name=" + file.getFileName() + "&token=" + authToken);
	}

	@Override
	public Date getReleaseDate() {
		return released;
	}

}
