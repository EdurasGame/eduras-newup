<?php
	require_once('basic.php');
	$channelinfo = $sconn->read('channels', '*', array('channelname' => $channel));
    $channelinfo = $channelinfo->fetch_object();
	echo $channelinfo->hash.PHP_EOL;
	echo $channelinfo->releasenumber.PHP_EOL;
	echo $channelinfo->releasetag.PHP_EOL;
	echo $channelinfo->updated.PHP_EOL;
	echo $channelinfo->displaytext;
?>