/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import au.edu.uq.rcc.index.BrainIndex;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import utils.BMProperties;
import utils.BoundingBox;
import utils.Utils;

/**
 *
 * @author oliver
 */
public class BrainMapper
{

    private static final Profiler profiler = new Profiler("Brain Mapper");
    private static final Logger log = LoggerFactory.getLogger(BrainMapper.class);    
    private static final File mriFile = BMProperties.getFile("mri-source");
    private static final File trackFile = BMProperties.getFile("track-source");
    private static final File roiDirectory = BMProperties.getFile("roi-directory");
    private static final int scaleFactor = BMProperties.getInt("scale-factor");
    private static final boolean doMap = BMProperties.getBoolean("do-map");
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Utils.logInitial();
        profiler.start("load tracks");
        TrackCollection tl = new TrackCollection(trackFile);
        tl.scaleUp(scaleFactor);
        MRISource mri = new MRISource(mriFile);
        profiler.start("build index");
        BrainIndex brainIndex = new BrainIndex(tl, mri);
        BoundingBox transformedBB = brainIndex.getTransformedBB(tl);
        System.out.printf("BB:\n %s\n", transformedBB);
        
        /*        
        RegionOfInterest testROI = new RegionOfInterest(brainIndex, "testROI");
        Utils.setMask(30, 50, 10, 10, testROI);        
        testROI.computeFaces();
        testROI.assignTracks(brainIndex);
        long count = testROI.getTracks().size();
        
        System.out.printf("faces = %d tracks = %d\n", testROI.getFaces().size(), count);

        if (doMap)
        {
            profiler.start("build ROI collections");
            MRISourceCollection roiCollection = new MRISourceCollection(roiDirectory, brainIndex);
            roiCollection.doMap();
        }
        */
        
        profiler.stop();
        profiler.print();
    }

    
}
