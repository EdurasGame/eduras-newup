<?php
	if($_SERVER["HTTPS"] != "on") {
	   header("HTTP/1.1 301 Moved Permanently");
	   header("Location: https://" . $_SERVER["SERVER_NAME"] . $_SERVER["REQUEST_URI"]);
	   exit();
	}
	require_once('basic.php');
	function startsWith($haystack, $needle)
{
    return $needle === "" || strpos($haystack, $needle) === 0;
}

function rrmdir($dir) {
   if (is_dir($dir)) {
     $objects = scandir($dir);
     foreach ($objects as $object) {
       if ($object != "." && $object != "..") {
         if (filetype($dir."/".$object) == "dir") rrmdir($dir."/".$object); else unlink($dir."/".$object);
       }
     }
     reset($objects);
     rmdir($dir);
   }
}

function listFiles($folder, $files) {
	$current = $folder;
	echo '<div class="files">';
	$offset = strlen($folder) + 1;
	$lastdir = $current;
	$depth = 0;
	$lastinset = 0;
	foreach($files as $file) {
		$file = substr($file, $offset);
		$lastslash = strrpos($file, '/');
		if ($lastslash === FALSE) {
			$filedir = '';
			$lastslash = -1;
		}
		else
			$filedir = substr($file, 0, $lastslash);
		$inset = substr_count($filedir, '/');
		$filename = substr($file, $lastslash + 1);
		if ($filedir == '')
			echo '<img src="images/file.png" alt="">'.$filename;
		elseif ($current == $filedir) {
			echo str_repeat('&nbsp;', 4 * $lastinset);
			echo '<img src="images/file.png" alt="-">'.$filename;
		}
		else {
			if (startsWith($file, $lastdir)) {
				$thisfiledir = substr($filedir, strlen($lastdir) + 1);
			} else {
				$thisfiledir = $filedir;
			}
			$subdirs = preg_split('#/#', $thisfiledir);
			foreach($subdirs as $subdir) {
				echo str_repeat('&nbsp;', 4 *$inset);
				echo '<img src="images/folder.png" alt="DIR"> '.$subdir.'/<br>';
				$inset++;
			}
			$current = $filedir;
			echo str_repeat('&nbsp;', 4*$inset);
			echo '<img src="images/file.png" alt="-">';
			echo $filename;
		}
		$lastdir = $filedir;
		$lastinset = $inset;
		echo '<br>';
	}
	echo '</div>';
}
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="Content-Language" content="en">
		<meta http-equiv="Pragma" content="no-cache">
		<meta name="robots" content="noindex, nofollow">
		<title>Update manager</title>
		<link rel="stylesheet" type="text/css" href="style.css">
	</head>
	<body>
		<h1>Manage releases</h1>
		<?php
			if (!isset($_GET['act'])) {
		?>
		<h2>Overview</h2>
		<table>
			<tr>
				<th>Channel</th><th>Current version</th><th>released</th><th>action</th>			
			</tr>
		<?php
			$channeldata = $sconn->read('channels', '*');
			while($channel = $channeldata->fetch_object()) {
				$empty = ($channel->updated == '0000-00-00 00:00:00');
				echo '<tr><td>'.$channel->channelname.'</td>';
				if ($empty) {
					echo '<td colspan="2"><i>no release yet</i>';
				} else {
					echo '<td>'.$channel->releasenumber.' ('.$channel->releasetag.')</td>
					<td>'.$channel->updated;
				}
				echo '</td><td>';
				if (!$empty) {
						echo '<a href="?act=details&channel='.$channel->channelname.'">details</a>';
				}
				echo '<a href="?act=update&channel='.$channel->channelname.'">update</a>
				<a href="?act=delete&channel='.$channel->channelname.'">delete</a></td></tr>';				
			}
		?>
		</table>
		<p><a href="?act=addchannel">create a new channel</a></p>
		<?php
		}
		else {	
			echo '<p><a href="index.php">back to index</a></p>';
			$act = $_GET['act'];
			switch($act) {
				case 'addchannel':
					?>
						<h2>New channel</h2>
						<form action="?act=doaddchannel" method="POST">
						Channel name: <input type="text" name="channelname"><br>
						<input type="submit" value="Create">
						</form>
					<?php
				break;
				case 'delete':
					echo '<h2>Delete channel</h2>';
					
					echo '<p>Do you really want to delete channel "'.$channel.'"?</p>';
					echo '<p><b>You cannot recover a deleted channel!</b></p>';
					echo '<p><a href="?act=dodelete&amp;channel='.$channel.'" style="background:red;">Delete channel "'.$channel.'"</a> <a href="index.php">Abort</a></p>';
					
				break;
				case 'dodelete':
					$sconn->delete('files', array('channel' => $channel));
					$dir = 'files/'.$channel;
					$it = new RecursiveDirectoryIterator($dir, RecursiveDirectoryIterator::SKIP_DOTS);
					$files = new RecursiveIteratorIterator($it,
					             RecursiveIteratorIterator::CHILD_FIRST);
					foreach($files as $file) {
					    if ($file->getFilename() === '.' || $file->getFilename() === '..') {
					        continue;
					    }
					    if ($file->isDir()){
					        rmdir($file->getRealPath());
					    } else {
					        unlink($file->getRealPath());
					    }
					}
					rmdir($dir);					
					$sconn->delete('channels', array('channelname' => $channel));
					echo '<p>Channel '.$channel.' deleted.</p>';
				break;
				case 'doaddchannel':
					echo '<h2>New channel</h2>';
					$channelname = $_POST['channelname'];
					$pattern = "/^[a-z]{3,}$/i";
					if ($sconn->count('channels', '', array('channelname' => $channelname)) > 0) {
						echo '<p>Channel already exists.</p>';
					} elseif (preg_match($pattern, $channelname)) {
						$sconn->write('channels', array('channelname' => $channelname, 'updated' => '0000-00-00 00:00:00'));
						mkdir('files/'.$channelname);
						echo '<p>channel created</p>';
					} else {
						echo '<p>Invalid channelname entered.</p>';
					}
				break;
				case 'verydoupdate':
					$sfolder = $_POST['folder'];
					$channelname = $_POST['channel'];
					$version = $_POST['releasenumber'];
					$tag = $_POST['releasetag'];
					$displaytext = $_POST['displaytext'];
					$targetfolder = 'files/'.$channelname;
					rrmdir($targetfolder);
					mkdir($targetfolder);
					recurse_copy($sfolder, $targetfolder);
				    $sconn->write('channels', array('releasenumber' => $version, 'releasetag' => $tag, 'displaytext' => $displaytext), array('channelname' => $channelname));
					if (scanChannel($channelname)) {
						echo 'success';
					} else {
						echo 'fail';
					}									
				break;
				case 'details':
					$channeldata = $sconn->read('channels','*', array('channelname' => $_GET['channel']));
					$channeldata = $channeldata->fetch_object();
					echo '<h2>Details of channel "'.$channeldata->channelname.'"</h2>
					<b>Current version:</b> '.$channeldata->releasenumber.' ('.$channeldata->releasetag.')<br>
					<b>Current release note:</b> '.$channeldata->displaytext.'<br>
					<b>Released:</b> '.$channeldata->updated.'<br>
					<b>Total files:</b> ';
					$files = $sconn->query('SELECT COUNT(*) AS numfiles, SUM(filesize) AS totalsize FROM `files` WHERE `channel` = "'.$channeldata->channelname.'";');
					echo $sconn->getError();
					$files = $files->fetch_object();
					echo $files->numfiles.' (ca. '.toFileSize($files->totalsize).')';
					$dir = 'files/'.$channeldata->channelname;
					listFiles($dir, getFilesInDir($dir));
				break;
				case 'doupdate':
					$channelname = $_POST['channel'];
					echo '<h2>Verify data</h2>
					<p>Please verify that data below are correct before proceeding.</p>';
					$version = $_POST['releasenumber'];
					$tag = $_POST['releasetag'];
					$displaytext = $_POST['displaytext'];
					$folder = $_POST['folder'];
					echo '<form action="?act=verydoupdate" method="POST">
					<input type="hidden" name="releasenumber" value="'.$version.'">
					<input type="hidden" name="releasetag" value="'.$tag.'">
					<input type="hidden" name="displaytext" value="'.$displaytext.'">
					<input type="hidden" name="channel" value="'.$channelname.'">
					<input type="hidden" name="folder" value="'.$folder.'">';
					
					echo '<p><b>Channel:</b> '.$channelname.'</p>';
					echo '<p><b>Version:</b> '.$version.'</p>';
					echo '<p><b>Tag:</b> '.$tag.'</p>';
					echo '<p><b>User hint:</b> '.$displaytext.'</p>';
					
					$dir = $folder;
					$d = getFilesInDir($dir);
					natcasesort($d);
					echo '<p>This release will contain the following files:</p>';
					listFiles($folder, $d);
					echo '<p>If anything is incorrect, please go back to previous page and correct your input.</p>';
					echo '<p><input type="submit" value="Publish this release"></p></form>';
				break;
				case 'update':
					$channel = $sconn->read('channels','*', array('channelname' => $_GET['channel']));
					$channel = $channel->fetch_object();
					echo '<h2>Update channel "'.$channel->channelname.'"</h2>';
					?>
					<form action="?act=doupdate" method="POST">
						<input type="hidden" name="channel" value="<?php echo $channel->channelname;?>">
						Version: <input type="text" name="releasenumber"> (last was <?php echo $channel->releasenumber;?>)<br>
						Tag: <input type="text" name="releasetag"><br>
						User notice: <input type="text" name="displaytext"><br>
						Source folder: <select name="folder">
							<?php
								$dirs = glob('newfiles/*',GLOB_ONLYDIR);
								foreach($dirs as $dir) {
									echo '<option>'.$dir.'</option>';
								}
							?>
						</select><br>
						<input type="submit" value="Create release">
					</form>
					<?php
				break;
				default:
					echo '<p>action not supported.</p>';
				break;					
			}
		}
		?>
	</body>
</html>