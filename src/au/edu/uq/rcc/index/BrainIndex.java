/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc.index;

import au.edu.uq.rcc.Face3i;
import au.edu.uq.rcc.MRISource;
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
 * @author oliver
 */
public class BrainIndex
{

    private static final Logger log = LoggerFactory.getLogger(BrainIndex.class);
        
    private Tuple3i dim = null;    
    private final MRISource mriSource; // Needed to get transform from trackspace to MRISpace.
    
    ArrayList<TrackIntersection>[][][] xPlane;  // x y z
    ArrayList<TrackIntersection>[][][] yPlane;
    ArrayList<TrackIntersection>[][][] zPlane;
    
    public BrainIndex(TrackCollection tracks, MRISource mriSource)
    {   
        this(mriSource);
        long tStart = System.currentTimeMillis();        
        loadTracks(tracks);
        long eTime = (System.currentTimeMillis() - tStart);        
        log.info(String.format("indexed %,d tracks in %,dms", tracks.getTrackList().size(), eTime));
    }
    
    public BrainIndex(MRISource mriSource)
    {   
        this.dim = mriSource.getDimensions();
        this.mriSource = mriSource;        
        xPlane = initialisePlane(dim);
        yPlane = initialisePlane(dim);
        zPlane = initialisePlane(dim);             
    }
    
    public Tuple3i getDimentions()
    {
        return dim;
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

    private void loadTracks(TrackCollection tl)
    {
        for (Track t : tl.getTrackList())
        {
            insertTrack(t);
        }
    }

    public void insertTrack(Track t)
    {
        Tuple3d p0 = new Point3d();
        Tuple3d p1 = new Point3d();
        Tuple3i i0 = new Point3i();
        Tuple3i i1 = new Point3i();
        Tuple3i diff = new Point3i();

        Tuple3d interval = new Point3d(1.0,1.0,1.0);

        List<Tuple3d> vertices = t.getVertices();
        // Determine where intervals are spanned by a track fragment
        // this is a face.
        for (int i = 0; i < vertices.size() - 1; i++)
        {
            p0 = new Point3d(vertices.get(i));
            p1 = new Point3d(vertices.get(i + 1));            
            p0 = mriSource.undoTransform(p0);
            p1 = mriSource.undoTransform(p1);            
            setIndex(i0, p0, interval);
            setIndex(i1, p1, interval);
            diff.sub(i1, i0);
            checkDiff(diff, p0, p1);
            
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
    
    private void checkDiff(Tuple3i d, Tuple3d p1, Tuple3d p2)
    {
        if (Math.abs(d.x) > 1 || Math.abs(d.y) > 1 || Math.abs(d.z) > 1)
        {
            System.out.printf("Diff %s is bad. %s -> %s", d.toString(), p1.toString(), p2.toString());
            
            throw new Error("bad index calculation");
        }
    }
    
    public BoundingBox getTransformedBB(TrackCollection tl)
    {
        BoundingBox bb = new BoundingBox();
        tl.getTrackList()
                .stream()
                .forEach(t -> {t.getVertices()
                        .stream()
                        .forEach(v -> bb.add(mriSource.undoTransform(v)));
                  });                
        return bb;
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
