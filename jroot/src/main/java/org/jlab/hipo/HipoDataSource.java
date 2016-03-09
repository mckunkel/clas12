/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.hipo;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jlab.data.io.DataEvent;
import org.jlab.data.io.DataEventList;
import org.jlab.data.io.DataSource;
import org.jlab.evio.clas12.EvioDataDictionary;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.evio.clas12.EvioFactory;

/**
 *
 * @author gavalian
 */
public class HipoDataSource implements DataSource {
    
    HipoReader reader = null;
    private int  sourcePosition = 0;
    private EvioDataDictionary dictionary = new EvioDataDictionary();
    
    public HipoDataSource(){
        
        String         dictionaryPath = "some";
        String CLAS12DIR = System.getenv("CLAS12DIR");
        String CLAS12DIRPROP = System.getProperty("CLAS12DIR");
        
        if(CLAS12DIR==null){
            System.out.println("---> Warning the CLAS12DIR environment is not defined.");
            //return;
        } else {
            dictionaryPath = CLAS12DIR + "/etc/bankdefs/clas12";
        }
        
        if(CLAS12DIRPROP==null){
            System.out.println("---> Warning the CLAS12DIR property is not defined.");
        } else {
            dictionaryPath = CLAS12DIRPROP + "/etc/bankdefs/clas12";
        }

        if(CLAS12DIRPROP==null&&CLAS12DIR==null){
            return;
        }
        //dictionary.initWithDir(dictionaryPath);
        //System.err.println("[EvioSource] ---> Loaded bank Descriptors from    : " +
        //        dictionaryPath);
        //System.err.println("[EvioSource] ---> Factory loaded descriptor count : " 
        //+ dictionary.getDescriptorList().length);
        
        EvioFactory.loadDictionary(dictionaryPath);
        dictionary = EvioFactory.getDictionary();
        System.err.println("[EvioSource] ---> Factory loaded descriptor count : " 
                + dictionary.getDescriptorList().length);
        dictionary.show();
    }
    
    public HipoDataSource(String filename){
        this.open(filename);
    }
    
    public final void open(String filename){
        this.reader = new HipoReader();
        this.reader.open(filename);
        this.sourcePosition = 0;
    }
    
    
    public boolean hasEvent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void open(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void open(ByteBuffer buff) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getSize() {
        return this.reader.getEventCount();
    }

    public DataEventList getEventList(int start, int stop) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public DataEventList getEventList(int nrecords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public DataEvent getNextEvent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public DataEvent getPreviousEvent() {
        return null;
    }

    public void show(){
        //this.reader.show();
        System.out.println(this.reader.getStatusString());
    }
    
    public DataEvent gotoEvent(int index) {
        byte[] eventBytes = this.reader.readEvent(index);
        EvioDataEvent event = new EvioDataEvent(eventBytes,ByteOrder.LITTLE_ENDIAN,this.dictionary);
        return event;
    }

    public void reset() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getCurrentIndex() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/Release-8.0/COATJAVA/coatjava/combinedFile.hipo";
        
        HipoDataSource  reader = new HipoDataSource(file);
        
    }
}
