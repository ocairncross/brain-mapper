/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import java.net.URL;
import java.net.URLClassLoader;
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

}
