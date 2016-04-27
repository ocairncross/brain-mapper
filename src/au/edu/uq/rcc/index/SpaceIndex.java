/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc.index;

import au.edu.uq.rcc.MRISource;
import au.edu.uq.rcc.RegionOfInterest;
import au.edu.uq.rcc.Track;
import au.edu.uq.rcc.TrackCollection;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author oliver
 */
public class SpaceIndex
{

    TrackCollection tracks;

    public SpaceIndex(TrackCollection tracks, MRISource mriSource)
    {
        this.tracks = tracks;

    }

    public void getTrackStats()
    {
        System.out.printf("calc track stats");
        DescriptiveStatistics ds = new DescriptiveStatistics();
        tracks.getTrackList()
                .stream()
                .forEach(t ->
                {
                    ds.addValue(t.numberOfVertices());
                });
        
        System.out.printf("%s\n", ds.toString());
        System.out.printf("total points: %,.0f\n", ds.getSum());
    }

    public List<Track> getTracks(RegionOfInterest roi)
    {
        List<Track> intersectTracks = new ArrayList<>();
        boolean[][][] roiMask = roi.getRoiMask();

        tracks.getTrackList()
                .stream()
                .forEach(t ->
                        {
                            boolean match = t.getVertices()
                            .stream()
                            .anyMatch(v ->
                                    {
                                        int x = (int) v.x;
                                        int y = (int) v.y;
                                        int z = (int) v.z;
                                        return roiMask[x][y][z];
                            });
                            if (match)
                            {
                                intersectTracks.add(t);
                            }
                });
        return intersectTracks;
    }

}
