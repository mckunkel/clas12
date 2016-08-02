/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.detector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.clas.detector.DetectorBankEntry;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clasrec.utils.DatabaseConstantProvider;
import org.jlab.containers.HashTable;
import org.jlab.containers.TableRow;

/**
 *
 * @author gavalian
 */
public class DetectorChannelDecoder {
    private Map<DetectorType,HashTable>   translationTable = new HashMap<DetectorType,HashTable>();
    private Map<DetectorType,String>      detectorTables = new HashMap<DetectorType,String>();
    private int                           debugMode      = 0;
    
    public DetectorChannelDecoder(){
        detectorTables.put(DetectorType.EC, "/daq/tt/ec");
        detectorTables.put(DetectorType.FTOF, "/daq/tt/ftof");
        detectorTables.put(DetectorType.FTCAL, "/daq/tt/ftcal");
        detectorTables.put(DetectorType.FTHODO, "/daq/tt/fthodo");
        detectorTables.put(DetectorType.LTCC, "/daq/tt/ltcc");
    }
    
    public DetectorChannelDecoder(int run, String variation){
        detectorTables.put(DetectorType.EC, "/daq/tt/ec");
        detectorTables.put(DetectorType.FTOF, "/daq/tt/ftof");
        detectorTables.put(DetectorType.FTCAL, "/daq/tt/ftcal");
        detectorTables.put(DetectorType.FTHODO, "/daq/tt/fthodo");
        detectorTables.put(DetectorType.LTCC, "/daq/tt/ltcc");
        this.init(run, variation);
    }
    
    public void setDebug(int level){
        this.debugMode = level;
    }
    
    public void add(String type, String table){
        if(detectorTables.containsKey(DetectorType.getType(type))==true){
            System.out.println("[DetectorPulseDecoder] --> overwritting table definition " + type);
        }
        detectorTables.put(DetectorType.getType(type), table);
    }
    
    public void clear(){
        this.detectorTables.clear();
        this.translationTable.clear();
    }
    
    public void add(DetectorType type, String table){
        this.add(type.getName(), table);
    }
    
    public final void init(int run, String variation){
        DatabaseConstantProvider provider = new DatabaseConstantProvider(run,variation);
        for(Map.Entry<DetectorType,String> entry : this.detectorTables.entrySet()){
            HashTable  table = provider.readTable(entry.getValue());
            this.translationTable.put(entry.getKey(), table);
        }
        //HashTable  table = provider.readTable("/daq/fadc/ec");
        //HashTable  table = provider.readTable("/daq/fadc/ec");
        //table.show();        
        provider.disconnect();
        this.show();
    }
    
    public HashTable  getTable(DetectorType type){
        return this.translationTable.get(type);
    }
    
    public HashTable  getTable(String type){
        return this.translationTable.get(DetectorType.getType(type));
    }
    
    public String getCharString(String symbol,int length) {
        StringBuilder str = new StringBuilder();
        for(int i=0;i<length;i++) str.append(symbol);
        return str.toString();
    }
    
    public DetectorDescriptor  getDescriptor(int... index){
        DetectorDescriptor desc = new DetectorDescriptor();        
        if(index.length>2){
            desc.setCrateSlotChannel(index[0], index[1], index[2]);
        }
        for(Map.Entry<DetectorType,HashTable> table : this.translationTable.entrySet()){
                TableRow  row = table.getValue().getRow(index);
                if(row!=null){
                    Integer sector = (Integer) row.get(0);
                    Integer layer  = (Integer) row.get(1);
                    Integer component = (Integer) row.get(2);
                    Integer order = (Integer) row.get(3);
                    desc.setSectorLayerComponent(sector, layer, component);
                    desc.setOrder(order);
                }
        }
        return desc;
    }
    
    public void decode(List<DetectorBankEntry> entries){
        
        for(DetectorBankEntry entry : entries){
            int crate   = entry.getDescriptor().getCrate();
            int slot    = entry.getDescriptor().getSlot();
            int channel = entry.getDescriptor().getChannel();
            boolean decoded = false;
            for(Map.Entry<DetectorType,HashTable> table : this.translationTable.entrySet()){
                TableRow  row = table.getValue().getRow(crate,slot,channel);
                if(row!=null){
                    Integer sector = (Integer) row.get(0);
                    Integer layer  = (Integer) row.get(1);
                    Integer component = (Integer) row.get(2);
                    Integer order = (Integer) row.get(3);
                    entry.getDescriptor().setSectorLayerComponent(sector, layer, component);
                    entry.getDescriptor().setOrder(order);
                    entry.getDescriptor().setType(table.getKey());
                    decoded = true;
                }
            }
            if(decoded==false&&this.debugMode>0){
                System.out.println(
                        String.format("warning : unable to decode entry for CRATE = %3d SLOT = %3d CHANNEL = %3d",
                                entry.getDescriptor().getCrate(),
                                entry.getDescriptor().getSlot(),
                                entry.getDescriptor().getChannel())
                );
            }
        }
    }
    
    public void show(){
        System.out.println("----->  Detector CHANNEL Decoder");
        System.out.println(this.getCharString("*", 64));
        for(Map.Entry<DetectorType,HashTable>  entry : this.translationTable.entrySet()){
            System.out.println(String.format("* %12s :  COLUMNS = %12d , ROWS = %12d *", 
                    entry.getKey().toString(),entry.getValue().getColumnCount(),
                    entry.getValue().getRowCount() ));
        }
        System.out.println(this.getCharString("*", 64));
    }
    
    public void dump(String name){
        if(this.translationTable.containsKey(DetectorType.getType(name))==true){
            HashTable table = this.translationTable.get(DetectorType.getType(name));
            table.show();
        }
    }
    
    
}
