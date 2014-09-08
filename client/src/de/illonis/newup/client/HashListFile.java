package de.illonis.newup.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

public class HashListFile {
	private final List<FileInfo> files;

	public HashListFile(List<FileInfo> files) {
		this.files = files;
	}

	HashListFile(InputStream input) throws IOException, NumberFormatException {
		files = new LinkedList<FileInfo>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				input, StandardCharsets.UTF_8))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;
				files.add(parseLine(line));
			}
		}
	}

	private FileInfo parseLine(String line) throws NumberFormatException {
		String[] parts = line.trim().split(" ");
		return new FileInfo(parts[2], parts[0], Long.parseLong(parts[1]));
	}

	List<FileInfo> getFiles() {
		return new LinkedList<FileInfo>(files);
	}

	void saveTo(Path path) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		for (FileInfo file : files) {
			writer.write(file.toFileString() + "\n");
		}
		writer.close();
	}
}
