/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * @author oliver
 */
public class TrackComparator
{

    private final List<Track> tc1;
    private final List<Track> tc2;

    private List<Track> sameTracks;
    private List<Track> tc1Only;
    private List<Track> tc2Only;

    public TrackComparator(List<Track> tc1, List<Track> tc2)
    {
        this.tc1 = tc1;
        this.tc2 = tc2;        
        compare();
    }

    private void compare()
    {
        sameTracks = new ArrayList<>();
        List<Track> sourceTracks = tc1;
        tc2Only = new ArrayList<>(tc2);
        tc1Only = new ArrayList<>();
        
        for(int i = 0; i < sourceTracks.size(); i++)
        {
            Track t = sourceTracks.get(i);
            boolean match = false;
            for (int j = 0; j < tc2Only.size(); j++)
            {
                if (equals(t, tc2Only.get(j)))
                {
                    sameTracks.add(t);
                    tc2Only.remove(j);
                    match = true;
                    break;
                }
            }
            if (!match)
            {
                tc1Only.add(t);
            }
        }
        
    }

    private boolean equals(Track t1, Track t2)
    {  
        if (t1.numberOfVertices() != t2.numberOfVertices())
        {
            return false;
        }

        return IntStream
                .range(0, t1.numberOfVertices())
                .parallel()
                .allMatch(i -> t1.getVertices().get(i).equals(t2.getVertices().get(i)));
    }
    
    public List<Track> getSameTracks()
    {
        return sameTracks;
    }
    
    public List<Track> getFirstTracks()
    {
        return tc1Only;
    }
    
    public List<Track> getSecondTracks()
    {
        return tc2Only;
    }

}
