/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clasrec.utils;

import java.util.List;
import java.util.Set;
import org.jlab.clas.detector.DetectorCollection;

/**
 *
 * @author gavalian
 */
public class CalibrationConstants {
        
    DetectorCollection<CalibrationEntry>  constantsCollection = new 
        DetectorCollection<CalibrationEntry>();
    
    public CalibrationConstants(){
        
    }
    
    public boolean hasEntry(int sector, int layer, int component){
        return this.constantsCollection.hasEntry(sector, layer, component);
    }
    
    public double getEntry(String name, int sector, int layer, int component){
        if(this.hasEntry(sector, layer, component)==false){
            System.out.println("[CalibrationConstant] ---> error no entry for "
            + sector + "  " + layer + "  " + component);
            return 0.0;
        }
        
        CalibrationEntry entry = this.constantsCollection.get(sector, layer, component);
        
        if(entry.getMap().containsKey(name)==true){
            return entry.getMap().get(name);
        }
        
        return 0.0;
    }
    
    public String getPathLast(String path){
        String[] tokens = path.split("/");
        return tokens[tokens.length-1];
    }
    
    
    
    public void loadTable(String table_name, int run, String variation){
        
        DatabaseConstantProvider  provider = new DatabaseConstantProvider();
        provider.loadTable(table_name);
        provider.disconnect();
        //System.out.println("Done loading the table");
        provider.show();
        Set<String> keys = provider.getEntrySet();
        
        int size = provider.getSize(table_name + "/sector");
        System.out.println("-----> SIZE = " + size);
        
        for(int loop = 0; loop < size; loop++){
            int sector    = provider.getInteger(table_name+"/sector", loop);
            int layer     = provider.getInteger(table_name+"/layer", loop);
            int component = provider.getInteger(table_name+"/component", loop);
            
            CalibrationEntry  entry = new CalibrationEntry();
            for(String key : keys){
                String column = this.getPathLast(key);
                //String[] tokens = key.split("/");
                //System.out.println("SPLIT SIZE = " + tokens.length);
                if(column.compareTo("sector")!=0&&
                        column.compareTo("layer")!=0&&column.compareTo("component")!=0){
                    double value = provider.getDouble(key, loop);
                    entry.getMap().put(column, value);
                }
            }
            this.constantsCollection.add(sector, layer, component, entry);            
        }
    }
    
    public void show(){
        //this.constantsCollection.show();
        List<CalibrationEntry> entries = this.constantsCollection.getList();
        for(CalibrationEntry item : entries){
            System.out.println(item);
        }
    }
    
    public static void main(String[] args){
        
        CalibrationConstants calibGain = new CalibrationConstants();
        calibGain.loadTable("/calibration/ftof/gain_balance", 10, "default");
        //calibGain.show();
        
        CalibrationConstants calibAtten = new CalibrationConstants();
        calibAtten.loadTable("/calibration/ftof/attenuation", 10, "default");
        //calibAtten.show();
        
        System.out.println("ATTENUATION LEFT/RIGHT  = "
                + calibAtten.getEntry("attlen_left", 2, 1, 15)
                + " / " + calibAtten.getEntry("attlen_right", 2, 1, 15));
        
    }
}
