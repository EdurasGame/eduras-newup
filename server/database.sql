-- phpMyAdmin SQL Dump
-- version 3.4.10.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Erstellungszeit: 08. Sep 2014 um 19:11
-- Server Version: 5.5.38
-- PHP-Version: 5.4.32-2+deb.sury.org~precise+1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Datenbank: `newup`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `channels`
--
CREATE TABLE IF NOT EXISTS `channels` (
  `channelname` varchar(50) NOT NULL,
  `hash` char(64) NOT NULL,
  `releasenumber` varchar(10) NOT NULL,
  `releasetag` varchar(50) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `displaytext` varchar(200) NOT NULL,
  PRIMARY KEY (`channelname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `files`
--

CREATE TABLE IF NOT EXISTS `files` (
  `channel` varchar(50) NOT NULL,
  `filename` varchar(150) NOT NULL,
  `hash` char(64) NOT NULL,
  `filesize` int(11) NOT NULL,
  PRIMARY KEY (`channel`,`filename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

