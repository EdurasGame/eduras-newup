<?php
	require_once('basic.php');
	$channelinfo = $sconn->read('channels', '*', array('channelname' => $channel));
    $channelinfo = $channelinfo->fetch_object();
	echo $channelinfo->hash.PHP_EOL;
	echo $channelinfo->displaytext;
?>