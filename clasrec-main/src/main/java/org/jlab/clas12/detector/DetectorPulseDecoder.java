/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.detector;

import java.util.HashMap;
import java.util.Map;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clasrec.utils.DatabaseConstantProvider;
import org.jlab.containers.HashTable;

/**
 *
 * @author gavalian
 */
public class DetectorPulseDecoder {
    
    private Map<DetectorType,HashTable>   detectorFADC = new HashMap<DetectorType,HashTable>();
    private Map<DetectorType,String>      detectorTables = new HashMap<DetectorType,String>();
    
    
    public DetectorPulseDecoder(){
        
        detectorTables.put(DetectorType.EC, "/daq/fadc/ec");
        detectorTables.put(DetectorType.FTOF, "/daq/fadc/ftof");
        detectorTables.put(DetectorType.FTCAL, "/daq/fadc/ftcal");
        
    }
    
    public DetectorPulseDecoder(int run, String variation){
        detectorTables.put(DetectorType.EC, "/daq/fadc/ec");
        detectorTables.put(DetectorType.FTOF, "/daq/fadc/ftof");
        detectorTables.put(DetectorType.FTCAL, "/daq/fadc/ftcal");
        this.init(run, variation);
    }
    
    public void add(String type, String table){
        if(detectorTables.containsKey(DetectorType.getType(type))==true){
            System.out.println("[DetectorPulseDecoder] --> overwritting table definition " + type);
        }
        detectorTables.put(DetectorType.getType(type), table);
    }
    
    public void add(DetectorType type, String table){
        this.add(type.getName(), table);
    }
    
    public final void init(int run, String variation){
        DatabaseConstantProvider provider = new DatabaseConstantProvider(run,variation);
        for(Map.Entry<DetectorType,String> entry : this.detectorTables.entrySet()){
            HashTable  table = provider.readTable(entry.getValue());
            this.detectorFADC.put(entry.getKey(), table);
        }
        provider.disconnect();
        this.show();
    }
    
    public HashTable  getTable(DetectorType type){
        return this.detectorFADC.get(type);
    }
    
    public HashTable  getTable(String type){
        return this.detectorFADC.get(DetectorType.getType(type));
    }
    
    public String getCharString(String symbol,int length) {
        StringBuilder str = new StringBuilder();
        for(int i=0;i<length;i++) str.append(symbol);
        return str.toString();
    }
    
    public void show(){
        System.out.println("----->  Detector Pulse Decoder");
        System.out.println(this.getCharString("*", 64));
        for(Map.Entry<DetectorType,HashTable>  entry : this.detectorFADC.entrySet()){
            System.out.println(String.format("* %12s :  COLUMNS = %12d , ROWS = %12d *", 
                    entry.getKey().toString(),entry.getValue().getColumnCount(),
                    entry.getValue().getRowCount() ));
        }
        System.out.println(this.getCharString("*", 64));
    }
    
    
    
    
    public static void main(String[] args){
        DetectorPulseDecoder fadc = new DetectorPulseDecoder();
        fadc.init(10,"default");
        //fadc.show();
        DetectorChannelDecoder  tt = new DetectorChannelDecoder();
        
        //tt.show();
        
    }
}
