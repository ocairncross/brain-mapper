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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import utils.TrackWriter;

/**
 *
 * @author oliver
 */
public class MRISourceCollection
{
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MRISourceCollection.class);    
    private List<RegionOfInterest> roiList = new ArrayList<>();

    public MRISourceCollection(File sourceDirectory, BrainIndex index)
    {
        try
        {
            log.info("loading roi's");            
            Files.list(sourceDirectory.toPath())
                    .forEach(f -> 
                    {
                        long tStart = System.currentTimeMillis();
                        MRISource mask = new MRISource(f.toFile());
                        String fileName = f.getFileName().toString();
                        RegionOfInterest roi = new RegionOfInterest(mask, fileName);
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
    
    public List<RegionOfInterest> getROIList()
    {
        return roiList;
    }
    
    public void doMap(File baseDirectory)
    {
        Runtime runtime = Runtime.getRuntime();
        List<RegionOfInterest> mapList = new ArrayList<>(roiList);
        log.info("mappings [roi -> roi, common segments, time (ms), free ram]");
        while (!mapList.isEmpty())
        {
            RegionOfInterest sourceROI = mapList.remove(0);       
            mapList.parallelStream().forEach(t -> 
                {
                    long tStart = System.currentTimeMillis();
                    List<PartitionedTrack> segments = sourceROI.getPartitionedTracks(t);                    
                    File trackFileName = new File(baseDirectory, 
                            String.format("%s-%s.tck", sourceROI.getName().replace(".nii", ""), t.getName().replace(".nii","")));
                    TrackWriter tw = new TrackWriter(trackFileName, segments);
                    long eTime = System.currentTimeMillis() - tStart;
                    String logString = String.format("%s -> %s,%d,%d,%d", sourceROI.getName(), t.getName(), segments.size(), eTime, runtime.freeMemory());
                    log.info(logString);
                    System.out.printf("wrote %s\n", trackFileName.getAbsolutePath());
                });
        }
    }
    
}

    
