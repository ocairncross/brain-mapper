/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uq.rcc.index;

import au.edu.uq.rcc.Track;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author oliver
 */
public class RunnableBrainIndex<T extends Track> implements Runnable
{

    private final BrainIndex brainIndex;
    private final BlockingQueue<T> trackQueue = new ArrayBlockingQueue(1024);
    public static final Track FINAL = new Track();

    public RunnableBrainIndex(BrainIndex brainIndex)
    {
        this.brainIndex = brainIndex;
    }

    public BlockingQueue<T> getTrackQueue()
    {
        return trackQueue;
    }

    @Override
    public void run()
    {
        try
        {
            Track track = trackQueue.take();
            while (track != FINAL)
            {
                brainIndex.insertTrack(track);                
                track = trackQueue.take();
            }            
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(RunnableBrainIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
