/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import java.util.Collection;

/**
 *
 * @author oliver
 */
public interface TrackProvider
{
    public Collection<Track> getTrackList();
    public String getName();
    
}
