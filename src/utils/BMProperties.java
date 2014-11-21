/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver
 */
public class BMProperties
{

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BMProperties.class);
    private static final Properties properties = new Properties();
    private static final File propFile = new File("properties.xml");

    static
    {
        try
        {
            InputStream is = new FileInputStream(propFile);
            properties.loadFromXML(is);
            log.info("read property file \"{}\"", propFile.getCanonicalPath());
            properties.stringPropertyNames()
                    .stream()
                    .forEach(pn -> log.info("{} = \"{}\"", pn, properties.getProperty(pn)));
        }
        catch (IOException ex)
        {
            log.error(ex.getLocalizedMessage(), ex);
            throw new Error(ex);
        }
    }

    public static void generateDefaults()
    {
        properties.setProperty("base-directory", "/media/oliver/A066DA6266DA392C/projects/brain/human/NC001");
        properties.setProperty("mri-source", "bet/NC001_convert_eddy_Ave_nodif_mask.nii");
        properties.setProperty("track-source", "mrtrix/NC001_whole_brain_DT_STREAM.tck");
        properties.setProperty("roi-directory", "roi");
        properties.setProperty("log-file", "brain-mapper.log");
        properties.setProperty("scale-factor", "10");

        try
        {
            OutputStream os = new FileOutputStream(propFile);
            properties.storeToXML(os, "brain-mapper-props");
            log.info("wrote properties to {}", propFile.getCanonicalPath());
        } catch (IOException ex)
        {
            log.error(ex.getLocalizedMessage(), ex);
        }
    }

    public static String get(String prop)
    {
        return properties.getProperty(prop);
    }

    public static boolean getBoolean(String n)
    {
        return Boolean.parseBoolean(properties.getProperty(n));
    }
    
    public static int getInt(String n)
    {
        return Integer.parseInt(properties.getProperty(n));
    }
    
    public static File getFile(String n)
    {
        File root = new File(properties.getProperty("base-directory"));
        return new File(root, properties.getProperty(n));
    }
    
    
}
