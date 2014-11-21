/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import au.edu.uq.rcc.Face3i;
import au.edu.uq.rcc.MRISource;
import au.edu.uq.rcc.PartitionedTrack;
import au.edu.uq.rcc.Track;
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
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver
 */
public class RegionOfInterest_old
{
    
    private static final Logger log = LoggerFactory.getLogger(RegionOfInterest_old.class);

    // Use to check neighbours. low side < p < high side
    private final int NO_NEIGHBOURS = 0b001;
    private final int LOW_NEIGHBOUR = 0b011;
    private final int HIGH_NEIGHBOUR = 0b101;

    Tuple3i dim = null;
    int[] dimArray = new int[3];
    String name;
    BrainIndex brainIndex;
    Set<Face3i> faces = new HashSet<>();
    MRISource mri;

    boolean[][][] roi; // This will have a skirt to avoid boundary checks.
    
    public RegionOfInterest_old(BrainIndex brainIndex, String name)
    {
        this.name = name;
        this.brainIndex = brainIndex;
        this.dim = brainIndex.getDim();
        dim.get(dimArray);
        roi = new boolean[dim.x][dim.y][dim.z];
        for (int i = 0; i < dim.x; i++)
        {
            for (int j = 0; j < dim.y; j++)
            {
                for (int k = 0; k < dim.z; k++)
                {
                    roi[i][j][k] = false;
                }
            }
        }
    }

    public void setVoxel(int i, int j, int k)
    {
        setVoxel(new Point3i(i, j, k));
    }

    public void setVoxel(Tuple3i p)
    {
        if (roi[p.x][p.y][p.z] == true)
        {
            return;
        }
        roi[p.x][p.y][p.z] = true;
        computeFaces(Face3i.X_FACET, p);
        computeFaces(Face3i.Y_FACET, p);
        computeFaces(Face3i.Z_FACET, p);
    }

    public void setVoxel(MRISource mri)
    {
        this.mri = mri;
        setMRI(mri);
//        for (int i = 0; i < mri.xDim(); i++)
//        {
//            for (int j = 0; j < mri.yDim(); j++)
//            {
//                for (int k = 0; k < mri.zDim(); k++)
//                {
//                    if (mri.getVoxel(i, j, k) != 0)
//                    {
//                        setVoxel(i, j, k);
//                    }
//                }
//            }
//        }
    }

    public void setMRI(MRISource mri)
    {
        this.mri = mri;
        for (int i = 0; i < mri.xDim(); i++)
        {
            for (int j = 0; j < mri.yDim(); j++)
            {
                for (int k = 0; k < mri.zDim(); k++)
                {
                   if (mri.getVoxelAsBoolean(i, j, k))
                   {
                       // X Plane                       
                       if (i == 0 || mri.getVoxelAsBoolean(i - 1, j, k))
                       {                           
                           faces.add(new Face3i(i, j, k, -1, Face3i.X_FACET));
                       }
                       
                       if (i == mri.xDim() - 1 || mri.getVoxelAsBoolean(i + 1, j, k))
                       {
                           faces.add(new Face3i(i + 1, j, k, 1, Face3i.X_FACET));
                       }
                       
                       // Y Plane                       
                       if (j == 0 || mri.getVoxelAsBoolean(i, j - 1, k))
                       {                           
                           faces.add(new Face3i(i, j, k, -1, Face3i.Y_FACET));
                       }
                       
                       if (j == mri.yDim() - 1 || mri.getVoxelAsBoolean(i, j + 1, k))
                       {
                           faces.add(new Face3i(i, j + 1, k, 1, Face3i.Y_FACET));
                       }
                       
                       // Z Plane                       
                       if (k == 0 || mri.getVoxelAsBoolean(i, j, k - 1))
                       {                           
                           faces.add(new Face3i(i, j, k, -1, Face3i.Z_FACET));
                       }
                       
                       if (k == mri.zDim() - 1 || mri.getVoxelAsBoolean(i, j, k + 1))
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

    private void computeFaces(int facet, Tuple3i p)
    {
        Face3i lowFace = new Face3i(p, facet);
        lowFace.n = -1;
        Face3i highFace = lowFace.getParallelFace();
        highFace.n = 1;

        int neighbours = getNeighbours(facet, p);

        if ((neighbours & LOW_NEIGHBOUR) == LOW_NEIGHBOUR)
        {
            faces.remove(lowFace);
        } else
        {
            faces.add(lowFace);
        }

        if ((neighbours & HIGH_NEIGHBOUR) == HIGH_NEIGHBOUR)
        {
            faces.remove(highFace);
        } else
        {
            faces.add(highFace);
        }

    }

    // Check for neighbours.
    private int getNeighbours(int face, Tuple3i p)
    {
        try
        {

            int[] pa = new int[3];
            p.get(pa);
            int neighbours = NO_NEIGHBOURS;
            // Check low neighbour.
            if (pa[face] > 0)
            {
                pa[face] -= 1;
                if (roi[pa[0]][pa[1]][pa[2]] == true)
                {
                    neighbours |= LOW_NEIGHBOUR;
                }
                pa[face] += 1; // Put index back the way it was.
            }
            // Check high neighbour.
            if (pa[face] < dimArray[face])
            {
                pa[face] += 1;
                if (roi[pa[0]][pa[1]][pa[2]] == true)
                {
                    neighbours |= HIGH_NEIGHBOUR;
                }                
            }
            return neighbours;
        } catch (ArrayIndexOutOfBoundsException ex)
        {
            log.info(String.format("ROI %s threw index out of bounds in getNeighbours - ingoring", name));
            return NO_NEIGHBOURS;
        }

    }
    
    public int numberOfTracks()
    {
        // return preCompInteresection.values().stream().collect(Collectors.summingInt(l -> l.size()));
        return preCompInteresection.size();
    }

    
    Map<Track, List<ROIIntersection>> preCompInteresection = new HashMap<>();

    public List<Track> commonTracks(RegionOfInterest_old target)
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

    public List<PartitionedTrack> calculateSegments(RegionOfInterest_old targetROI)
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
        List<ROIIntersection> intersectionList = preCompInteresection.get(t);
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
        return preCompInteresection.keySet().stream();
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
