package de.illonis.newup.client.demo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.illonis.newup.client.NeWUpClient;
import de.illonis.newup.client.UpdateException;
import de.illonis.newup.client.UpdateListener;
import de.illonis.newup.client.UpdateResult;

public class NeWUpClientDemo {

	/**
	 * Starts a simple client demo.
	 * 
	 * @param args
	 *            <li>1. server url <li>2. local path
	 * 
	 */
	public static void main(String[] args) {
		System.out.println("Starting update");
		URL server;
		try {
			server = new URL("http://192.168.0.2/newup/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		Path local = Paths.get("/tmp/newup/");
		NeWUpClient client = new NeWUpClient(server, local);
		client.addUpdateListener(new SimpleUpdateListener());
		System.out.println("Checking for updates and perform them if any.");
		client.checkForUpdates(true);
	}

	static class SimpleUpdateListener implements UpdateListener {

		@Override
		public void onUpdateCompleted(UpdateResult result) {
			System.out.println("Update completed. Downloaded "
					+ result.getNewFilesAmount() + " new files.");
			// go on with something
		}

		@Override
		public void onUpdateInfoReceived(UpdateResult result) {
			System.out.println("Got update info: " + result.getNewFilesAmount()
					+ " new files (total " + result.getDownloadSize()
					+ " Kb), " + result.getDeleteLocalAmount()
					+ " files can be deleted locally.");
		}

		@Override
		public void onUpdateError(UpdateException e) {
			e.printStackTrace();
		}

		@Override
		public void onNetworkError(IOException e) {
			e.printStackTrace();
		}

		@Override
		public void updateProgress(int progress, String note) {
			System.out.println("[" + progress + "%] " + note);
		}

		@Override
		public void onUpdateCancelled() {
			System.out.println("Update has been cancelled.");
		}

	}

}
