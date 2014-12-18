/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import java.io.File;
import java.util.DoubleSummaryStatistics;
import java.util.Random;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import utils.BMProperties;

/**
 *
 * @author oliver
 */
public class Foo
{

    private static final File mriFile = BMProperties.getFile("mri-source");
    private static final File trackFile = BMProperties.getFile("track-source");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
//        TrackCollection tc = new TrackCollection(trackFile);
//        MRISource mri = new MRISource(mriFile);
//        DoubleSummaryStatistics summaryStatistics = tc.getTrackList().stream().flatMapToDouble(t -> 
//        {
//            return t.getVertices().stream().mapToDouble(v -> 
//            {
//                Tuple3d o = new Point3d(v);
//                Tuple3d oo = new Point3d(o);
//                Tuple3d ut = mri.undoTransform(o);
//                Tuple3d at = mri.applyTransform(ut);
//                
//                Vector3d vec0 = new Vector3d(v);
//                Vector3d vec1 = new Vector3d(mri.applyTransform(v));
//                Vector3d vec2 = new Vector3d(mri.undoTransform(vec1));                
//                vec0.sub(vec2);
//                return vec0.length();
//                
//            });
//        }).summaryStatistics();

        IntStream
                .range(0, 100)
                .map(i -> i * 3)
                .parallel()
                .forEach(i -> System.out.printf("%03d : %d %s\n", i, Thread.currentThread().getId(), Thread.currentThread().getName()));

    }
    
    private static class IntSkipSupplier implements IntSupplier
    {

        int inc;
        int counter;

        public IntSkipSupplier(int inc)
        {
            this.inc = inc;
            counter = -inc;
        }
        
        @Override
        public int getAsInt()
        {
            return counter += inc;
        }

    }
    

}
