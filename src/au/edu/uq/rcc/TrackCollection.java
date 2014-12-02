/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc;

import au.edu.uq.rcc.index.BrainIndex;
import au.edu.uq.rcc.index.RunnableBrainIndex;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.input.SwappedDataInputStream;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver
 */
public class TrackCollection
{

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(TrackCollection.class);
    private List<Track> trackList = new ArrayList<>();

    public TrackCollection(File trackFile)
    {
        loadTracks(trackFile, null);
    }

    public TrackCollection(File trackFile, BrainIndex bi)
    {
        RunnableBrainIndex rbi = new RunnableBrainIndex(bi);
        new Thread(rbi).start();
        loadTracks(trackFile, rbi.getTrackQueue());
    }

    private void loadTracks(File trackFile, BlockingQueue<Track> trackQueue)
    {
        try
        {
            log.info("loading track file '{}'", trackFile.getAbsolutePath());
            int offset = 0;
            long fileSize = trackFile.length();
            long logIncrement = fileSize / 10;
            long nextLogEvent = logIncrement;
            long filePosition = 0;
            FileReader fr = new FileReader(trackFile);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (!line.matches("END"))
            {
                if (line.startsWith("file"))
                {
                    offset = extractNumber(line);
                }
                line = br.readLine();
            }

            BufferedInputStream bufIS = new BufferedInputStream(Files.newInputStream(trackFile.toPath()));
            SwappedDataInputStream inputStream = new SwappedDataInputStream(bufIS);
            inputStream.skip(offset);
            filePosition = offset;

            Float x = inputStream.readFloat();
            Float y = inputStream.readFloat();
            Float z = inputStream.readFloat();
            filePosition += 12;

            while (!(x.isInfinite() && y.isInfinite() && z.isInfinite()))
            {
                Track track = new Track();
                while (!(x.isNaN() && y.isNaN() && z.isNaN()))
                {
                    track.addPoint(x, y, z);
                    x = inputStream.readFloat();
                    y = inputStream.readFloat();
                    z = inputStream.readFloat();
                    filePosition += 12;
                }
                trackList.add(track);
                if (trackQueue != null)
                {
                    trackQueue.add(track);
                }
                x = inputStream.readFloat();
                y = inputStream.readFloat();
                z = inputStream.readFloat();
                filePosition += 12;

                if (filePosition > nextLogEvent)
                {
                    String complete = String.format("%.2f%%", (float) filePosition / fileSize * 100);
                    log.info("read {} bytes {} done", filePosition, complete);
                    nextLogEvent += logIncrement;
                }
            }
            if (trackQueue != null)
            {
                trackQueue.add(RunnableBrainIndex.FINAL);
            }
            log.debug("loaded {} tracks", trackList.size());
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

}
