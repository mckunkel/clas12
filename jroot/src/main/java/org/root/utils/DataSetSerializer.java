/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import org.root.base.EvioWritableTree;
import org.root.histogram.H1D;
import org.root.histogram.H2D;

/**
 *
 * @author gavalian
 */
public class DataSetSerializer {
    public static byte[] getByteArray(EvioWritableTree h){
        try {
            TreeMap<Integer,Object> tree = h.toTreeMap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
               
            out = new ObjectOutputStream(bos);
            out.writeObject(tree);
            byte[] yourBytes = bos.toByteArray();
            return yourBytes;
        } catch (IOException ex) {
            Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    public static byte[] gzip(byte[] ungzipped) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bytes);
            gzipOutputStream.write(ungzipped);
            gzipOutputStream.close();
        } catch (IOException e) {
           // LOG.error("Could not gzip " + Arrays.toString(ungzipped));
            System.out.println("[iG5DataCompressor] ERROR: Could not gzip the array....");
        }
        return bytes.toByteArray();
    }
    
    public static void main(String[] args){
        H2D  h2 = new H2D("h2",200,0.0,12.0,200,0.0,12.0);
        DataFactory.createSampleH2D(h2, 450000);
        byte[] array = DataSetSerializer.getByteArray(h2);
        System.out.println("SIZE = " + array.length + " " + (100*100*8));
        byte[] comp  = DataSetSerializer.gzip(array);
        System.out.println("COMPRESSED SIZE = " + comp.length);
        
        double factor = ((double) comp.length)/array.length;
        
        System.out.println("Compression factor = " + String.format("%.3f", 100.0*factor));
    }
}
