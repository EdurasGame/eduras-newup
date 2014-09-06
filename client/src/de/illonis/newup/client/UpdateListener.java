package de.illonis.newup.client;

import java.io.IOException;

/**
 * Listens for update results.
 * 
 * @author illonis
 * 
 */
public interface UpdateListener {

	/**
	 * Called when update has been successfully completed.
	 * 
	 * @param result
	 *            result details.
	 */
	void onUpdateCompleted(UpdateResult result);

	/**
	 * Called when update information was received but no actual update has been
	 * performed. You can assume that an update is only required if
	 * <code>{@link UpdateResult#getNewFilesAmount()} > 0</code>.
	 * 
	 * @param result
	 *            details for pending update.
	 */
	void onUpdateInfoReceived(UpdateResult result);

	/**
	 * Called when an error occured while receiving update or update
	 * information.
	 * 
	 * @param e
	 *            the error.
	 */
	void onUpdateError(UpdateException e);

	/**
	 * Called when a network error occurs while downloading files or
	 * information.
	 * 
	 * @param e
	 *            the error.
	 */
	void onNetworkError(IOException e);

	/**
	 * Updates the overall update progress.
	 * 
	 * @param progress
	 *            value between 0 and 100.
	 * @param note
	 *            describes current action.
	 */
	void updateProgress(int progress, String note);

	/**
	 * Called when update process was cancelled and has stopped.
	 */
	void onUpdateCancelled();
}
