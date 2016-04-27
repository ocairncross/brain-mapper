/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import au.edu.uq.rcc.index.BrainIndex;
import java.io.File;
import org.slf4j.profiler.Profiler;
import utils.BMProperties;
import utils.Utils;

/**
 *
 * @author oliver
 */
public class BrainMapper
{

    private static final Profiler profiler = new Profiler("Brain Mapper");
    private static final File mriFile = BMProperties.getFile("mri-source");
    private static final File trackFile = BMProperties.getFile("track-source");
    private static final File roiDirectory = BMProperties.getFile("roi-directory");
    private static final File outputDirectory = BMProperties.getFile("track-destination");    
    private static final boolean doMap = BMProperties.getBoolean("do-map");
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Utils.logInitial();
        profiler.start("load tracks");
        MRISource mri = new MRISource(mriFile);
        Transform transform = new Transform(mri.getHeader());
        TrackCollection trackCollection = new TrackCollection(trackFile, transform);
        profiler.start("build index");
        BrainIndex brainIndex = new BrainIndex(trackCollection, mri);
        
        if (doMap)
        {
            profiler.start("build ROI collections");
            ROISourceCollection roiCollection = new ROISourceCollection(roiDirectory, brainIndex);
            roiCollection.doMap(outputDirectory, mri);
        }
        
        profiler.stop();
        profiler.print();
    }

    
}
