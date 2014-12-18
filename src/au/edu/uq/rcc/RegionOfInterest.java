/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import au.edu.uq.rcc.index.BrainIndex;
import au.edu.uq.rcc.index.TrackIntersection;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.MinMax;

/**
 *
 * @author oliver
 */
public class RegionOfInterest
{

    public static enum Selection{AND, NOT, NONE};
    private static final Logger log = LoggerFactory.getLogger(RegionOfInterest.class);

    private String name;
    private Tuple3i dim;
    private final Set<Face3i> faces = new HashSet<>();
    private boolean[][][] roiMask;
    private final Map<Track, List<Integer>> trackIntersections;
    private final ObjectProperty<Selection> selectionProp = new SimpleObjectProperty<>();

    public RegionOfInterest(BrainIndex brainIndex, String name)
    {
        this(brainIndex.getDimentions(), name);
    }

    public RegionOfInterest(MRISource mriMask, String name)
    {
        this(mriMask.getDimensions(), name);
        setMRIMask(mriMask);
    }

    public RegionOfInterest(Tuple3i dim, String name)
    {
        trackIntersections = new HashMap<>();
        selectionProp.set(Selection.NONE);
        this.dim = dim;
        this.name = name;
        roiMask = new boolean[dim.x][dim.y][dim.z];
        for (int i = 0; i < dim.x; i++)
        {
            for (int j = 0; j < dim.y; j++)
            {
                Arrays.fill(roiMask[i][j], false);
            }
        }
    }
    
    public ObjectProperty<Selection> getSelectionProperty()
    {
        return selectionProp;
    }

    public Tuple3i getDimensions()
    {
        return dim;
    }

    public boolean[][][] getRoiMask()
    {
        return roiMask;
    }

    public final void setMRIMask(MRISource mri)
    {
        Tuple3i roiDim = mri.getDimensions();
        for (int i = 0; i < roiDim.x; i++)
        {
            for (int j = 0; j < roiDim.y; j++)
            {
                for (int k = 0; k < roiDim.z; k++)
                {
                    roiMask[i][j][k] = mri.getVoxelAsBoolean(i, j, k);
                }
            }
        }
        computeFaces();
    }

    public void setVoxel(int i, int j, int k)
    {
        roiMask[i][j][k] = true;
    }

    public void clearVoxel(int i, int j, int k)
    {
        roiMask[i][j][k] = false;
    }

    public void computeFaces()
    {
        faces.clear();
        for (int i = 0; i < dim.x; i++)
        {
            for (int j = 0; j < dim.y; j++)
            {
                for (int k = 0; k < dim.z; k++)
                {
                    if (roiMask[i][j][k])
                    {
                        // X Plane                       
                        if (i == 0 || !roiMask[i - 1][j][k])
                        {
                            faces.add(new Face3i(i, j, k, -1, Face3i.X_FACET));
                        }
                        if (i == dim.x - 1 || !roiMask[i + 1][j][k])
                        {
                            faces.add(new Face3i(i + 1, j, k, 1, Face3i.X_FACET));
                        }

                        // Y Plane                       
                        if (j == 0 || !roiMask[i][j - 1][k])
                        {
                            faces.add(new Face3i(i, j, k, -1, Face3i.Y_FACET));
                        }
                        if (j == dim.y - 1 || !roiMask[i][j + 1][k])
                        {
                            faces.add(new Face3i(i, j + 1, k, 1, Face3i.Y_FACET));
                        }

                        // Z Plane                       
                        if (k == 0 || !roiMask[i][j][k - 1])
                        {
                            faces.add(new Face3i(i, j, k, -1, Face3i.Z_FACET));
                        }
                        if (k == dim.z - 1 || !roiMask[i][j][k + 1])
                        {
                            faces.add(new Face3i(i, j, k + 1, 1, Face3i.Z_FACET));
                        }
                    }
                }
            }
        }
    }

    public Map<Track, List<Integer>> getTrackIntersection()
    {
        return trackIntersections;
    }

    public List<Integer> getIntersections(Track t)
    {
        return trackIntersections.get(t);
    }

    public String getName()
    {
        return name;
    }

    public Set<Face3i> getFaces()
    {
        return faces;
    }

    public int numberOfTracks()
    {
        return trackIntersections.size();
    }

    public void assignTracks(BrainIndex bi)
    {
        faces.stream().forEach((Face3i f) ->
        {
            bi.getTrackIntersections(f).stream().forEach((TrackIntersection ti) ->
            {
                List<Integer> intersctions = trackIntersections.get(ti.track);
                if (intersctions == null)
                {
                    intersctions = new ArrayList<>();
                    trackIntersections.put(ti.track, intersctions);
                }
                intersctions.add(ti.address);
            }
            );
        });
    }
    
    public void match(RegionOfInterest targetROI)
    {
        
    }

    public List<PartitionedTrack> getPartitionedTracks(RegionOfInterest targetROI)
    {
        List<PartitionedTrack> partitionedTracks = new ArrayList<>();
        trackIntersections.keySet().stream().forEach((Track st) ->
        {            
            if (targetROI.getTrackIntersection().keySet().contains(st))
            {             
                PartitionedTrack partitionedTrack = new PartitionedTrack(st);
                partitionedTrack.addIntersections(this);
                partitionedTrack.addIntersections(targetROI);
                partitionedTracks.add(partitionedTrack);
            }
        });
        return partitionedTracks;
    }
    
    public List<Track> getCommonTracks(RegionOfInterest targetROI)
    {
        List<Track> commonTrak = new ArrayList<>();
        trackIntersections.keySet().stream().forEach((Track st) ->
        {
                if (targetROI.getTrackIntersection().keySet().contains(st))
                {
                    commonTrak.add(st);
                }
        });
        return commonTrak;
    }

    public Set<Track> getTracks()
    {
        return trackIntersections.keySet();
    }

    public List<Track> getCloseTracks(int extension)
    {
        List<Track> closeTracks = new ArrayList<>();
        for (Track t : getTracks())
        {
            MinMax interval = new MinMax(0, t.numberOfVertices());            
            Integer i = trackIntersections.get(t).get(0);
            interval.setVal(i + extension);
            interval.setVal(i - extension);
            Track closeTrack = t.getSegment((int) interval.getMin(), (int) interval.getMax());
            closeTracks.add(closeTrack);
        }
        return closeTracks;
    }

    @Override
    public String toString()
    {
        return name;
    }
    
    public void manualProcessTracks(List<Track> tl, MRISource mri)
    {
        tl.forEach(t -> 
        {
            trackInside(t, mri);
            if (t.isSelected())
            {
                insideTracks.add(t);
            }
            else
            {
                outsideTracks.add(t);
            }
        });    
    }

    public List<Track> getInsideTracks()
    {
        return insideTracks;
    }

    public List<Track> getOutsideTracks()
    {
        return outsideTracks;
    }
    
    private List<Track> insideTracks = new ArrayList<>();
    private List<Track> outsideTracks  = new ArrayList<>();
    
    private void trackInside(Track track, MRISource mriSource)
    {
        
        IntStream.range(0, track.numberOfVertices())
                .parallel()
                .forEach((int i) -> 
                {                    
                    track.setSelected(false);
                    Tuple3d v = track.getVertices().get(i);                   
                    int x = (int) v.x;
                    int y = (int) v.y;
                    int z = (int) v.z;                    
                    if(roiMask[x][y][z])
                    {
                        // System.out.printf("%d : %d : %d\n", track.numberOfVertices(), track.getVertexColor().size(), i);
                        track.setColor(i, Color.red);
                        track.setSelected(true);              
                    }                    
                });   
    }
    

}
