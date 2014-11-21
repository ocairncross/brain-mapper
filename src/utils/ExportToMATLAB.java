/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import au.edu.uq.rcc.Track;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Tuple3d;

/**
 *
 * @author oliver
 */
public class ExportToMATLAB
{
    
    double m[][];

    public ExportToMATLAB(Track track)
    {
        m = new double[track.numberOfVertices()][3];
        List<Tuple3d> vertices = track.getVertices();
        for (int i = 0; i < m.length; i++)
        {            
            Tuple3d p = vertices.get(i);
            m[i][0] = p.getX();
            m[i][1] = p.getY();
            m[i][2] = p.getZ();            
        }
        
        MLDouble mlDouble = new MLDouble("double_arr", m);
        List list = new ArrayList();
        list.add(mlDouble);
        
        try
        {
            new MatFileWriter(new File("tack.mat"), list);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ExportToMATLAB.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
    
    
    
    
    
}