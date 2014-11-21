/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc.index;

import au.edu.uq.rcc.Track;

/**
 *
 * @author oliver
 */
public class TrackIntersection
{
    public Track track;
    public int address; 

    public TrackIntersection(Track t, int i)
    {        
        this.track = t;
        this.address = i;
    }

    @Override
    public String toString()
    {
        return String.format("%s, %d", track.toString(), address);
    }
    
}
