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
import utils.Utils;

/**
 *
 * @author oliver
 */
public class BrainMapper
{

    private static final Logger log = LoggerFactory.getLogger(BrainMapper.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {

        // Utils.printLoggerInfo();
        Profiler profiler = new Profiler("Brain Mapper");

        BMProperties props = new BMProperties();

        File root = new File(props.get("base-directory"));
        File mriFile = new File(root, props.get("mri-source"));
        File trackFile = new File(root, props.get("track-source"));
        File roiDirectory = new File(root, props.get("roi-directory"));
        int scaleFactor = Integer.parseInt(props.get("scale-factor"));
        boolean doMap = Boolean.parseBoolean(props.get("do-map"));

        logInitial();

        profiler.start("load tracks");
        TrackCollection tl = new TrackCollection(trackFile);
        tl.scaleUp(scaleFactor);
        MRISource mri = new MRISource(mriFile);
        profiler.start("build index");
        BrainIndex brainIndex = new BrainIndex(tl, mri);
        
        MRISourceCollection.testFaceCount(roiDirectory, brainIndex);

//        if (doMap)
//        {
//            profiler.start("build ROI collections");
//            MRISourceCollection roiCollection = new MRISourceCollection(roiDirectory, brainIndex);
//            roiCollection.doMap();
//        }
        
        profiler.stop();
        profiler.print();
    }

    private static RegionOfInterest makeROI(String name, int x, int y, int z, int s, BrainIndex bi)
    {
        RegionOfInterest roi = new RegionOfInterest(bi, name);
        for (int i = 0; i < s; i++)
        {
            for (int j = 0; j < s; j++)
            {
                for (int k = 0; k < s; k++)
                {
                    roi.setVoxel(i + x, j + y, k + z);
                }
            }
        }
        roi.assignTracks();
        return roi;
    }

    private static void logInitial()
    {
        Utils.printClassPath();
        Runtime r = Runtime.getRuntime();
        log.info(String.format("Executing with %s processors in thread pool", r.availableProcessors()));
        log.info("Total memory {}", r.totalMemory());
        log.info("Free memory {}", r.freeMemory());
    }

}
