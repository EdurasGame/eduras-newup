package de.illonis.newup.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class LocalFiles implements FileData {
	private final Path localPath;
	private String version;
	private String tag;
	private Date updated;
	private String releaseChannel;

	public LocalFiles(Path path) {
		this.localPath = path;
		this.version = "n/a";
		this.tag = "n/a";
		this.updated = new Date(0);
		releaseChannel = "n/a";
	}

	public String getReleaseChannel() {
		return releaseChannel;
	}

	@Override
	public List<FileInfo> getFileList() throws IOException {
		Path file = localPath.resolve(HASHLIST_FILENAME);
		if (!Files.exists(file))
			return new LinkedList<FileInfo>();
		return new HashListFile(Files.newInputStream(file,
				StandardOpenOption.READ)).getFiles();
	}

	@Override
	public String getOverallHash() throws IOException {
		Path file = localPath.resolve(TOTALHASH_FILENAME);
		if (!Files.exists(file))
			return "";
		List<String> hash = Files.readAllLines(file, StandardCharsets.UTF_8);
		String hashString = hash.get(0);
		version = hash.get(2);
		tag = hash.get(3);
		releaseChannel = hash.get(1);
		try {
			updated = DATE_FORMAT.parse(hash.get(4));
		} catch (ParseException e1) {
		}
		try {
			updated = DATE_FORMAT.parse(hash.get(2));
		} catch (ParseException e) {
		}
		return hashString;
	}

	void delete(FileInfo file) throws IOException {
		Files.delete(localPath.resolve(file.getFileName()));
	}

	void updateLocalData(List<FileInfo> files, String totalHash,
			String version, String tag, String channel, Date date)
			throws IOException {
		new HashListFile(files).saveTo(localPath.resolve(HASHLIST_FILENAME));
		List<String> lines = new LinkedList<String>();
		lines.add(totalHash);
		lines.add(channel);
		lines.add(version);
		lines.add(tag);
		lines.add(DATE_FORMAT.format(date));
		Files.write(localPath.resolve(TOTALHASH_FILENAME), lines,
				StandardCharsets.UTF_8, StandardOpenOption.CREATE);
	}

	Path computeLocalUrl(FileInfo file) {
		return localPath.resolve(file.getFileName());
	}

	boolean verify(FileInfo file) {
		String hash = computeHash(computeLocalUrl(file));
		return hash.equals(file.getHash());
	}

	/**
	 * Computes SHA-256 hash for given file
	 * 
	 * @param file
	 *            the file to compute hash from.
	 * @return SHA-256 hash in hexadecimal format.
	 */
	public static String computeHash(Path file) {
		try {
			InputStream in = Files.newInputStream(file);
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] md = new byte[8192];

			for (int n = 0; (n = in.read(md)) > -1;)
				digest.update(md, 0, n);

			byte[] hashed = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : hashed) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (IOException | NoSuchAlgorithmException e) {
			return "";
		}
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Date getReleaseDate() {
		return updated;
	}

}
