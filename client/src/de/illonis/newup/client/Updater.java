package de.illonis.newup.client;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.illonis.newup.client.UpdateException.ErrorType;

public class Updater {
	private final Networker networker;
	private LocalFiles local;
	private ServerFiles server;
	private boolean checked;
	private boolean updateRequired;
	private List<FileInfo> downloadFiles;
	private List<FileInfo> deleteFiles;
	private long totalSize;

	Updater(Networker networker) {
		this.networker = networker;
		deleteFiles = new LinkedList<FileInfo>();
		downloadFiles = new LinkedList<FileInfo>();
		updateRequired = true;
		checked = false;
		totalSize = 0;
	}

	UpdateResult getUpdateInfo() {
		String serverAllHash = server.getOverallHash();
		String clientAllHash = local.getOverallHash();
		if (serverAllHash.equals(clientAllHash)) {
			return new UpdateResult(0, 0, 0);
		}
		List<FileInfo> localFiles = local.getFileList();
		List<FileInfo> serverFiles = server.getFileList();

		downloadFiles = computeDownloadFiles(localFiles, serverFiles,
				deleteFiles);
		if (downloadFiles.size() > 0 || deleteFiles.size() > 0)
			updateRequired = true;
		totalSize = 0;
		for (FileInfo fileInfo : downloadFiles) {
			totalSize += fileInfo.getFileSize();
		}
		checked = true;
		return new UpdateResult(downloadFiles.size(), totalSize,
				deleteFiles.size());
	}

	UpdateResult performUpdate() throws UpdateException {
		if (!checked) {
			getUpdateInfo();
		}
		if (updateRequired) {
			try {
				deleteFiles(deleteFiles);
			} catch (IOException e) {
				throw new UpdateException(ErrorType.DELETE_LOCAL_FAILED,
						"Could not delete local file. " + e.getMessage());
			}
			downloadFiles(downloadFiles);
			return new UpdateResult(downloadFiles.size(), totalSize,
					deleteFiles.size());
		} else {
			return new UpdateResult(0, 0, 0);
		}
	}

	private void downloadFiles(List<FileInfo> filesToDownload)
			throws UpdateException {
		for (FileInfo fileInfo : filesToDownload) {
			try {
				networker.downloadFile(fileInfo);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void deleteFiles(List<FileInfo> filesToDelete) throws IOException {
		for (FileInfo fileInfo : filesToDelete) {
			local.delete(fileInfo);
		}
	}

	private List<FileInfo> computeDownloadFiles(List<FileInfo> localFiles,
			List<FileInfo> serverFiles, List<FileInfo> deleteFiles) {
		List<FileInfo> downloadFiles = new LinkedList<FileInfo>();
		deleteFiles.clear();
		for (FileInfo localFile : localFiles) {
			int serverIndex = serverFiles.indexOf(localFile);
			if (serverIndex >= 0) {
				if (localFile.hashEquals(serverFiles.get(serverIndex))) {
					// this file is up to date.
				} else {
					// update this file
					downloadFiles.add(localFile);
				}
			} else {
				// delete this file
				deleteFiles.add(localFile);
			}
		}
		for (FileInfo serverFile : serverFiles) {
			if (!localFiles.contains(serverFile)) {
				// this file is new.
				downloadFiles.add(serverFile);
			}
		}
		return downloadFiles;
	}
}
