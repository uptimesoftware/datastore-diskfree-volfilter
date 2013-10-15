/*
 * MonitorESXDatastoreCapacity.java
 *
 * Created on Sep 29, 2008
 *
 * The purpose of this extension is to allow users to threshold capacities in terms of disk available scaled to megabytes
 * 
 */

package com.uptimesoftware.uptime.erdc;
import com.uptimesoftware.uptime.erdc.baseclass.*;
import com.uptimesoftware.uptime.erdc.perfcheck.MonitorPerformanceCheckDataRetriever;
import com.uptimesoftware.uptime.erdc.performance.statistics.*;
import com.uptimesoftware.uptime.base.entity.UptimeHostIdByHostnameLoader;
import com.uptimesoftware.uptime.base.entity.configuration.MemorySizeByHostLoader;
import com.uptimesoftware.uptime.base.util.Parameters;
import java.util.*;
import java.io.*;


/**
 *
 * @author Kenneth Cheung [ken.cheung@uptimesoftware.com]
 */
public class MonitorDatastoreCapacityAvailable extends MonitorWithMonitorVariables {
    
    /*
    private String hostname         = "";
    private Double freeMem          = null;
    private Double freeSwap         = null;
    private Double freeMemPercent     = null;
    private Long physicalMem        = null;
    */
    
    private String volumeString = null;
    //ArrayList<String> volumeArray = new ArrayList<String>();
    String[] volumeStringArray = null; //limited to a max of 49 volumes SERIOUSLY
    
    /** Creates a new instance of MonitorPerformance */
    public MonitorDatastoreCapacityAvailable() {
    }
    
    @Override
    public void setParameters(Parameters params, Long instanceId) {
        super.setParameters(params, instanceId);
        
        try {
            volumeString = parameters.getStringParameter("volumes");
            
        }  catch (Exception e) {
            setMessage("Monitor failed in getting parameters\r" + e.getMessage());
        }
        
    }
    
    @Override
    protected void monitor(){

        try {
            
            setState(ErdcTransientState.OK);
            
            MemorySizeByHostLoader memoryLoader = new MemorySizeByHostLoader();
            UptimeHostIdByHostnameLoader idLoader = new UptimeHostIdByHostnameLoader();            
            MonitorPerformanceCheckDataRetriever retriever = new MonitorPerformanceCheckDataRetriever();
            
            boolean filters = false;
            // split the list of volumes entered, and remove any/all spaces
            if (volumeString != null && volumeString.trim().length() > 0) {
                volumeStringArray = volumeString.replaceAll("\\s+", "").split(",");
                filters = true;
            }

            idLoader.setHostname(getHostname());                        
            Long hostId = idLoader.execute();
            memoryLoader.setHostId(hostId);
            
            // Author: Joel Pereira
            // May 1, 2012
            // - updating with changes from v5 -> v6+
            // v5
            //DataSample dataSample = retriever.getMostRecentDataSample(getHostname());
            // v6
            retriever.setHostname(getHostname());
            DataSample dataSample = (DataSample)retriever.getDataSampleListInMinuteRange(15);
            
            
            
            List<FilesystemCapacity> myDiskList = dataSample.getFilesystems();
            /* Debug code leave in for testing
            BufferedWriter logger = new BufferedWriter(new FileWriter("c:\\MonitorPerformance.txt"));
            logger.write("Running! ------   ");
            logger.write("The collection has " + myDiskList.size() + " objects");
            */ 

            //ListIterator li = myDiskList.listIterator();

            //declare some variables to get this show started
            //double worstFsCapacity = 0;
            double thisFsCap = 0;
            double worstFsCapacityAvailable = 999999999;
            String worstFileSystemName = null;
            int isDesiredVolume=0; //this is a flag

            //Forward direction traversal of all elemints in the list
            //while(li.hasNext()){
            for (int i = 0; i < myDiskList.size(); i++) {
                //FilesystemCapacity myFSCAP = (FilesystemCapacity) li.next();
                FilesystemCapacity myFSCAP = myDiskList.get(i);

                /* Debug code 
                logger.write(myFSCAP.getFilesystem()+ " " +myFSCAP.getPercentUsed().toString() + "% ");
                */  

                thisFsCap = Double.parseDouble(myFSCAP.getSpaceAvail().toString())/1024;

                //check if this filesystem is actually one of the filesystems we want to check

                isDesiredVolume=0;
                if (filters) {
                    for (int counterX = 0; counterX < volumeStringArray.length; counterX++ ) {
                        if (myFSCAP.getFilesystem().toLowerCase().contains(volumeStringArray[counterX].toLowerCase())){
                            isDesiredVolume = 1;
                        }
                    }
                }
                else {
                    // since no filters were added, let's just go through all disks
                    isDesiredVolume = 1;
                }

                //myFSCAP.getFilesystem().toString()
                if (worstFsCapacityAvailable > thisFsCap && isDesiredVolume == 1){ 
                //worstFsCapacity = myFSCAP.getPercentUsed();
                worstFileSystemName = myFSCAP.getFilesystem();
                worstFsCapacityAvailable = myFSCAP.getSpaceAvail()/1024; //convert kilobytes to megabytes
                }
            }
            addVariable("worstOffenderFSCapacity", worstFsCapacityAvailable);
            setMessage("The most filled filesystem is: " + worstFileSystemName + " with a capacity of " + worstFsCapacityAvailable + " megaBytes available." );
            //setState(ErdcTransientState.TBD);
            
            /* Test Output For Debugging
            logger.write("\nWorst FS:" + worstFileSystemName + "Worst FS Capacity: " + worstFsCapacity );
            logger.close();
            */
        } catch(Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            setMessage("An error occured while executing the monitor\n" + exceptionAsString);
            setState(ErdcTransientState.CRIT);
        }
    }
}