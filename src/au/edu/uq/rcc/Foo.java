/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author oliver
 */
public class Foo
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Face3i f1 = new Face3i(0,0,0,1,Face3i.Y_FACET);
        Face3i f2 = new Face3i(0,0,0,-1,Face3i.Y_FACET);
        
        Set<Face3i> fMap = new HashSet<>();
        
        fMap.add(f1);
        System.out.printf("size = %d\n", fMap.size());
        fMap.add(f2);
        System.out.printf("size = %d\n", fMap.size());
        
        RegionOfInterest roi = new RegionOfInterest(null, "test");
        
        System.out.printf("%s\n", f1.equals(f2));
        
    }
    
  
    
  
 
        
}
