/*
 * Note that the hash and equality functions for Face is that of Tuple3i
 */
package au.edu.uq.rcc;

import au.edu.uq.rcc.index.ROIIntersection;
import au.edu.uq.rcc.index.TrackIntersection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.vecmath.Tuple3i;
import javax.vecmath.Tuple4i;

/**
 *
 * @author oliver
 */
public class Face3i extends Tuple4i
{
    // w coorinate represents the facet.
    
    public static final int X_FACET = 0;
    public static final int Y_FACET = 1;
    public static final int Z_FACET = 2;
    
    public int n; // Represents the normal it is either + or -
    
    private Set<TrackIntersection> intersectionSet;
    
    public Face3i(Tuple3i p, int facet)
    {
        super(p.x, p.y, p.z, facet);
    }
    
    public Face3i(int x, int y, int z, int n, int facet)
    {
        super(x, y, z, facet);
        this.n = n;
    }
    
    public Face3i(int x, int y, int z, int facet)
    {
        super(x, y, z, facet);
    }

    public Face3i(int[] p)
    {
        super(p);
    }

    public Face3i(Tuple4i t1)
    {
        super(t1);
    }

    public Face3i()
    {
        super();
    }

    public int getN()
    {
        return n;
    }

    public void setN(int n)
    {
        this.n = n;
    }
    
    public Face3i getParallelFace()
    {
        if (w == X_FACET)
        {
            return new Face3i(x + 1, y, z, w);
        }
        else if (w == Y_FACET)
        {
            return new Face3i(x, y + 1, z, w);
        }
        else
        {
            return new Face3i(x, y, z + 1, w);
        }
    }
    
    public int numberOfTracks()
    {
        if (intersectionSet == null)
        {
            return 0;
        }
        else
        {
            return intersectionSet.size();
        }
    }
    
    public List<Track> getTracks()
    {
        if (intersectionSet == null)
        {
            return null;
        }
        else
        {
            return intersectionSet.stream().map(s -> s.track).collect(Collectors.toList());
        }
    }
    
    @Override
    public String toString()                        
    {
        String face = "x";
        if (w == Y_FACET)
        {
            face = "y";
        }
        else if (w == Z_FACET)
        {
            face = "z";
        }
        
        String normal = "+";
        if (n < 0)
        {
            normal = "-";
        }
        
        return String.format("(%3d,%3d,%3d) %s %s", x, y, z, face, normal);
    }
    
    
}
