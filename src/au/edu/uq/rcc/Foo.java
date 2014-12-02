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

/**
 *
 * @author oliver
 */
public class Foo
{

    private static final File trackFile = BMProperties.getFile("track-source");
    private static final File mriFile = BMProperties.getFile("mri-source");
    private static final Profiler profiler = new Profiler("Brain Mapper");
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
       profiler.start("load tracks");
       BrainIndex brainIndex = new BrainIndex(new MRISource(mriFile));
       TrackCollection tc = new TrackCollection(trackFile, brainIndex); 
       profiler.stop();
       profiler.print();
    }

}
