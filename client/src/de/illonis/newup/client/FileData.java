package de.illonis.newup.client;

import java.util.List;

public interface FileData {
	List<FileInfo> getFileList();

	String getOverallHash();
}
