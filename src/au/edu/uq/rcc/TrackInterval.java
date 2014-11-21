/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

/**
 *
 * @author oliver
 */
public class TrackInterval
{
    private final Track track;
    private final int start;
    private final int end;

    public TrackInterval(Track track, int start, int end)
    {
        this.track = track;
        this.start = start;
        this.end = end;
    }

    public Track getTrack()
    {
        return track;
    }

    public int getStart()
    {
        return start;
    }

    public int getEnd()
    {
        return end;
    }
    
    
}
