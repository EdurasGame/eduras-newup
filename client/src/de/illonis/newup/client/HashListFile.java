package de.illonis.newup.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class HashListFile {
	private final List<FileInfo> files;

	HashListFile(InputStream input) throws IOException, NumberFormatException {
		files = new LinkedList<FileInfo>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				input))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				files.add(parseLine(line));
			}
		}
	}

	private FileInfo parseLine(String line) throws NumberFormatException {
		String[] parts = line.split(" ");
		return new FileInfo(parts[2], parts[0], Long.parseLong(parts[1]));
	}

	List<FileInfo> getFiles() {
		return new LinkedList<FileInfo>(files);
	}
}
