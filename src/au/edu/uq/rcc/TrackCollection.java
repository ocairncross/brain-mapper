/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.vecmath.Tuple3d;
import org.apache.commons.io.input.SwappedDataInputStream;
import utils.BoundingBox;

/**
 *
 * @author oliver
 */
public class TrackCollection
{

    List<Track> trackList = new ArrayList<>();
    BoundingBox boundingBox = new BoundingBox();

    public TrackCollection(File trackFile)
    {
        try
        {
            int offset = 0;
            FileReader fr = new FileReader(trackFile);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (!line.matches("END"))
            {
                // System.out.printf("%s\n", line);
                if (line.startsWith("file"))
                {
                    offset = extractNumber(line);
                }
                line = br.readLine();
            }

            Files.newInputStream(trackFile.toPath());
            BufferedInputStream bufIS = new BufferedInputStream(Files.newInputStream(trackFile.toPath()));
            SwappedDataInputStream inputStream = new SwappedDataInputStream(bufIS);

            inputStream.skip(offset);

            Float x = inputStream.readFloat();
            Float y = inputStream.readFloat();
            Float z = inputStream.readFloat();

            while (!(x.isInfinite() && y.isInfinite() && z.isInfinite()))
            {
                Track track = new Track();
                while (!(x.isNaN() && y.isNaN() && z.isNaN()))
                {
                    track.addPoint(x, y, z);
                    x = inputStream.readFloat();
                    y = inputStream.readFloat();
                    z = inputStream.readFloat();
                    boundingBox.add(x, y, z);
                }
                trackList.add(track);
                // System.out.printf("Track: %,d, %,d\n", trackList.size(), trackList.get(trackList.size() - 1).numberOfVertices());
                if (trackList.size() > 500 && false)
                {
                    break;
                }
                x = inputStream.readFloat();
                y = inputStream.readFloat();
                z = inputStream.readFloat();
            }

        } catch (IOException ex)
        {
            Logger.getLogger(TrackCollection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void scaleUp(int factor)
    {
        
        if (factor < 2)
        {
            return;
        }
        
        List<Track> originalList = new ArrayList<>(trackList);
        originalList.stream()
                .forEach(t ->
                        {
                            for (int i = 0; i < factor; i++)
                            {
                                trackList.add(t);
                            }
                }
                );
    }

    public List<Track> getTrackList()
    {
        return trackList;
    }

    public BoundingBox getBoundingBox()
    {
        return boundingBox;
    }

    private Integer extractNumber(String s)
    {
        Integer i = null;
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(s);
        while (m.find())
        {
            i = Integer.parseInt(m.group());
        }
        if (i == null)
        {
            throw new Error("bad trc file");
        }
        return i;
    }

    public Stream<Tuple3d> vertexStream()
    {
        return trackList.stream().flatMap(t -> t.getVertices().stream());
    }

}
