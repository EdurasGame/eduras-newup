package de.illonis.newup.client;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.illonis.newup.client.UpdateException.ErrorType;

public class UpdateWorker extends Thread {
	private final Networker networker;
	private final LocalFiles local;
	private final ServerFiles server;
	private boolean updateRequired;
	private List<FileInfo> downloadFiles;
	private List<FileInfo> deleteFiles;
	private long totalSize;
	private boolean cancelRequested;
	private final boolean autoStart;
	private IOException ioException;
	private UpdateException updateException;
	private List<FileInfo> serverFiles;
	private String serverAllHash;
	private String notice;
	private final List<UpdateListener> listeners;
	private int deleted;
	private int downloaded;

	UpdateWorker(Networker networker, ServerFiles server, LocalFiles local,
			boolean autoStart, List<UpdateListener> listeners) {
		this.networker = networker;
		this.listeners = listeners;
		this.server = server;
		this.local = local;
		this.autoStart = autoStart;
		deleteFiles = new LinkedList<FileInfo>();
		downloadFiles = new LinkedList<FileInfo>();
		updateRequired = true;
		totalSize = 0;
		deleted = 0;
		downloaded = 0;
		cancelRequested = false;
		notice = "";
	}

	@Override
	public void run() {
		if (cancelRequested)
			return;
		try {
			getUpdateInfo();
			if (autoStart && !cancelRequested) {
				performUpdate();
			}
		} catch (IOException e) {
			ioException = e;
		} catch (UpdateException e) {
			updateException = e;
		}
	}

	boolean isCancelled() {
		return cancelRequested;
	}

	UpdateResult getResult() throws IOException, UpdateException {
		if (ioException != null) {
			throw ioException;
		}
		if (updateException != null)
			throw updateException;
		return new UpdateResult(downloadFiles.size(), totalSize,
				deleteFiles.size(), notice, server, local);
	}

	void cancel() {
		cancelRequested = true;
	}

	void getUpdateInfo() throws IOException, UpdateException {
		downloadFiles.clear();
		totalSize = 0;
		deleteFiles.clear();
		if (cancelRequested)
			return;
		serverAllHash = server.getOverallHash();
		notice = server.getNotice();
		String clientAllHash = local.getOverallHash();
		if (serverAllHash.equals(clientAllHash)) {
			updateRequired = false;
			return;
		}
		if (cancelRequested)
			return;
		List<FileInfo> localFiles = local.getFileList();
		serverFiles = server.getFileList();

		if (cancelRequested)
			return;
		downloadFiles = computeDownloadFiles(localFiles, serverFiles,
				deleteFiles);
		if (cancelRequested)
			return;
		if (downloadFiles.size() > 0 || deleteFiles.size() > 0)
			updateRequired = true;
		for (FileInfo fileInfo : downloadFiles) {
			totalSize += fileInfo.getFileSize();
		}
	}

	void performUpdate() throws UpdateException, IOException {
		if (updateRequired && !cancelRequested) {
			deleteFiles(deleteFiles);
			downloadFiles(downloadFiles);
			notifyProgress("Updating local infos.");
			local.updateLocalData(serverFiles, serverAllHash,
					server.getVersion(), server.getTag(),
					server.getReleaseChannel(), server.getReleaseDate());
			notifyProgress("Done.");
		}
	}

	private void downloadFiles(List<FileInfo> filesToDownload)
			throws UpdateException, IOException {
		for (FileInfo fileInfo : filesToDownload) {
			notifyProgress("Downloading " + fileInfo.getFileName());
			networker.downloadFile(fileInfo);
			downloaded++;
			notifyProgress("Verify download of " + fileInfo.getFileName());
			if (!local.verify(fileInfo)) {
				throw new UpdateException(ErrorType.DOWNLOAD_ERROR,
						"Hash of downloaded file " + fileInfo.getFileName()
								+ " does not match hash on server.");
			}
		}
	}

	private void deleteFiles(List<FileInfo> filesToDelete) {
		for (FileInfo fileInfo : filesToDelete) {
			if (cancelRequested)
				return;
			try {
				notifyProgress("Deleting " + fileInfo.getFileName());
				local.delete(fileInfo);
				deleted++;
			} catch (IOException e) {
				// do nothing as failing to delete a file is no issue at all. It
				// just sits there eating space...
			}
		}
	}

	private List<FileInfo> computeDownloadFiles(List<FileInfo> localFiles,
			List<FileInfo> serverFiles, List<FileInfo> deleteFiles) {
		List<FileInfo> downloadFiles = new LinkedList<FileInfo>();
		deleteFiles.clear();
		for (FileInfo localFile : localFiles) {
			int serverIndex = serverFiles.indexOf(localFile);
			if (serverIndex >= 0) {
				FileInfo serverFile = serverFiles.get(serverIndex);
				if (localFile.hashEquals(serverFile)) {
					// this file is up to date.
				} else {
					// update this file
					downloadFiles.add(serverFile);
				}
			} else {
				// delete this file
				deleteFiles.add(localFile);
			}
			if (cancelRequested)
				return downloadFiles;
		}
		for (FileInfo serverFile : serverFiles) {
			if (!localFiles.contains(serverFile)) {
				// this file is new.
				downloadFiles.add(serverFile);
			}
		}
		return downloadFiles;
	}

	private void notifyProgress(String note) {
		int total = Math.min(deleteFiles.size(), 5) + downloadFiles.size();
		int deleteProgress;
		if (deleteFiles.size() > 0) {
			deleteProgress = (int) Math.floor(((float) deleted / deleteFiles
					.size()) * Math.min(deleteFiles.size(), 5));
		}
		deleteProgress = 0;
		int current = deleteProgress + downloaded;
		int progress = Math.round((float) current * 100 / total);
		for (UpdateListener listener : listeners) {
			listener.updateProgress(progress, note);
		}
	}
}
