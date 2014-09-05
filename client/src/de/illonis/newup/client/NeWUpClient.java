package de.illonis.newup.client;

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
	private final String serverUrl;
	private final String authToken;

	/**
	 * Creates a new update-client.
	 * 
	 * @param updateUrl
	 *            url to server. Must contain path to updater-directory.
	 */
	public NeWUpClient(String updateUrl) {
		this(updateUrl, "");
	}

	/**
	 * Creates a new update-client with server-authentication.
	 * 
	 * @param updateUrl
	 *            url to server. Must contain path to updater-directory.
	 * @param authToken
	 *            token for authentication.
	 */
	public NeWUpClient(String updateUrl, String authToken) {
		this.serverUrl = updateUrl;
		this.authToken = authToken;
		listeners = new LinkedList<UpdateListener>();
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

	}
}
