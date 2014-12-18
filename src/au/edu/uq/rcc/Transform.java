/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import edu.washington.biostr.sig.nifti.AnalyzeNiftiSpmHeader;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author oliver
 */
public class Transform
{

    private final float[] offset;
    private final float[] pixDim;
    private final float zDirection; // represents -1 or 1
    private final Matrix3d transposeRotation;
    private final Matrix3d rotation;
    
    Tuple3d interval = new Point3d(1.0,1.0,1.0);
    Tuple3d voxelOriginTransform = new Point3d(interval);

    public Transform(AnalyzeNiftiSpmHeader header)
    {
        voxelOriginTransform.scale(0.5);
        this.pixDim = header.getPixdim();
        zDirection = pixDim[0];
        rotation = getTMethod2Rotation(header);
        transposeRotation = new Matrix3d();
        transposeRotation.transpose(rotation);
        offset = getMethod2Offset(header);
    }
    
    public Tuple3d apply(Tuple3d p)
    {
        Vector3d v = new Vector3d(p.x, p.y, p.z * zDirection);
        v.x = (v.x * pixDim[1]);
        v.y = (v.y * pixDim[2]);
        v.z = (v.z * pixDim[3]);
        rotation.transform(v);
        v.x = v.x + offset[0] + voxelOriginTransform.x;
        v.y = v.y + offset[1] + voxelOriginTransform.y;
        v.z = v.z + offset[2] + voxelOriginTransform.z;
        return v;
    }

    public Tuple3d undo(Tuple3d p)
    {
        Vector3d v = new Vector3d(p);
        v.x -= (offset[0] + voxelOriginTransform.x);
        v.y -= (offset[1] + voxelOriginTransform.y);
        v.z -= (offset[2] + voxelOriginTransform.z);
        transposeRotation.transform(v);
        v.x /= pixDim[1];
        v.y /= pixDim[2];
        v.z /= pixDim[3] * (zDirection);
        return v;
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
    
    private static float[] getMethod2Offset(AnalyzeNiftiSpmHeader h)
    {
        float[] o = new float[3];
        o[0] = h.getQoffsetX();
        o[1] = h.getQoffsetY();
        o[2] = h.getQoffsetZ();
        return o;
    }

}
