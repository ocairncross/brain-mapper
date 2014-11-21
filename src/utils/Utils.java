/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import au.edu.uq.rcc.RegionOfInterest;
import au.edu.uq.rcc.index.BrainIndex;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver
 */
public class Utils
{

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static void printClassPath()
    {
        String classPath = System.getProperty("java.class.path");
        Arrays.asList(classPath.split(":"))
                .stream()
                .forEach(c -> log.debug("class-path: {}", c));
        
    }

    public static void printLoggerInfo()
    {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);
    }
    
    public static void logInitial()
    {
        Utils.printClassPath();
        Runtime r = Runtime.getRuntime();
        log.info(String.format("Executing with %s processors in thread pool", r.availableProcessors()));
        log.info("Total memory {}", r.totalMemory());
        log.info("Free memory {}", r.freeMemory());
    }
    
    public static void setMask(int x, int y, int z, int s, RegionOfInterest roi)
    {        
        for (int i = 0; i < s; i++)
        {
            for (int j = 0; j < s; j++)
            {
                for (int k = 0; k < s; k++)
                {
                    roi.setVoxel(i + x, j + y, k + z);
                }
            }
        }
        
    }
    


}
