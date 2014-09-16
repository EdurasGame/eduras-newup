package de.illonis.newup.client.example;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import de.illonis.newup.client.ChannelListener;
import de.illonis.newup.client.NeWUpClient;
import de.illonis.newup.client.UpdateException;
import de.illonis.newup.client.UpdateListener;
import de.illonis.newup.client.UpdateResult;

public class ExampleNeWUpClient {

	/**
	 * Starts a simple client demo.
	 * 
	 * @param args
	 *            1. server url<br>
	 *            2. local path<br>
	 *            3. release-channel<br>
	 *            4. autoUpdate? default <i>true</i>
	 * 
	 */
	public static void main(String[] args) {
		String serverAddress = args[0];
		String localPath = args[1];
		String channelName = args[2];
		boolean autoStart = true;
		if (args.length == 4) {
			autoStart = Boolean.parseBoolean(args[3]);
		}

		System.out.println("Starting update");
		URL server;
		try {
			server = new URL(serverAddress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		NeWUpClient.queryChannels(server, new SimpleChannelListener());
		Path local = Paths.get(localPath);
		try {
			System.out.println("Local release channel: "
					+ NeWUpClient.getLocalChannel(local));
		} catch (IOException e) {
		}
		NeWUpClient client = new NeWUpClient(server, local, channelName);
		client.addUpdateListener(new SimpleUpdateListener());
		System.out.println("Checking for updates and perform them if any.");
		client.checkForUpdates(autoStart);
	}

	static class SimpleChannelListener implements ChannelListener {

		@Override
		public void onChannelListReceived(Collection<String> channels) {
			System.out.println("retrieved channel list:");
			for (String channel : channels) {
				System.out.println(channel);
			}
		}

		@Override
		public void onError(Exception e) {
			System.out.println("Error retrieving channels.");
			e.printStackTrace();
		}

	}

	static class SimpleUpdateListener implements UpdateListener {

		@Override
		public void onUpdateCompleted(UpdateResult result) {
			if (result.getNewFilesAmount() == 0) {
				System.out.println("No update required.");
				System.out.printf("Current version: %s (%s) from %s\n",
						result.getLocalVersion(), result.getLocalTag(),
						result.getLocalReleaseDate());
			} else {
				System.out.println("Update completed. Downloaded "
						+ result.getNewFilesAmount() + " new files.");
				System.out.println("Patchnotes: " + result.getNotice());
				System.out.printf("Current version: %s (%s) from %s\n",
						result.getServerVersion(), result.getServerTag(),
						result.getServerReleaseDate());
				System.out.printf("Old version was: %s (%s) from %s\n",
						result.getLocalVersion(), result.getLocalTag(),
						result.getLocalReleaseDate());
			}
			// go on with something
		}

		@Override
		public void onUpdateInfoReceived(UpdateResult result) {
			System.out.println("Got update info: " + result.getNewFilesAmount()
					+ " new files (total " + result.getDownloadSize()
					+ " Kb), " + result.getDeleteLocalAmount()
					+ " files can be deleted locally.");
			System.out.printf("Server version: %s (%s) from %s\n",
					result.getServerVersion(), result.getServerTag(),
					result.getServerReleaseDate());
			System.out.printf("Client version: %s (%s) from %s\n",
					result.getLocalVersion(), result.getLocalTag(),
					result.getLocalReleaseDate());
			System.out.println("Patchnotes: " + result.getNotice());
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
