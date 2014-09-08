<?php
	require_once('basic.php');
	$basepath = 'files/'.$channel.'/';
	$sconn->delete('files', array('channel' => $channel));
	$files = scandir($basepath);
	$hashes = array();
	foreach($files as $file) {
		if ($file == '.' || $file == '..') {
			continue;
		}
		$filename = $file;
		if (strpos($file, ' ') !== false) {
			echo 'Files must not contain spaces.';
			exit;
		}
		$hash = hash_file("sha256", $basepath.$file);
		$hashes[] = $hash;
		$filesize = filesize($basepath.$file) / 1024;
		$sconn->write('files', array('channel' => $channel, 'filename' => $filename, 'hash' => $hash, 'filesize' => $filesize));
		echo 'added '.$file.PHP_EOL;
	}
	$allhash = hash("sha256",implode('', $hashes));
	echo $allhash;
	$sconn->write('channels', array('hash' => $allhash), array('channelname' => $channel));
?>