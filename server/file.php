<?php
	require_once('basic.php');
	$files = $sconn->read('files', '*', array('channel' => $channel, 'filename' => $_GET['name']));
	$file = $files->fetch_object();
	readfile('files/'.$channel.'/'.$file->filename);