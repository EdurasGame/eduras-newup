package de.illonis.newup.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public interface FileData {

	final static String HASHLIST_FILENAME = "files.hash";
	final static String TOTALHASH_FILENAME = "all.hash";
	
	final static SimpleDateFormat DATE_FORMAT = new  SimpleDateFormat("yyyy-MM-d H:m:s");

	List<FileInfo> getFileList() throws IOException;
	
	String getVersion();
	
	String getTag();
	
	Date getReleaseDate();

	String getOverallHash() throws IOException;
}
