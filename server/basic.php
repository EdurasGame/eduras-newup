<?php
   $channel = $_GET['channel'];
	require_once('db_connection.php');
   db_connection::setupConnection('localhost','newup','newup','DA3JAajyVecXZcnX');
   $sconn = new db_connection();
?>