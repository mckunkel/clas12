/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.hipo;

import org.jlab.evio.clas12.EvioDataEvent;

/**
 *
 * @author gavalian
 */
public class HipoBenchmark {
    public static void main(String[] args){
       

        String filename = "/Users/gavalian/Work/Software/Release-8.0/COATJAVA/coatjava/combinedFile.hipo";
        for(int k = 0; k < 100; k++){
            
            
            HipoDataSource reader = new HipoDataSource();
            reader.open(filename);
            int nevents = reader.getSize();
            System.out.println(" N EVENTS = " + nevents);
            
            int counter = 0;
            for(int i = 0; i < nevents; i++){
                EvioDataEvent  event = (EvioDataEvent) reader.gotoEvent(i);
                //EvioDataBank bank = event.getBank("HEADER::info");
                //bank.show();
                //event.show();
                counter++;
            }
            System.out.println("  procecessed " + counter + "  events");
        }
        //reader.show();

    }
}
