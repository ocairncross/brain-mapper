/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package au.edu.uq.rcc.nifti;

import edu.washington.biostr.sig.nifti.AnalyzeNiftiSpmHeader;
import edu.washington.biostr.sig.nifti.NiftiIO;
import edu.washington.biostr.sig.nifti.NiftiIO.VolumePair;
import edu.washington.biostr.sig.volume.VolumeArray;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Matrix4d;

/**
 *
 * @author oliver
 */
public class NiftiLoader
{
    
    VolumeArray va;

    public NiftiLoader()
    {        
        File f = new File("/media/oliver/A066DA6266DA392C/projects/brain/human/NC001/bet/NC001_convert_eddy_Ave_nodif_mask.nii");
        try
        {
            VolumePair volume = NiftiIO.load(f);
            AnalyzeNiftiSpmHeader header = volume.getHeader();
            System.out.printf("Intent: %d\n",header.getIntentCode());
            System.out.printf("QForm: %d\n", header.getQformCode());        
            System.out.printf("SForm: %d\n", header.getSformCode());       
            
            // System.out.printf("%s\n", volume.getHeader().toString());            
            va = volume.getArray();            
            System.out.printf("dims [%d, %d, %d, %d, %d]\n", va.getMaxX(), va.getMaxY(), va.getMaxZ(), va.getMaxTime(), va.getMaxI5());
            Matrix4d index2Space = va.getIndex2Space();
            Matrix4d space2Index = va.getSpace2Index();
            System.out.printf("\n%s\n", index2Space.toString());
            System.out.printf("\n%s\n", space2Index.toString());
            System.out.printf("\nsize [%f, %f, %f]\n", va.getMmPerX(), va.getMmPerY(), va.getMmPerZ());
        }
        catch (IOException ex)
        {
            Logger.getLogger(NiftiLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public VolumeArray getVolumeArray()
    {
        return va;
    }
    
}
