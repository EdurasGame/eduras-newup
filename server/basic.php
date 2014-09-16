<?php
  if (isset($_GET['channel'])) $channel = $_GET['channel'];
	require_once('db_connection.php');
   db_connection::setupConnection('localhost','newup','newup','DA3JAajyVecXZcnX');
   $sconn = new db_connection();
   
function toFileSize($value, $offset = 1)
{
 $i = $offset;
 $n = array('Byte','Kb','Mb','Gb');
 while ($value > 1024)
 {
   $i++;
   $value = $value / 1024;
 }
 return number_format($value,max(array(0,$i - 1)),',','.').' '.$n[$i];
}

function recurse_copy($src,$dst) {
    $dir = opendir($src);
    @mkdir($dst);
    while(false !== ( $file = readdir($dir)) ) {
        if (( $file != '.' ) && ( $file != '..' )) {
            if ( is_dir($src . '/' . $file) ) {
                recurse_copy($src . '/' . $file,$dst . '/' . $file);
            }
            else {
                copy($src . '/' . $file,$dst . '/' . $file);
            }
        }
    }
    closedir($dir);
}

function getFilesInDir($dir) {
	$d = array();
	while($dirs = glob($dir . '/*')) {
	  $dir .= '/*';
	  if(!$d) {
	     $d = $dirs;
	   } else {
	      $d = array_merge($d,$dirs);
	   }
	}
	$d = array_filter($d, 'is_file');
	return $d;
}

function scanChannel($channel) {
	global $sconn;
	$sconn->delete('files', array('channel' => $channel));
	$basepath = 'files/'.$channel;
	$files = getFilesInDir($basepath);
	$hashes = array();
	foreach($files as $file) {
		if ($file == '.' || $file == '..') {
			continue;
		}
		$filename = str_replace($basepath.'/', '', $file);
		if (strpos($file, ' ') !== false) {
			echo 'Files must not contain spaces: '.$file;
			return false;
		}
		$hash = hash_file("sha256", $file);
		$hashes[] = $hash;
		$filesize = filesize($file) / 1024;
		$sconn->write('files', array('channel' => $channel, 'filename' => $filename, 'hash' => $hash, 'filesize' => $filesize));
	}
	$allhash = hash("sha256",implode('', $hashes));
	$sconn->write('channels', array('hash' => $allhash), array('channelname' => $channel));
	return true;
}
?>