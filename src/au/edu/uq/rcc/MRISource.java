/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import edu.washington.biostr.sig.nifti.AnalyzeNiftiSpmHeader;
import edu.washington.biostr.sig.nifti.NiftiIO;
import edu.washington.biostr.sig.volume.VolumeArray;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3i;

/**
 *
 * @author oliver
 */
public class MRISource
{
    
    private VolumeArray volumeArray;
    private AnalyzeNiftiSpmHeader header = null;
    private short[] dim;
    private float[] pixDim;        
    
    public MRISource(File brainFile)
    {
        try
        {
            NiftiIO.VolumePair volume = NiftiIO.load(brainFile);
            volumeArray = volume.getArray();
            header = volume.getHeader();
            dim = header.getDim();
            pixDim = header.getPixdim();            
        }
        catch (IOException ex)
        {
            Logger.getLogger(MRISource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public AnalyzeNiftiSpmHeader getHeader()
    {
        return header;
    }
    
    public float[] getPixDim()
    {
        return pixDim;
    }
    
    public double getVoxel(int i, int j, int k)
    {        
        return volumeArray.getDouble(i, j, k, 0, 0);
    }
    
    public boolean getVoxelAsBoolean(int i, int j, int k)
    {
        return getVoxel(i, j, k) > 0;
    }
    
    public Tuple3i getDimensions()
    {
        return new Point3i(dim[1], dim[2], dim[3]);
    }
    
    public double getMaxVoxel()
    {
        return volumeArray.getImageMax();
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("dim d = %d, [%d, %d, %d]\n", dim[0], dim[1], dim[2], dim[3]));
        sb.append(String.format("pixDim k = %f, [%f, %f, %f]\n", pixDim[0], pixDim[1], pixDim[2], pixDim[3]));
        return sb.toString();
    }
    
    public Transform getTransform()
    {
        return new Transform(header);
    }

}
