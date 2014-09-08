package de.illonis.newup.client;

import java.io.IOException;
import java.util.List;

public interface FileData {

	final static String HASHLIST_FILENAME = "files.hash";
	final static String TOTALHASH_FILENAME = "all.hash";

	List<FileInfo> getFileList() throws IOException;

	String getOverallHash() throws IOException;
}
