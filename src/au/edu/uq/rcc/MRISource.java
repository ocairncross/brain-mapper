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
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3i;
import javax.vecmath.Vector3d;

/**
 *
 * @author oliver
 */
public class MRISource
{
    
    private VolumeArray volumeArray;

    private short[] dim;
    private float[] pixDim;
    private float[] offset;
    private Matrix3d rotation;
    private Matrix3d transposeRotation;
    private float zDirection; // represents -1 or 1
    
    public MRISource(File brainFile)
    {
        try
        {
            NiftiIO.VolumePair volume = NiftiIO.load(brainFile);
            volumeArray = volume.getArray();
            AnalyzeNiftiSpmHeader header = volume.getHeader();   
            dim = header.getDim();
            pixDim = header.getPixdim();
            zDirection = pixDim[0];
            rotation = getTMethod2Rotation(header);
            transposeRotation = new Matrix3d();
            transposeRotation.transpose(rotation);            
            offset = getMethod2Offset(header);            
        }
        catch (IOException ex)
        {
            Logger.getLogger(MRISource.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    private static float[] getMethod2Offset(AnalyzeNiftiSpmHeader h)
    {
        float[] o = new float[3];
        o[0] = h.getQoffsetX();
        o[1] = h.getQoffsetY();
        o[2] = h.getQoffsetZ();
        return o;
    }
    
    // check http://brainder.org/2012/09/23/the-nifti-file-format/ for information
    // on how this works.

    private static javax.vecmath.Matrix3d getTMethod2Rotation(AnalyzeNiftiSpmHeader h)
    {
        float b = h.getQuaternB();
        float c = h.getQuaternC();
        float d = h.getQuaternD();

        float a = (float) Math.sqrt(1 - b * b - c * c - d * d);

        float aa = a * a;
        float bb = b * b;
        float cc = c * c;
        float dd = d * d;
        float ac = a * c;
        float ab = a * b;
        float ad = a * d;
        float bc = b * c;
        float bd = b * d;
        float cd = c * d;

        javax.vecmath.Matrix3d r = new javax.vecmath.Matrix3d(
                aa + bb - cc - dd, 2 * (bc - ad), 2 * (bd + ac),
                2 * (bc + ad), aa + cc - bb - dd, 2 * (cd - ab),
                2 * (bd - ac), 2 * (cd + ab), aa + dd - bb - cc);

        return r;
    }

    public float[] applyTransform(float[] coords)
    {        
        Vector3d v = new Vector3d(coords[0], coords[1], coords[2] * zDirection);
        v.x = (v.x * pixDim[1]);
        v.y = (v.y * pixDim[2]);
        v.z = (v.z * pixDim[3]);
        rotation.transform(v);
        v.x = v.x + offset[0];
        v.y = v.y + offset[1];
        v.z = v.z + offset[2];
        return new float[] {(float) v.x, (float) v.y, (float) v.z};
    }
    
    public float[] undoTransform(float[] coords)
    {     
        Vector3d v = new Vector3d(coords[0], coords[1], coords[2]);
        v.x = v.x - offset[0];
        v.y = v.y - offset[1];
        v.z = v.z - offset[2];
        transposeRotation.transform(v);
        v.x = v.x / pixDim[1];
        v.y = v.y / pixDim[2];
        v.z = v.z / pixDim[3] * zDirection * -1;
        return new float[]{(float) v.x, (float) v.y, (float) v.z};
    }
    
    public Tuple3d undoTransform(Tuple3d p)
    {
        Vector3d v = new Vector3d(p);
        v.x -= offset[0];
        v.y -= offset[1];
        v.z -= offset[2];
        transposeRotation.transform(v);
        v.x /= pixDim[1];
        v.y /= pixDim[2];
        v.z /= pixDim[3] * (zDirection);
        return v;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("dim d = %d, [%d, %d, %d]\n", dim[0], dim[1], dim[2], dim[3]));
        sb.append(String.format("pixDim k = %f, [%f, %f, %f]\n", pixDim[0], pixDim[1], pixDim[2], pixDim[3]));
        sb.append(String.format("offset [%f, %f, %,f]\n", offset[0], offset[1], offset[2]));
        sb.append(String.format("rotation:\n%s\n",rotation.toString()));
        return sb.toString();
    }

}
