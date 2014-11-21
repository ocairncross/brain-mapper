/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import au.edu.uq.rcc.index.BrainIndex;
import au.edu.uq.rcc.index.ROIIntersection;
import au.edu.uq.rcc.index.TrackIntersection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver
 */
public class RegionOfInterest
{
    
    private static final Logger log = LoggerFactory.getLogger(RegionOfInterest.class);

    private String name;
    private int xDim;
    private int yDim;
    private int zDim;
    private final Set<Face3i> faces = new HashSet<>();
    private boolean[][][] roiMask;
    private final Map<Track, List<Integer>> trackIntersections;
    
    public RegionOfInterest(BrainIndex brainIndex, String name)
    {
        this(brainIndex.getDim().x, brainIndex.getDim().y, brainIndex.getDim().z, name);
    }

    public RegionOfInterest(int xDim, int yDim, int zDim, String name)
    {
        trackIntersections = new HashMap<>();
        this.xDim = xDim;
        this.yDim = yDim;
        this.zDim = zDim;
        this.name = name;
        roiMask = new boolean[xDim][yDim][zDim];
        for (int i = 0; i < xDim; i++)
        {
            for (int j = 0; j < yDim; j++)
            {
                for (int k = 0; k < zDim; k++)
                {
                    roiMask[i][j][k] = false;
                }
            }
        }
    }
    
    public void setMRIMask(MRISource mri)
    {        
        for (int i = 0; i < mri.xDim(); i++)
        {
            for (int j = 0; j < mri.yDim(); j++)
            {
                for (int k = 0; k < mri.zDim(); k++)
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
        for (int i = 0; i < xDim; i++)
        {
            for (int j = 0; j < yDim; j++)
            {
                for (int k = 0; k < zDim; k++)
                {
                   if (roiMask[i][j][k])
                   {
                       // X Plane                       
                       if (i == 0 || !roiMask[i - 1][j][k])
                       {                           
                           faces.add(new Face3i(i, j, k, -1, Face3i.X_FACET));
                       }                       
                       if (i == xDim - 1 || !roiMask[i + 1][j][k])
                       {
                           faces.add(new Face3i(i + 1, j, k, 1, Face3i.X_FACET));
                       }
                       
                       // Y Plane                       
                       if (j == 0 || !roiMask[i][j - 1][k])
                       {                           
                           faces.add(new Face3i(i, j, k, -1, Face3i.Y_FACET));
                       }                       
                       if (j == yDim - 1 || !roiMask[i][j + 1][k])
                       {
                           faces.add(new Face3i(i, j + 1, k, 1, Face3i.Y_FACET));
                       }
                       
                       // Z Plane                       
                       if (k == 0 || !roiMask[i][j][k - 1])
                       {                           
                           faces.add(new Face3i(i, j, k, -1, Face3i.Z_FACET));
                       }                       
                       if (k == zDim - 1 || !roiMask[i][j][k + 1])
                       {
                           faces.add(new Face3i(i, j, k + 1, 1, Face3i.Z_FACET));
                       }                       
                   }                    
                }
            }
        }
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

    public List<Track> commonTracks(RegionOfInterest target)
    {
        if (target == this)
        {
            throw new Error("source and target must be different");
        }
        
        return getTrackStream()
                .filter(sourceTrack -> target
                    .getTrackStream()
                    .anyMatch(targettrack -> targettrack == sourceTrack))
                .collect(Collectors.toList());
    }

    public List<PartitionedTrack> calculateSegments(RegionOfInterest targetROI)
    {
        List<PartitionedTrack> partitionedTracks = new ArrayList<>();
        getTrackStream().forEach(sourceTrack ->
        {
            List<ROIIntersection> interesctions = targetROI.getIntersectionAdresses(sourceTrack);
            if (!interesctions.isEmpty())
            {
                interesctions.addAll(getIntersectionAdresses(sourceTrack));
                partitionedTracks.add(new PartitionedTrack(sourceTrack, interesctions));
            }
        });
        return partitionedTracks;
    }

    public Stream<Track> getTrackStream()
    {
        return trackIntersections.keySet().stream();
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static void printTrackIntersections(List<PartitionedTrack> trackList)
    {
        trackList.stream().forEach(System.out::println);
    }

}
