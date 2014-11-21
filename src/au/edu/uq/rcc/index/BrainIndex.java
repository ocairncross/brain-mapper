/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc.index;

import au.edu.uq.rcc.Face3i;
import au.edu.uq.rcc.MRISource;
import au.edu.uq.rcc.RegionOfInterest;
import au.edu.uq.rcc.Track;
import au.edu.uq.rcc.TrackCollection;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.BoundingBox;

/**
 *
 * @author oliver
 */
public class BrainIndex
{

    private static final Logger log = LoggerFactory.getLogger(BrainIndex.class);
    
    
    private Tuple3i dim = null;
    private BoundingBox boundingBox;
    
    ArrayList<TrackIntersection>[][][] xPlane;  // x y z
    ArrayList<TrackIntersection>[][][] yPlane;
    ArrayList<TrackIntersection>[][][] zPlane;
    
    public BrainIndex(TrackCollection tracks, MRISource mri)
    {
        
        long tStart = System.currentTimeMillis();
        dim = new Point3i(mri.xDim(), mri.yDim(), mri.zDim());
        this.boundingBox = tracks.getBoundingBox();
        xPlane = initialisePlane(dim);
        yPlane = initialisePlane(dim);
        zPlane = initialisePlane(dim);        
        analyseTracks(tracks);
        long eTime = (System.currentTimeMillis() - tStart);        
        log.info(String.format("indexed %,d tracks in %,dms", tracks.getTrackList().size(), eTime));
    }
    
    public Tuple3i getDim()
    {
        return dim;
    }
    
    public BoundingBox getBoundingBox()
    {
        return boundingBox;
    }

    // initialise index with number of voxels.
    private ArrayList<TrackIntersection>[][][] initialisePlane(Tuple3i d)
    {        
        ArrayList<TrackIntersection>[][][] planeIndex = new ArrayList[d.x + 1][d.y + 1][d.z + 1];
        for (int i = 0; i < d.x + 1; i++)
        {
            for (int j = 0; j < d.y + 1; j++)
            {
                for (int k = 0; k < d.z + 1; k++)
                {
                    planeIndex[i][j][k] = new ArrayList<>();         
                }
            }
        }
        return planeIndex;
    }

    private void analyseTracks(TrackCollection tl)
    {
        for (Track t : tl.getTrackList())
        {
            insertTrack(t);
        }
    }

    private void insertTrack(Track t)
    {
        Tuple3d p0 = new Point3d();
        Tuple3d p1 = new Point3d();
        Tuple3i i0 = new Point3i();
        Tuple3i i1 = new Point3i();
        Tuple3i diff = new Point3i();

        Tuple3d basePoint = boundingBox.getBasePoint();
        Tuple3d interval = boundingBox.getIntervalLength(dim);

        List<Tuple3d> vertices = t.getVertices();        
        // Determine where intervals are spanned by a track fragment
        // this is a face.
        for (int i = 0; i < vertices.size() - 1; i++)
        {
            p0.sub(vertices.get(i), basePoint);
            p1.sub(vertices.get(i + 1), basePoint);
            setIndex(i0, p0, interval);
            setIndex(i1, p1, interval);
            diff.sub(i1, i0);
            if (diff.x != 0)
            {
                xPlane[i1.x][i1.y][i1.z].add(new TrackIntersection(t, i));
            }
            if (diff.y != 0)
            {
                yPlane[i1.x][i1.y][i1.z].add(new TrackIntersection(t, i));
            }
            if (diff.z != 0)
            {
                zPlane[i1.x][i1.y][i1.z].add(new TrackIntersection(t, i));
            }
        }
    }


    private void setIndex(Tuple3i index, Tuple3d p, Tuple3d interval)
    {
        index.set((int) (p.x / interval.x), (int) (p.y / interval.y), (int) (p.z / interval.z));
    }
    
    public ArrayList<TrackIntersection> getTrackIntersections(Face3i face)
    {   
        if (face.w == Face3i.X_FACET)
        {
            return xPlane[face.x][face.y][face.z];
        }
        else if (face.w == Face3i.Y_FACET)
        {
            return yPlane[face.x][face.y][face.z];
        }
        else
        {
            return zPlane[face.x][face.y][face.z];
        }        
    }
    
    public ArrayList<TrackIntersection>[][][] getXPlane()
    {
        return xPlane;
    }
    
    public ArrayList<TrackIntersection>[][][] getYPlane()
    {
        return yPlane;
    }
    
    public ArrayList<TrackIntersection>[][][] getZPlane()
    {
        return zPlane;
    }
    
}
