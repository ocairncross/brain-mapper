/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package au.edu.uq.rcc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

/**
 *
 * @author oliver
 */
public class Track
{
    
    private final List<Tuple3d> vertices = new ArrayList<>();
    private final List<Color> vertexColor = new ArrayList<>();
    private Color colour = Color.green;
    private boolean selected = false;
    
    public void addPoint(double x, double y, double z)
    {
        Point3d p = new Point3d(x, y, z);
        addPoint(p);
    }
    
    public void addPoint(Tuple3d p)
    {
        vertices.add(p);        
        vertexColor.add(colour);
    }
    
    public void addPointList(List<Tuple3d> points)
    {
        vertices.addAll(points);
    }
    
    public void setColor(int v, Color c)
    {        
        vertexColor.add(v, c);
    }
    
    public Color getColor(int v)
    {
        return vertexColor.get(v);
    }
    
    public void setAllColors(Color colour)
    {
        this.colour = colour;
        IntStream.range(0, numberOfVertices()).forEach(i -> vertexColor.set(i, colour));
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

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public List<Color> getVertexColor()
    {
        return vertexColor;
    }
    
    @Override
    public String toString()
    {
        return Integer.toHexString(this.hashCode()) + ", " + numberOfVertices();
    }
    
}
