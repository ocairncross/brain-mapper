/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc.index;

import au.edu.uq.rcc.RegionOfInterest;

/**
 *
 * @author oliver
 */
public class ROIIntersection
{
    
    public RegionOfInterest roi;
    public int address;

    public ROIIntersection(RegionOfInterest roi, int i)
    {        
        this.roi = roi;
        this.address = i;
    }
    
    @Override
    public String toString()            
    {
        return String.format("%s, %d" , roi.toString(), address);
    }
    
}
