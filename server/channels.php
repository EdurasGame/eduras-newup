<?php
	require_once('basic.php');
	$channelinfo = $sconn->read('channels', array('channelname'));
	while($channel = $channelinfo->fetch_object()) {
		echo $channel->channelname.PHP_EOL;
	}