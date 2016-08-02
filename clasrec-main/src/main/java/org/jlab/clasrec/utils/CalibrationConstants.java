/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clasrec.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jlab.clas.detector.DetectorCollection;
import org.jlab.clas.detector.DetectorType;
import org.jlab.containers.HashTable;

/**
 *
 * @author gavalian
 */
public class CalibrationConstants {
        
    DetectorCollection<CalibrationEntry>  constantsCollection = new 
        DetectorCollection<CalibrationEntry>();
    
    Map<String,String>    calibrationTableNames = new HashMap<String,String>();
    Map<String,HashTable> calibrationTables     = new HashMap<String,HashTable>();
    
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
    
    public void define(String name, String table){
        if(this.calibrationTableNames.containsKey(name)==true){
            System.out.println("[CalibrationConstants] ----> replacing entry for " + name);
        }
        this.calibrationTableNames.put(name, table);
    }
    
    public void init(int run, String variation){
        DatabaseConstantProvider provider = new DatabaseConstantProvider(run,variation);
        for(Map.Entry<String,String> entry : this.calibrationTableNames.entrySet()){
            HashTable  table = provider.readTable(entry.getValue());
            this.calibrationTables.put(entry.getKey(), table);
        }
        provider.disconnect();
    }
    
    public boolean hasEntry(String name, int sector, int layer, int component){
        if(this.calibrationTables.containsKey(name)==false) return false;
        return this.calibrationTables.get(name).hasRow(sector,layer,component);
    }
    
    public double getEntryDouble(String name, String column, int sector, int layer, int component){
        if(this.calibrationTables.containsKey(name)==false){
            return 0.0;
        }
        HashTable table = this.calibrationTables.get(name);
        if(table.hasRow(sector,layer,component)==true&&table.hasColumn(column)){
            Number value = table.getValue(column, sector,layer,component);
            if(value instanceof Double) return (Double) value;
            System.out.println(String.format("(error) : [%3d %3d %3d] value [%s] is not a double.",
                    sector,layer,component, column));
        }
        return 0.0;
    }
    
    public int getEntryInt(String name, String column, int sector, int layer, int component){
        if(this.calibrationTables.containsKey(name)==false){
            return 0;
        }
        HashTable table = this.calibrationTables.get(name);
        //System.out.println(" HAS ENTRY  = " + table.hasRow(sector,layer,component));
        if(table.hasRow(sector,layer,component)==true&&table.hasColumn(column)){
            Number value = table.getValue(column, sector,layer,component);
            if(value instanceof Integer) return (Integer) value;
            System.out.println(String.format("(error) : [%3d %3d %3d] value [%s] is not an integer.",
                    sector,layer,component, column));
        }
        return 0;
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
    public String getCharString(String symbol,int length) {
        StringBuilder str = new StringBuilder();
        for(int i=0;i<length;i++) str.append(symbol);
        return str.toString();
    }
    
    public void show(){
        //this.constantsCollection.show();
        /*List<CalibrationEntry> entries = this.constantsCollection.getList();
        for(CalibrationEntry item : entries){
            System.out.println(item);
        }*/
        System.out.println("----->  Calibration Constants");
        System.out.println(this.getCharString("*", 64));
        for(Map.Entry<String,HashTable>  entry : this.calibrationTables.entrySet()){
            System.out.println(String.format("* %12s :  COLUMNS = %12d , ROWS = %12d *", 
                    entry.getKey(),entry.getValue().getColumnCount(),
                    entry.getValue().getRowCount() ));
        }
        System.out.println(this.getCharString("*", 64));
    }
    
    public void setValueAsDouble(String system, String variable, double value, int... index){
        this.calibrationTables.get(system).setValueAtAsDouble(variable, value, index);
    }
    
    public void setValueAsInt(String system, String variable, int value, int... index){
        this.calibrationTables.get(system).setValueAtAsInt(variable, value, index);
    }
    
    public void writeFile(String name, String filename){
        if(this.calibrationTables.containsKey(name)==true){
            this.calibrationTables.get(name).writeFile(filename);
        } else {
            System.out.println("[] ---> error. no calibration table with name "
            + name + "  exists");
        }
    }
    public void describe(){
         for(Map.Entry<String,HashTable>  entry : this.calibrationTables.entrySet()){
            System.out.println("CALIBRATION CONSTANTS : " + entry.getKey());
            entry.getValue().describe();
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
