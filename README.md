# Datastore Disk Free in MB with Volume Filter Inclusion Monitor

See http://uptimesoftware.github.io for more information.

### Tags 
 plugin   deprecated  

### Category

plugin

### Version Compatibility


  
* Datastore Disk Free in MB with Volume Filter Inclusion Monitor 1.1 - 5.5
  

  
* Datastore Disk Free in MB with Volume Filter Inclusion Monitor 1.0 - 5.4, 5.3
  


### Description
This plug-in provides a solution to set thresholds on the actual number of megabytes of disk space available on any given file system. It will find the file system with the least available space and generate alerts using thresholds based on acceptable space remaining on a volume.
We recommend you use the built-in File System Capacity Monitor as it now has the same functionality.


### Supported Monitoring Stations

5.5

### Supported Agents
None; no agent required

### Installation Notes
<ol>
<li>Place jar file(s) in "/core" directory</li>
</ol>


<p>If on Linux/Solaris:
- edit the /uptime.lax file and add the new jar files to the line that starts with:
lax.class.path=...</p>

<p>Note: It will be a single long line even though it looks like it's on multiple lines. Make sure the new jar filenames are on the same line.</p>

<ol>
<li><p>Restart the up.time Data Collector (core)</p></li>
<li><p>Place the xml file in the uptime directory and run the following command(s) from the uptime directory:</p>

<blockquote><p>scripts\erdcloader -x</p></blockquote></li>
</ol>



### Dependencies
<p>None; Java core monitor</p>


### Input Variables

* {"Monitored volumes/file systems separated by commas (e.g. value"=>"c,f)"}


### Output Variables


* {"Monitored volumes/file systems separated by commas (e.g. value"=>"c,f)"}


### Languages Used

* Java

