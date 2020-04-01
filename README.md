# ariia  (0.2.37)

OKaria is a smart command line download manager.


## Dependency
 - okhttp (3.14.7)
 - gson (2.8.4)
 - lawnha (0.2.0)
 

### Options :
 - support HTTP and HTTPS.
 - support Header and Cookies
 - cross-Platform: support Linux, Unix, Windows and Mac OS.
 - parallel download, using segment.
 - saved setting every 1 second.
 - support Google Chrome with extension.
 - support using Proxy [HTTP, HTTPS, SOCKS], COMMING SOON JSCH(SSH)
 - support download from maven repository
 - supported arguments
 - save time while downloading, by spiriting download process and writing data to hard disk.
 - using cache memory, to reduce hate resulted by continuous writhing to (mechanical/old/magnet hard disk),
 		not test on ssd Hard Disk
 - solve heat problem, by reduce write time to hard disk - flush on fixed rate of time every 5s.
  
 
 
 ```
 java -jar okaria.jar URL
	-u		--url			[-u] add new link/url to download manager 
	-i		--input-file		downoload from text file - list of urls
	-m		--metalink		downoload from  metalink text/xml file - list of urls on deffrient servers for the same daownloadable file
	-r		--http-referer		set referer header for that link
	-H		--header		set one/multiable different header(s) for that link
	-cf		--cookie-file		add cookie(s) from standered cookie file
	-o		--file-name		save download link to file on hard-disk
	-sp		--save-path		set directory of download process
	-t		--tries			number of tries when failler, then giveup (0 for keep-try )
	-c		--max-connection	max connection for current session for each link
	-n		--num-downloa		number of download links in queue, if more links, will be in watting list
	-p		--proxy			set proxy to http://host:port[8080]/, support protocols http, https ans socks4/5
	-http	--http-proxy		use http proxy [host:port] format
	-https	--https-proxy		use https proxy [host:port] format
	-socks	--socks-proxy		use socks proxy [host:port] format
	-socks4	--socks4-proxy		use socks4 proxy [host:port] format
	-socks5	--socks5-proxy		use socks5 proxy [host:port] format
	-s		--ssh			use ssh connection as proxy - [remotehost:port], not supported yet
	-su		--ssh-user		set ssh user name - remote login user name
	-sp		--ssh-pass		set remote login password, if non will be asked from terminal
	-h		--help			print this message
	-d		--debug-level		display logging, Levels: [off, error, debug, warning, info, fine, finer, finest, all]
	-v		--version		display the version of okaria
 ```

### TO:DO:LIST

 - add SSH implemntaion

### Overview

![screenshot-01](img/download-ubuntu-mini.gif)

![screenshot-01](img/mini-table-01.png)
![screenshot-02](img/mini-table-02.png)

### Text Link Format: 
![Format1](img/text-format01.png)
![Format2](img/text-format02.png)


### ISSUE FIX:

 - fix load cookies from file
 - fix memory leak 


