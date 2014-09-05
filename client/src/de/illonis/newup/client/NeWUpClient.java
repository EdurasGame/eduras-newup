package de.illonis.newup.client;

import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Client that checks for updates on server and performs them.<br>
 * A simple token-based authentication is optional.
 * 
 * @author illonis
 * 
 */
public final class NeWUpClient {

	private final List<UpdateListener> listeners;
	private final Updater updater;
	private LocalFiles local;
	private ServerFiles server;
	private final Networker networker;

	/**
	 * Creates a new update-client.
	 * 
	 * @param updateUrl
	 *            url to server. Must contain path to updater-directory.
	 */
	public NeWUpClient(URL updateUrl, Path localPath) {
		this(updateUrl, localPath, "");
	}

	/**
	 * Creates a new update-client with server-authentication.
	 * 
	 * @param updateUrl
	 *            url to server. Must contain path to updater-directory.
	 * @param authToken
	 *            token for authentication.
	 */
	public NeWUpClient(URL updateUrl, Path localPath, String authToken) {
		listeners = new LinkedList<UpdateListener>();
		local = new LocalFiles(localPath);
		server = new ServerFiles(updateUrl, authToken);
		networker = new Networker(server, local);
		updater = new Updater(networker);
	}

	public void addUpdateListener(UpdateListener listener) {
		listeners.add(listener);
	}

	public void removeUpdateListener(UpdateListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Checks for update on remote server and optionally starts downloading it.
	 * If <i>autoStart</i> is <i>true</i>, {@link #performUpdate()} method will
	 * automatically called if there are new files. Otherwise, listener will
	 * receive a {@link UpdateListener#onUpdateInfoReceived(UpdateResult)} call
	 * with information about download size and amount and can manually trigger
	 * the update method.
	 * 
	 * @param autoStart
	 *            true if updates should be downloaded automatically after
	 *            check, false if not.
	 */
	public void checkForUpdates(boolean autoStart) {
		// FIXME: make threaded
		UpdateResult result = updater.getUpdateInfo();
		if (result.getNewFilesAmount() > 0) {
			if (autoStart) {
				performUpdate();
			} else {
				notifyUpdateInfoReceived(result);
			}
		} else {
			notifyUpdateInfoReceived(result);
		}
	}

	private void notifyUpdateInfoReceived(UpdateResult result) {
		for (UpdateListener listener : listeners) {
			listener.onUpdateInfoReceived(result);
		}
	}

	/**
	 * Performs an update and calls either
	 * {@link UpdateListener#onUpdateCompleted(UpdateResult)} or
	 * {@link UpdateListener#onUpdateError(UpdateException)} when finished. This
	 * method checks for updates if no previous check was issued.<br>
	 * While updating, {@link UpdateListener#updateProgress(int, String)} calls
	 * may be issued.
	 */
	public void performUpdate() {
		// FIXME: make threaded
		UpdateResult result;
		try {
			result = updater.performUpdate();
		} catch (UpdateException e) {
			for (UpdateListener listener : listeners) {
				listener.onUpdateError(e);
			}
			return;
		}
		for (UpdateListener listener : listeners) {
			listener.onUpdateCompleted(result);
		}
	}
}
