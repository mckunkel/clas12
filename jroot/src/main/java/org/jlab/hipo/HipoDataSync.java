/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.hipo;

import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.evio.clas12.EvioSource;

/**
 *
 * @author gavalian
 */
public class HipoDataSync {
    
    public static void main(String[] args){
        int nfiles = args.length;
        System.out.println("[HipoDataSync-writer] ---> combining " + nfiles + " files.");
        HipoWriter  writer = new HipoWriter();
        writer.setCompression(true);
        writer.open(args[0]);
        
        for(int loop = 0; loop < nfiles-1;loop++){
            EvioSource reader = new EvioSource();
            reader.open(args[loop+1]);
            while(reader.hasEvent()){
                EvioDataEvent event = (EvioDataEvent) reader.getNextEvent();
                byte[]  eventBuffer = event.getEventBuffer().array();
                writer.writeEvent(eventBuffer);
            }
            reader.close();
        }
        
        writer.close();
    }
}
