/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3i;

/**
 *
 * @author oliver
 */
public class BoundingBox
{
    MinMax x = new MinMax();
    MinMax y = new MinMax();
    MinMax z = new MinMax();
    
    public void add(double px, double py, double pz)
    {
        x.setVal(px);
        y.setVal(py);
        z.setVal(pz);
    }
    
    public void add(Tuple3d p)
    {
        x.setVal(p.x);
        y.setVal(p.y);
        z.setVal(p.z);
    }
    
    public void add(BoundingBox b)
    {
        x.setVal(b.x);
        y.setVal(b.y);
        z.setVal(b.z);
    }
    
    @Override
    public String toString()
    {
        return String.format("[%f,%f,%f], [%f,%f,%f]", x.getMin(), y.getMin(), z.getMin(),
                x.getMax(), y.getMax(), z.getMax());
    }
    
    public MinMax getXRange()
    {
        return x;
    }
    
    public MinMax getYRange()
    {
        return y;
    }
    
    public MinMax getZRange()
    {
        return z;
    }
    
    public Tuple3d getBasePoint()
    {
        return new Point3d(x.getMin(), y.getMin(), z.getMin());
    }
    
    public Tuple3d getIntervalLength(Tuple3i i)
    {
        return new Point3d(x.getRange() / i.x, y.getRange() / i.y, z.getRange() / i.z);
    }
    
    public float[] getMin()
    {
        return new float[]{(float) x.getMin(), (float) y.getMin(), (float) z.getMin()};       
    }
    
    public float[] getMax()
    {
        return new float[]{(float) x.getMax(), (float) y.getMax(), (float) z.getMax()};       
    }
    
    
    
}
