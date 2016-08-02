/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.data.io.hipo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jlab.data.io.DataEvent;
import org.jlab.data.io.DataSync;
import org.jlab.evio.clas12.EvioDataDictionary;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.evio.clas12.EvioFactory;
import org.jlab.evio.clas12.EvioSource;
import org.jlab.hipo.io.HipoWriter;

/**
 *
 * @author gavalian
 */
public class HipoDataSync implements DataSync {
    
    HipoWriter writer = null;
        
    public HipoDataSync(){
        this.writer = new HipoWriter();
    }
    
    public void open(String file) {
        this.writer.open(file);
        EvioDataDictionary  dict = EvioFactory.getDictionary();
        String[] descList = dict.getDescriptorList();
        for(String desc : descList){
            String descString = dict.getDescriptor(desc).toString();
            this.writer.addHeader(descString);
        }
    }

    public void writeEvent(DataEvent event) {
        EvioDataEvent  evioEvent = (EvioDataEvent) event;
        this.writer.writeEvent(evioEvent.getEventBuffer().array());
    }

    public void close() {
        this.writer.close();
    }
    
    public void setCompressionType(int type){
        this.writer.setCompressionType(type);
    }
    public static void printUsage(){
        System.out.println("\tUsage: convert -[option] output.hipo input.evio [input2.evio] [input3.evio]");
            System.out.println("\n\t Options :");
            System.out.println("\t\t -u    : uncompressed");
            System.out.println("\t\t -gzip : gzip compression");
            System.out.println("\t\t -lz4  : lz4 compression");
            System.out.println("\n");
    }
    public static void main(String[] args){
        
        if(args[0].startsWith("-")==false){
            System.out.println("\n\n--> please provide compression type");
            HipoDataSync.printUsage();
            System.exit(0);
        }
        
        int compressionType = -1;
        
        if(args[0].compareTo("-u")==0){
            compressionType = 0;
        }
        
        if(args[0].compareTo("-gzip")==0){
            compressionType = 1;
        }
        
        if(args[0].compareTo("-lz4")==0){
            compressionType = 2;
        }
        
        if(compressionType<0){
            HipoDataSync.printUsage();
            System.out.println("[error] ---> compression type string is invalid.");
            System.exit(0);
        }
        String outputFile       = args[1];
        List<String> inputFiles = new ArrayList<String>();
        
        for(int i = 2; i < args.length; i++){
            inputFiles.add(args[i]);
        }
        
        File outFile = new File(outputFile);
        
        if(outFile.exists()==true){
            System.out.println("\n[error] ---> can not overwrite existing file.\n\n");
            System.exit(0);
        }
        
        HipoDataSync  writer = new HipoDataSync();
        writer.setCompressionType(compressionType);
        writer.open(outputFile);
        for(String inFile : inputFiles){
            EvioSource reader = new EvioSource();
            reader.open(inFile);
            while(reader.hasEvent()){
                EvioDataEvent event = (EvioDataEvent) reader.getNextEvent();
                writer.writeEvent(event);
            }
        }
        writer.close();
    }
}
