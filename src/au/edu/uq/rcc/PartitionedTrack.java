/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import au.edu.uq.rcc.index.ROIIntersection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author oliver
 */
public class PartitionedTrack
{
    private final Track track;
    private List<ROIIntersection> intersections;

    public PartitionedTrack(Track track, List<ROIIntersection> intersections)
    {
        this.track = track;
        addIntersections(intersections);
    }
    
    public final void addIntersections(List<ROIIntersection> intersections)
    {
        intersections.sort((i, j) -> i.address - j.address);
        this.intersections = intersections;
    }
    
    public List<TrackInterval> getTrackIntervals()
    {        
        List<TrackInterval> trackIntervals = new ArrayList<>();
        // Track crossings from one ROI to another.
        // ROI_1 ---> ROI_2 ----> ROI_1 is illegal as it means we have a degnerate ROI.
        // we will flag this situation.
        int BorderCrossings = 0; 
        for (int i = 0; i < intersections.size() - 1; i++)
        {
            ROIIntersection intersec0 = intersections.get(i);
            ROIIntersection intersec1 = intersections.get(i + 1);
            if (intersec0.roi != intersec1.roi)
            {
                BorderCrossings++;
                if (BorderCrossings > 1)
                {
                    // Write out to a log when implented.
                }
                trackIntervals.add(new TrackInterval(track, intersec0.address, intersec1.address));
            }
            else
            {
                BorderCrossings = 0;
            }
        }
        return trackIntervals;
    }
    
    @Override
    public String toString()
    {
        String collect = intersections
                .stream()
                .map(i -> String.format("[%s, %d]", i.roi.name, i.address))
                .collect(Collectors.joining(", "));
        return String.format("%s : %s", this.hashCode(), collect);
    }
    
}
