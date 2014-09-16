Network Updater
===============

This document describes setup and usage of client and server part of this tool.

# Features

NeWUp can keep a folder structure on clients up-to-date by downloading new or changed files from a server.

* token based authentication for client
* simple and easy to use backend
* multiple release channels

## Limits
NeWUp (currently) has a few limitations that you should keep in order not to break the mechanism:

* the following filenames are not allowed in top-level folder: `all.hash`, `files.hash`
* spaces in filenames and folders are not supported
* you cannot revert to a previous release.

# Server

## Requirements
* Webserver (e.g. Apache2)
* PHP &geq; 5.3
* MySQL database

## Setup

1. Unpack the `newup-server.tar.gz` to a folder on your webserver.
2. Open `https://<webserver/<folder>/setup.php` in your browser
3. Follow the steps on screen.

# Client

## Requirements
* Java 7

## Setup
All you have to do to is adding `newup-client.jar` to your project.

## Usage
> You can take a look at the `example`-package to get a quick example.

### Update
In general, there are two kinds of workflows that you can choose of:

- Trigger download automatically each time an update is available *or*
- Ask the user if he wants to download the update that has been found

Which way you use is of your choice. You could even make this a user setting.

The following code snippet illustrates the basic usage of the updater:

```java
URL server = new URL("http://example.com/updater");
Path local = Paths.get("/my/programm/data");
String channelName = "beta";
NeWUpClient client = new NeWUpClient(server, local, channelName);
client.addUpdateListener(listener);
client.checkForUpdates(true);
```

The boolean argument of the `checkForUpdates()` method decides which way of the above you choose for this time.

The Client itself needs three information to work:

1. the url to the server where the server-part of the updater-tool is running
2. The local path that should be kept up-to-date using the updater.
3. The release channel you want to update.

The update-process itself is asynchronous, so you start the update(-check) routine from anywhere and add an `UpdateListener` that receives a notification when something happens.
So the first thing you should do is create a class that implements `UpdateListener`. This class can perform all gui-updates or actions that should be performed after an update.

If you choose the first method, you should keep in mind, that the `onUpdateInfoReceived(...)` method is never called on the listener.

If you choose the second method, you should prompt the user in the `onUpdateInfoReceived(...)` method and call the `checkForUpdates(bool)` method again **on the same updater instance** with `true` as parameter if the user wants to download the update. So in this case, you have to keep a reference to the `NeWUpClient` instance.

#### the `UpdateResult` object

The `UpdateResult` object is passed to the listener whenever update information are retrieved or an update completed successfully. This object contains relevant data that can be displayed to the user for information.

This object contains the following information:

- number of files (to be) downloaded and their total filesize
- number of obsolete local files that could (or will) be deleted
- local version number, tag and releaseDate *before* update
- server version number, tag and releaseDate
- a release information String for latest release on server

For more details, take a look at JavaDoc.

### Query channels
To retrieve current client channel, call static `getLocalChannel` method.

You can easily query avaliable channels by calling the static `queryChannels` method with an appropriate listener attached.