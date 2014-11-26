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
    private final List<ROIIntersection> ROIIntersections;
    boolean sorted = false;

    public PartitionedTrack(Track track)
    {
        ROIIntersections = new ArrayList<>();
        this.track = track;
    }
    
    public final void addIntersections(RegionOfInterest roi)
    {
        sorted = false;        
        roi.getIntersections(track).stream()
                .forEach(i -> ROIIntersections.add(new ROIIntersection(roi, i)));
    }
    
    public List<TrackInterval> getTrackIntervals()
    {        
        if (!sorted)
        {            
            ROIIntersections.sort((i, j) -> i.address - j.address);
            sorted = true;
        }
        List<TrackInterval> trackIntervals = new ArrayList<>();
        // Track crossings from one ROI to another.
        // ROI_1 ---> ROI_2 ----> ROI_1 is illegal as it means we have a degnerate ROI.
        // we will flag this situation.
        int BorderCrossings = 0;
        for (int i = 0; i < ROIIntersections.size() - 1; i++)
        {
            ROIIntersection intersec0 = ROIIntersections.get(i);
            ROIIntersection intersec1 = ROIIntersections.get(i + 1);
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
        String collect = ROIIntersections
                .stream()
                .map(i -> String.format("[%s, %d]", i.roi.getName(), i.address))
                .collect(Collectors.joining(", "));
        return String.format("%s : %s", this.hashCode(), collect);
    }
    
}
