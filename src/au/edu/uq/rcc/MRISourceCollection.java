/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import au.edu.uq.rcc.index.BrainIndex;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver
 */
public class MRISourceCollection
{
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MRISourceCollection.class);
    File sourceDirectory;
    List<RegionOfInterest> roiList = new ArrayList<>();

    public MRISourceCollection(File sourceDirectory, BrainIndex index)
    {
        try
        {
            log.info("loading roi's");
            this.sourceDirectory = sourceDirectory;
            Files.list(sourceDirectory.toPath())
                    .forEach(f -> 
                    {
                        long tStart = System.currentTimeMillis();
                        MRISource mask = new MRISource(f.toFile());
                        String fileName = f.getFileName().toString();
                        RegionOfInterest roi = new RegionOfInterest(index, fileName);                        
                        roi.assignTracks(index);
                        roiList.add(roi);
                        long tElapsed = System.currentTimeMillis() - tStart;
                        log.info("created ROI {}  with {} tracks in {}ms", fileName, roi.numberOfTracks(), tElapsed);
                    });
            
        } 
        catch (IOException ex)
        {
            Logger.getLogger(MRISourceCollection.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }
    
    public void doMap()
    {
        Runtime runtime = Runtime.getRuntime();
        List<RegionOfInterest> mapList = new ArrayList<>(roiList);
        log.info("mappings [roi -> roi, common segments, time (ms), free ram]");
        while (!mapList.isEmpty())
        {
            RegionOfInterest sourceROI = mapList.remove(0);            
            mapList.stream().forEach(t -> 
                {
                    long tStart = System.currentTimeMillis();
                    List<PartitionedTrack> segments = sourceROI.calculateSegments(t);
                    long eTime = System.currentTimeMillis() - tStart;
                    String logString = String.format("%s -> %s,%d,%d,%d", sourceROI.getName(), t.getName(), segments.size(), eTime, runtime.freeMemory());
                    log.info(logString);
                });
        }
    }
    
}

    
