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
    boolean[][][] roi;
    
    public RegionOfInterest(BrainIndex brainIndex, String name)
    {
        this(brainIndex.getDim().x, brainIndex.getDim().y, brainIndex.getDim().z, name);
    }

    public RegionOfInterest(int xDim, int yDim, int zDim, String name)
    {
        this.xDim = xDim;
        this.yDim = yDim;
        this.zDim = zDim;
        this.name = name;
        roi = new boolean[xDim][yDim][zDim];
        for (int i = 0; i < xDim; i++)
        {
            for (int j = 0; j < yDim; j++)
            {
                for (int k = 0; k < zDim; k++)
                {
                    roi[i][j][k] = false;
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
                    roi[i][j][k] = mri.getVoxelAsBoolean(i, j, k);                    
                }
            }
        }
        computeFaces();
    }
    
    public void setVoxel(int i, int j, int k)
    {
        roi[i][j][k] = true;
    }
    
    public void clearVoxel(int i, int j, int k)
    {
        roi[i][j][k] = false;
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
                   if (roi[i][j][k])
                   {
                       // X Plane                       
                       if (i == 0 || !roi[i - 1][j][k])
                       {                           
                           faces.add(new Face3i(i, j, k, -1, Face3i.X_FACET));
                       }                       
                       if (i == xDim - 1 || !roi[i + 1][j][k])
                       {
                           faces.add(new Face3i(i + 1, j, k, 1, Face3i.X_FACET));
                       }
                       
                       // Y Plane                       
                       if (j == 0 || !roi[i][j - 1][k])
                       {                           
                           faces.add(new Face3i(i, j, k, -1, Face3i.Y_FACET));
                       }                       
                       if (j == yDim - 1 || !roi[i][j + 1][k])
                       {
                           faces.add(new Face3i(i, j + 1, k, 1, Face3i.Y_FACET));
                       }
                       
                       // Z Plane                       
                       if (k == 0 || !roi[i][j][k - 1])
                       {                           
                           faces.add(new Face3i(i, j, k, -1, Face3i.Z_FACET));
                       }                       
                       if (k == zDim - 1 || !roi[i][j][k + 1])
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
        return trackInteresections.size();
    }

    public void assignTracks(BrainIndex bi)
    {
        bi.assignTracks(this);
    }

    Map<Track, List<ROIIntersection>> trackInteresections = new HashMap<>();

    public void addIntersection(TrackIntersection trackIntersection)
    {
        List<ROIIntersection> intersectionList = trackInteresections.get(trackIntersection.track);
        if (intersectionList == null)
        {
            intersectionList = new ArrayList<>();
            trackInteresections.put(trackIntersection.track, intersectionList);
        }
        intersectionList.add(new ROIIntersection(this, trackIntersection.address));
    }

    public List<Track> commonTracks(RegionOfInterest target)
    {
        if (target == this)
        {
            throw new Error("source and target must be different");
        }
        return getTrackStream()
                .filter(sourceTrack -> target.getTrackStream()
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
        }
        );
        return partitionedTracks;
    }

    public List<ROIIntersection> getIntersectionAdresses(Track t)
    {
        List<ROIIntersection> intersectionList = trackInteresections.get(t);
        if (intersectionList == null)
        {
            return Collections.EMPTY_LIST;
        }
        else
        {
            return intersectionList;
        }
    }

    public Stream<Track> getTrackStream()
    {
        return trackInteresections.keySet().stream();
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
