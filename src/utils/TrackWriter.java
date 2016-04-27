/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import au.edu.uq.rcc.MRISource;
import au.edu.uq.rcc.PartitionedTrack;
import au.edu.uq.rcc.TrackInterval;
import au.edu.uq.rcc.Transform;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.vecmath.Tuple3d;

/**
 *
 * @author oliver
 */
public class TrackWriter
{

    File trackFile;

    public TrackWriter(File trackFile, List<PartitionedTrack> trackList, MRISource mriSource)
    {
        this.trackFile = trackFile;
        try
        {
            FileOutputStream fso = new FileOutputStream(trackFile);
            BufferedOutputStream bos = new BufferedOutputStream(fso);
            DataOutputStream dos = new DataOutputStream(bos);
            Transform transform = mriSource.getTransform();            
            dos.writeBytes(getHeader());
            for (PartitionedTrack pt : trackList)
            {
                for (TrackInterval ti : pt.getTrackIntervals())
                {
                    for (int i = ti.getStart(); i < ti.getEnd(); i++)
                    {
                        Tuple3d p = ti.getTrack().getVertices().get(i);
                        p = transform.apply(p);
                        dos.writeFloat((float) p.x);
                        dos.writeFloat((float) p.y);
                        dos.writeFloat((float) p.z);
                    }
                    dos.writeFloat((float) Float.NaN);
                    dos.writeFloat((float) Float.NaN);
                    dos.writeFloat((float) Float.NaN);
                }
            }
            dos.writeFloat((float) Float.POSITIVE_INFINITY);
            dos.writeFloat((float) Float.POSITIVE_INFINITY);
            dos.writeFloat((float) Float.POSITIVE_INFINITY);
            dos.flush();
            dos.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(TrackWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getHeader()
    {
        List<String> headerLines = Arrays.asList(
                "mrtrix tracks\n",
                "datatype: Float32BE\n",
                String.format("file . %d\n", 0),
                "END\n");

        int initialSize, newSize;
        do
        {
            initialSize = headerSize(headerLines);
            headerLines.set(2, String.format("file: . %d\n", initialSize));
            newSize = headerSize(headerLines);
        } while (initialSize != newSize);
        return headerLines.stream().collect(Collectors.joining());
    }

    private static int headerSize(List<String> headerLines)
    {
        return headerLines.stream().mapToInt((String s) -> s.length()).sum();
    }

}
