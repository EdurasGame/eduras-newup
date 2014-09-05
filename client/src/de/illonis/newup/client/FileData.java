package de.illonis.newup.client;

import java.io.IOException;
import java.util.List;

public interface FileData {

	final static String HASHLIST_FILENAME = "";

	List<FileInfo> getFileList() throws IOException;

	String getOverallHash() throws IOException;
}
