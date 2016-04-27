/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import au.edu.uq.rcc.index.SpaceIndex;
import java.io.File;
import java.util.List;
import org.slf4j.profiler.Profiler;
import utils.BMProperties;

/**
 *
 * @author oliver
 */
public class Foo
{

    private static final Profiler profiler = new Profiler("Brain Mapper - Interpolator");
    private static final File mriFile = BMProperties.getFile("mri-source");
    private static final File trackFile = BMProperties.getFile("track-source");
    private static final File roiDirectory = BMProperties.getFile("roi-directory");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        MRISource mri = new MRISource(mriFile);
        TrackCollection tc = new TrackCollection(trackFile, mri.getTransform());
        SpaceIndex spaceIndex = new SpaceIndex(tc, mri);
        calcStats(spaceIndex);
        profiler.stop();
        profiler.print();
    }
    
    
    private static void calcStats(SpaceIndex spaceIndex)
    {
        spaceIndex.getTrackStats();;
    }
    
    private static void calcIntersect(SpaceIndex spaceIndex)
    {
        
        ROISourceCollection roiSource = new ROISourceCollection(roiDirectory, null);
        roiSource.getROIList().forEach((RegionOfInterest r) ->
        {
            profiler.start(String.format("ROI: %s", r.getName()));
            List<Track> tracks = spaceIndex.getTracks(r);
            System.out.printf("ROI %s has %d tracks\n", r.getName(), tracks.size());            
        });
    }

}
