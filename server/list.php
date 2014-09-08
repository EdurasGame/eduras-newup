<?php
	require_once('basic.php');
	$files = $sconn->read('files', '*', array('channel' => $channel));
	while($file = $files->fetch_object()) {
		echo $file->hash.' '.$file->filesize.' '.$file->filename.PHP_EOL;	
	}
?>