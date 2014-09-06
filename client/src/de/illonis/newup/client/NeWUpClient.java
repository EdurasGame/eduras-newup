package de.illonis.newup.client;

import java.io.IOException;
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
	private LocalFiles local;
	private ServerFiles server;
	private final Networker networker;
	private UpdateRunner thread;

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
		thread = null;
	}

	public void addUpdateListener(UpdateListener listener) {
		listeners.add(listener);
	}

	public void removeUpdateListener(UpdateListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Checks for update on remote server and optionally starts downloading it.
	 * If <i>autoStart</i> is <i>true</i>, the update will be started
	 * automatically if there are new files. Otherwise, listener will receive a
	 * {@link UpdateListener#onUpdateInfoReceived(UpdateResult)} call with
	 * information about download size and amount and can manually call this
	 * method again with <code>autoStart=true</code>.<br>
	 * <b>Note:</b> calling {@link #checkForUpdates(boolean)} multiple times on
	 * the same instance of {@link NeWUpClient} will not fetch update-data from
	 * server again. To refresh update-data, create a new instance and call this
	 * method there.
	 * 
	 * @param autoStart
	 *            true if updates should be downloaded automatically after
	 *            check, false if not.
	 */
	public void checkForUpdates(boolean autoStart) {
		if (thread.isAlive()) {
			throw new IllegalStateException(
					"There is an update check running. Please stop it or wait for it to finish before running a new check.");
		} else {
			thread = new UpdateRunner(autoStart);
			thread.start();
		}
	}

	/**
	 * Aborts update process. Aborting does not restore the state before update
	 * process has been started.
	 */
	public void abort() {
		thread.cancel();
	}

	private void notifyUpdateInfoReceived(UpdateResult result) {
		for (UpdateListener listener : listeners) {
			listener.onUpdateInfoReceived(result);
		}
	}

	private class UpdateRunner extends Thread {
		private final Updater updater;
		private final boolean autoStart;

		public UpdateRunner(boolean autoStart) {
			this.autoStart = autoStart;
			updater = new Updater(networker, autoStart);
		}

		public void cancel() {
			updater.cancel();
		}

		@Override
		public void run() {
			updater.start();
			try {
				updater.join();
				UpdateResult result;
				try {
					if (updater.isCancelled()) {
						for (UpdateListener listener : listeners) {
							listener.onUpdateCancelled();
						}
						return;
					}
					result = updater.getResult();

					if (autoStart) {
						for (UpdateListener listener : listeners) {
							listener.onUpdateCompleted(result);
						}
					} else {
						notifyUpdateInfoReceived(result);
					}
				} catch (IOException e) {
					for (UpdateListener listener : listeners) {
						listener.onNetworkError(e);
					}
				} catch (UpdateException e) {
					for (UpdateListener listener : listeners) {
						listener.onUpdateError(e);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
