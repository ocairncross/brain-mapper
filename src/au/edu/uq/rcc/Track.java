/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package au.edu.uq.rcc;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

/**
 *
 * @author oliver
 */
public class Track
{
    
    private final List<Tuple3d> vertices = new ArrayList<>();
    
    public void addPoint(double x, double y, double z)
    {
        Point3d p = new Point3d(x, y, z);
        vertices.add(p);    
    }
    
    public void addPointList(List<Tuple3d> points)
    {
        vertices.addAll(points);
    }
    
    public Track getSegment(int start, int end)
    {
        Track segment = new Track();
        segment.addPointList(new ArrayList<>(vertices.subList(start, end)));
        return segment;
    }
    
    public List<Tuple3d> getVertices()
    {
        return vertices;
    }
    
    public int numberOfVertices()
    {
        return vertices.size();
    }
    
    @Override
    public String toString()
    {
        return Integer.toHexString(this.hashCode()) + ", " + numberOfVertices();
    }
    
}
