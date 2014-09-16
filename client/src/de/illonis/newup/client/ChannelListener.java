package de.illonis.newup.client;

import java.util.Collection;

public interface ChannelListener {

	void onChannelListReceived(Collection<String> channels);
	
	void onError(Exception e);
}
