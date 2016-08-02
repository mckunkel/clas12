/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.detector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.jlab.clas.detector.BankType;
import org.jlab.clas.detector.DetectorBankEntry;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.clas.detector.DetectorRawData;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas.tools.benchmark.Benchmark;
import org.jlab.clas.tools.benchmark.BenchmarkTimer;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.evio.clas12.EvioSource;
import org.jlab.io.decode.AbsDetectorTranslationTable;
import org.root.histogram.H1D;
import org.root.histogram.H2D;
import org.root.pad.TGCanvas;

/**
 *
 * @author gavalian
 */
public class EventDecoder {
    
    RawEventDecoder rawDecoder = null;
    
    AbsDetectorTranslationTable  table = new AbsDetectorTranslationTable();
    List<DetectorBankEntry>      dataEntries = null;
    Map<DetectorType,List<DetectorCounter> >  counters = new TreeMap<DetectorType,List<DetectorCounter>>();
    Benchmark                                benchMark = new Benchmark();
    Map<DetectorType,IFADCFitter>             pulseFitters = new TreeMap<DetectorType,IFADCFitter>();
    FADCConfigLoader                          fadcLoader   = new FADCConfigLoader();
    
    public EventDecoder(){
        
        
        rawDecoder = new RawEventDecoder();
        Set<String>     detectors = rawDecoder.getDetectorList();
        benchMark.addTimer("RAW-DECODER");
        benchMark.addTimer("RAW-TR");
        benchMark.addTimer("OBJ-CREATE");
        fadcLoader.load("/test/fc/fadc",10,"default");
        /*
        for(String detector : detectors){
            pulseFitters.put(DetectorType.getType(detector), new FADCBasicFitter(0,30,35,70));
        }*/
        //table.readFile("/Users/gavalian/Work/Software/Release-8.0/COATJAVA/coatjava/etc/bankdefs/translation/FTOF1A.table");
        //this.decoder.addTranslationTable(table);
    }
    
    public List<DetectorBankEntry>   getDataEntries(String type){
        return this.getDataEntries(DetectorType.getType(type));
    }
    
    public List<DetectorBankEntry>   getDataEntries(DetectorType type){
        List<DetectorBankEntry> dataList = new ArrayList<DetectorBankEntry>();
        for(DetectorBankEntry data : this.dataEntries){
            if(data.getDescriptor().getType()==type){
                dataList.add(data);
            }
        }
        return dataList;
    }
    
    public void addFitter(DetectorType type, IFADCFitter fitter){
        this.pulseFitters.put(type, fitter);
    }
    
    public void  readDataEntries(EvioDataEvent event){
        this.dataEntries = this.rawDecoder.getDataEntries(event);
    }
    
    public void  decode(EvioDataEvent event){
        
        this.benchMark.resume("RAW-DECODER");
        this.dataEntries = this.rawDecoder.getDataEntries(event);
        this.benchMark.pause("RAW-DECODER");
        this.benchMark.resume("RAW-TR");
        
        this.rawDecoder.decode(dataEntries);
        
        this.benchMark.pause("RAW-TR");
        
        
        counters.clear();
        
        Set<String>  detectors = rawDecoder.getDetectorList();
        Map<Integer,DetectorCounter>   counterList = new TreeMap<Integer,DetectorCounter>();
        
        this.benchMark.resume("OBJ-CREATE");
        for(String detector : detectors){
            List<DetectorBankEntry>  entries = this.getDataEntries(detector);
            /*
            DetectorType  type = DetectorType.getType(detector);
            this.counters.put(type,new ArrayList<DetectorCounter>());
            counterList.clear();
            
            for(DetectorBankEntry  entry : entries){
                int hash = entry.getDescriptor().getHashCode();
                if(counterList.containsKey(hash)==false){
                    counterList.put(hash, new DetectorCounter(entry.getDescriptor()));
                }
                
                DetectorCounter  counter = counterList.get(hash);
                int channel = entry.getDescriptor().getOrder()/2;
                if(counter.getChannels().size()<(channel+1)){
                    counter.addChannel(new DetectorChannel());
                }
                
                if(entry.getType()==BankType.TDC){
                    counter.addTDC(channel, ( (int[]) entry.getDataObject())[0]);
                }
                //System.out.println(counter);
                //System.out.println(" ORDER = " + entry.getDescriptor().getOrder() + 
                //        "  CHANNEL = " + channel);
                //System.out.println(" DETECTOR " + entry.getType() 
                //        + "  " + counter);
                //counter.addADC(channel, 10);
            }
            */
        }
        this.benchMark.pause("OBJ-CREATE");        
    }
    
    
    public List<DetectorCounter>   getDetectorCounters(DetectorType t){
        
        List<DetectorCounter>    counters = new ArrayList<DetectorCounter>();
        
        List<DetectorBankEntry>  entries = this.getDataEntries(t);
        Map<Integer,DetectorCounter>   counterList = new TreeMap<Integer,DetectorCounter>();
        
        for(DetectorBankEntry entry : entries){
            int hash = entry.getDescriptor().getHashCode();
            if(counterList.containsKey(hash)==false){
                counterList.put(hash, new DetectorCounter(entry.getDescriptor()));
            }
            
            DetectorCounter  counter = counterList.get(hash);
            
            int channel = 0;
            int order   = entry.getDescriptor().getOrder();
            switch (order) {
                case 0: channel = 0; break;
                case 1: channel = 1; break;
                case 2: channel = 0; break;
                case 3: channel = 1; break;
            }
            
            if(counter.getChannels().size()<(channel+1)){
                counter.addChannel(new DetectorChannel());
            }
            
            if(entry.getType()==BankType.TDC){
                counter.addTDC(channel, ( (int[]) entry.getDataObject())[0]);
            }
            
            if(entry.getType()==BankType.ADCPULSE){
                short[] adc = (short[]) entry.getDataObject();
                counter.addADCPulse(channel, adc);
                if(this.pulseFitters.containsKey(t)==true){
                    IFADCFitter fitter = this.pulseFitters.get(t);
                    fitter.fit(counter.getChannels().get(channel));
                } else {
                    System.out.println(" CAN NOT FIND FITTER FOR Detector Type = " + t);
                }
            }
            if(entry.getType()==BankType.ADCFPGA){
                int[] adc = (int[]) entry.getDataObject();
                int ped = adc[2];
                FADCConfig  config = fadcLoader.getMap().get(
                        entry.getDescriptor().getCrate(),
                        entry.getDescriptor().getSlot(),
                        entry.getDescriptor().getChannel());
                int nsab = config.getNSA()+config.getNSB();
                //System.out.println("NSAB = " + nsab);
                int adcc = adc[1]-nsab*ped;
                counter.addADC(channel, adcc);
            }
            
            if(entry.getType()==BankType.SVT){
                int[] svt = (int[]) entry.getDataObject();
                counter.addADC(0, svt[2]);
                counter.addTDC(0, svt[3]);
            }
        }
        
        for(Map.Entry<Integer,DetectorCounter> item : counterList.entrySet()){
            //System.out.println(item.getValue());
            counters.add(item.getValue());
        }
        return counters;
    }
    
    public void showTimer(){
        System.out.println(this.benchMark.toString());
    }
    
    public List<DetectorBankEntry>  getDataEntries(){
        return this.dataEntries;
    }
    
    public static H1D  getADCPulse(DetectorBankEntry entry){
        short[] pulse = (short[]) entry.getDataObject();
        int nbins = pulse.length;
        H1D h1 = new H1D(DetectorDescriptor.getName("ADC_", 
                entry.getDescriptor().getCrate(),
                entry.getDescriptor().getSlot(),
                entry.getDescriptor().getChannel()
                ),nbins,0.5,nbins + 0.5);
        for(int loop = 0; loop < nbins; loop++) h1.setBinContent(loop, pulse[loop]);
        return h1;
    }
    
    public List<DetectorCounter>  getCounters(EvioDataEvent event){
        /*
        List<DetectorCounter>  counterList = new ArrayList<DetectorCounter>();
        List<DetectorRawData> rawData = decoder.getDataEntries(event);
        decoder.decode(rawData);
        List<DetectorRawData> trData  =  decoder.getDetectorData(rawData, DetectorType.FTOF1A);
        for(DetectorRawData data : trData){
            System.out.println(data);
        }
        
        Map<Integer,DetectorCounter>  counters = new TreeMap<Integer,DetectorCounter>();
        for(DetectorRawData data : trData){
            
            int hash = data.getDescriptor().getHashCode();
            if(counters.containsKey(hash)==false){
                counters.put(hash, new DetectorCounter(DetectorType.FTOF1A,
                        data.getDescriptor().getSector(),data.getDescriptor().getLayer(),
                        data.getDescriptor().getComponent()));
            }
            
            DetectorCounter counter = counters.get(hash);
            
            int tube  = data.getDescriptor().getOrder()%2;
            int value = data.getDescriptor().getOrder()/2;
            
            System.out.println(" ORDER = " + data.getDescriptor().getOrder() 
                    + "  tube = " + tube + " value = " + value);
            
            
            if(value==0){
                System.out.println("adding " + tube + " " + value + " " + counter.getChannels().size());
                counter.addADC(tube,1000);
            }
            /*
           
            //System.out.println(data);
        }
        
        for(Map.Entry<Integer,DetectorCounter>  entry : counters.entrySet()){
            counterList.add(entry.getValue());
        }
        return counterList;
        */
        return null;
    }
    
    
    public static void main(String[] args){
        
        /*
        DetectorCounter counter = new DetectorCounter(DetectorType.EC,1,1,1);
        counter.addADC(2, 150);
        counter.addADC(1, 140);
        counter.addADC(0, 100);
        counter.addTDC(0, 120);
        counter.addADC(0, 130);
        counter.addTDC(1, 140);

        
        System.out.println(counter);
        */
        
        String input = "/Users/gavalian/Work/Software/Release-8.0/COATJAVA/FC/sector2_000233_mode7.evio.0";
        //String input = "/Users/gavalian/Work/Software/Release-8.0/COATJAVA/FC/sector2_000229_mode1.evio.0";
        //String input = "/Users/gavalian/Work/Software/Release-8.0/COATJAVA/sector1_000139.evio";
        //String input = "/Users/gavalian/Work/Software/Release-8.0/COATJAVA/SVT/svt257er_000017.evio.0";
        EvioSource  reader = new EvioSource();
        reader.open(input);
        EventDecoder decoder = new EventDecoder();
        
        decoder.addFitter(DetectorType.FTOF1A, new FADCBasicFitter(30,35,70,75));
        int icounter = 0;
        H1D hADC = new H1D("hADC",100,0.0,14000.0);
        H1D hTDC = new H1D("hTDC",100,-2000.0,2000.0);
        H2D hADCPADDLE = new H2D("hADCPADDLE",23,0.5,23.5,40,0.0,14000.0);
        H2D hTDCPADDLE = new H2D("hTDCPADDLE",23,0.5,23.5,40,-2000.0,2000.0);
        
        hADC.setLineWidth(2);
        hADC.setFillColor(3);
        hTDC.setLineWidth(2);
        hTDC.setFillColor(6);
        
        hADC.setXTitle("ADCL+ADCR");
        hTDC.setXTitle("TDCL-TDCR");
        hADCPADDLE.setXTitle("FTOF1A PADDLE #");
        hTDCPADDLE.setXTitle("FTOF1A PADDLE #");
        hADCPADDLE.setYTitle("ADCL+ADCR");
        hTDCPADDLE.setYTitle("TDCL-TDCR");
        
        
        while(reader.hasEvent()){
        //while(icounter<15){
            icounter++;
             EvioDataEvent event = (EvioDataEvent) reader.getNextEvent();
             //System.out.println("---------------> event  # " + icounter);
             decoder.decode(event);
             
             
             //List<DetectorBankEntry> counters =  decoder.getDataEntries("FTOF1A");
             /*
             List<DetectorBankEntry> counters =  decoder.getDataEntries();
             System.out.println(" DATA ENTRIES SIZE = " + counters.size());
             for(DetectorBankEntry cnt : counters){
                 System.out.println(cnt);
             }*/
             /*
             List<DetectorBankEntry> countersPCAL =  decoder.getDataEntries("PCAL");
             for(DetectorBankEntry cnt : countersPCAL){
                 //System.out.println(cnt);
             }*/
             //System.out.println("---------------> COUNTERS  # " + icounter);
             /*decoder.getDetectorCounters(DetectorType.FTOF1A);
             for(DetectorBankEntry cnt : counters){
                 System.out.println(cnt);
                 if(cnt.getType()==BankType.ADCPULSE){
                     H1D hp = EventDecoder.getADCPulse(cnt);
                     for(int bin = 0; bin < hp.getxAxis().getNBins();bin++){
                         System.out.println(bin + " " + hp.getBinContent(bin));
                     }
                 }
                 
                 if(cnt.getType()==BankType.TDC){
                     int[] tdc = (int[]) cnt.getDataObject();
                     System.out.println(" TDC VALUE = " + tdc[0]);                     
                 }
                 
                 if(cnt.getType()==BankType.ADC){
                     int[] adc = (int[]) cnt.getDataObject();
                     System.out.println(" ADC VALUE = " + adc[0]);
                 }
                 
                 if(cnt.getType()==BankType.ADCFPGA){
                     int[] adc = (int[]) cnt.getDataObject();
                     System.out.println(" PEDISTAL = " + adc[0] 
                             + "  PULSE = " + adc[1] 
                             + "  MAX   = " + adc[2] 
                             + "  TIME  = " + adc[3]);                     
                 }
                 
                 
             }
             */
             
             List<DetectorCounter> banks = decoder.getDetectorCounters(DetectorType.FTOF1A);
             //System.out.println("----------------------> BANKS FTOF1A " + banks.size());

             for(DetectorCounter bank : banks){
                 //System.out.println(bank.isMultiHit());
                 //System.out.println(bank);
                 if(bank.getChannels().size()==2){
                     if(bank.isMultiHit()==false){
                         // isMultihit() method returns false when
                         //  (bank.getChannels().get(0).getADC().size()==1&&
                         //  bank.getChannels().get(1).getADC().size()==1&&
                         //  bank.getChannels().get(0).getTDC().size()==1&&
                         //  bank.getChannels().get(1).getTDC().size()==1)
                         // it checks if each channel has one ADC and one TDC.
                         int adcL = bank.getChannels().get(0).getADC().get(0);
                         int adcR = bank.getChannels().get(1).getADC().get(0);
                         int tdcL = bank.getChannels().get(0).getTDC().get(0);
                         int tdcR = bank.getChannels().get(1).getTDC().get(0);
                         hADC.fill(adcL+adcR);
                         hTDC.fill(tdcL-tdcR);
                         int paddle = bank.getDescriptor().getComponent();
                         hADCPADDLE.fill(paddle, adcL+adcR);
                         hTDCPADDLE.fill(paddle, tdcL-tdcR);
                     }
                 }
             }
             /*
             List<DetectorCounter> banksPCAL = decoder.getDetectorCounters(DetectorType.SVT);
             //System.out.println("----------------------> BANKS PCAL");
             for(DetectorCounter bank : banksPCAL){
                 System.out.println(bank);
             }*/
             
        }
        System.out.println("done...");
        decoder.showTimer();
        TGCanvas c1 = new TGCanvas("c1","FTOF1A",1200,800,2,2);
        c1.cd(0);
        c1.draw(hADC);
        c1.cd(1);
        c1.draw(hTDC);
        c1.cd(2);
        c1.draw(hADCPADDLE);
        c1.cd(3);
        c1.draw(hTDCPADDLE);
    }
}
