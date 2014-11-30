/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import au.edu.uq.rcc.index.BrainIndex;
import java.io.File;
import java.util.List;
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

    private static final Profiler profiler = new Profiler("Brain Mapper");    
    private static final File mriFile = BMProperties.getFile("mri-source");
    private static final File trackFile = BMProperties.getFile("track-source");
    private static final File roiDirectory = BMProperties.getFile("roi-directory");
    private static final File outputDirectory = BMProperties.getFile("track-destination");    
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
        
        if (doMap)
        {
            profiler.start("build ROI collections");
            MRISourceCollection roiCollection = new MRISourceCollection(roiDirectory, brainIndex);
            roiCollection.doMap(outputDirectory);
        }
        
        profiler.stop();
        profiler.print();
    }

    
}
