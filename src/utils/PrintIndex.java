/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import au.edu.uq.rcc.index.BrainIndex;
import au.edu.uq.rcc.index.TrackIntersection;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 *
 * @author oliver
 */
public class PrintIndex
{

    public static void printStats(BrainIndex bi) throws NullArgumentException
    {
        DescriptiveStatistics xStats = planeStats(bi.getXPlane());
        DescriptiveStatistics yStats = planeStats(bi.getYPlane());
        DescriptiveStatistics zStats = planeStats(bi.getZPlane());

        System.out.printf("x %s\n", xStats.toString());
        System.out.printf("y %s\n", yStats.toString());
        System.out.printf("z %s\n", zStats.toString());

        EmpiricalDistribution xDist = new EmpiricalDistribution(10);
        EmpiricalDistribution yDist = new EmpiricalDistribution(10);
        EmpiricalDistribution zDist = new EmpiricalDistribution(10);

        xDist.load(xStats.getValues());
        yDist.load(yStats.getValues());
        zDist.load(zStats.getValues());

        printDist("x DIST", xDist);
        printDist("y DIST", yDist);
        printDist("z DIST", zDist);
    }


    public static void PrintPlaneStats(String name, ArrayList<TrackIntersection>[][][] a)
    {
        DescriptiveStatistics planeStats = planeStats(a);
        EmpiricalDistribution distribution = new EmpiricalDistribution(20);
        distribution.load(planeStats.getValues());
        System.out.printf("%s plane\n %s\n", name, planeStats);
        printDist(name + " plane", distribution);
    }
    
    private static void printDist(String name, EmpiricalDistribution d)
    {
        System.out.printf("%s\n", name);
        for (SummaryStatistics ss : d.getBinStats())
        {
            System.out.printf("%03.2f: %,d\n", ss.getMin(), ss.getN());
        }
        System.out.printf("\n");
    }

    private static DescriptiveStatistics planeStats(ArrayList<TrackIntersection>[][][] plane)
    {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < plane.length; i++)
        {
            for (int j = 0; j < plane[0].length; j++)
            {
                for (int k = 0; k < plane[0][0].length; k++)
                {
                    ArrayList<TrackIntersection> face = plane[i][j][k];
                    stats.addValue(face.size());
                }
            }
        }
        return stats;
    }
    
    private int emptyFaces(ArrayList<TrackIntersection>[][][] plane)
    {
        int c = 0;
        for (int i = 0; i < plane.length; i++)
        {
            for (int j = 0; j < plane[0].length; j++)
            {
                for (int k = 0; k < plane[0][0].length; k++)
                {
                    ArrayList<TrackIntersection> face = plane[i][j][k];
                    if (face.size() == 0)
                    {
                        c++;
                    }
                }
            }
        }
        return c;
    }
    
    private int notEmptyFaces(ArrayList<TrackIntersection>[][][] plane)
    {
        int c = 0;
        for (int i = 0; i < plane.length; i++)
        {
            for (int j = 0; j < plane[0].length; j++)
            {
                for (int k = 0; k < plane[0][0].length; k++)
                {
                    ArrayList<TrackIntersection> face = plane[i][j][k];
                    if (face.size() > 0)
                    {
                        c++;
                    }
                }
            }
        }
        return c;
    }
    
}
