/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.detector;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.clas.detector.BankType;
import org.jlab.clas.detector.DetectorBankEntry;
import org.jlab.clas.detector.DetectorRawBank;
import org.jlab.clas.detector.DetectorRawData;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas.tools.utils.DataUtils;
import org.jlab.clas.tools.utils.FileUtils;
import org.jlab.clas.tools.utils.ResourcesUtils;
import org.jlab.clas12.raw.EvioTreeBranch;
import org.jlab.clas12.raw.RawDataEntry;
import org.jlab.coda.jevio.ByteDataTransformer;
import org.jlab.coda.jevio.CompositeData;
import org.jlab.coda.jevio.DataType;
import org.jlab.coda.jevio.EvioException;
import org.jlab.coda.jevio.EvioNode;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.evio.clas12.EvioSource;
import org.jlab.io.decode.AbsDetectorTranslationTable;
import org.jlab.io.decode.EvioRawEventDecoder;
import org.jlab.io.decode.IDetectorTranslationTable;

/**
 *
 * @author gavalian
 */
public class RawEventDecoder {
    private TreeMap<String,IDetectorTranslationTable> translationTables
            = new TreeMap<String,IDetectorTranslationTable>();
    SVTTranslationTable                       trSVT = null;
    
    public RawEventDecoder(){
        this.init();        
    }
    
    
    public final void init(){
        //String  clas12dir = System.getenv("CLAS12DIR");
        String  clas12dir = ResourcesUtils.getResourceDir("CLAS12DIR", "etc/bankdefs/translation");
        if(clas12dir!=null){

            List<String>  tableList = FileUtils.getFilesInDir(clas12dir);
            System.out.println("----> number of translation tables = " + tableList.size());
            for(String tableFile : tableList){
                AbsDetectorTranslationTable table = new AbsDetectorTranslationTable();
                table.readFile(tableFile);
                System.out.println("---> loading tanslation table [" + table.getName() + "] : " + tableFile);
                this.addTranslationTable(table);
            }
        } else {
            System.out.println("--------> ERROR no environment is set.....");
        }
        this.trSVT = new SVTTranslationTable();
    }
    
    public Set<String>  getDetectorList(){
        return this.translationTables.keySet();
    }
    
    public void decode(List<DetectorBankEntry> dataEntries){
        for(DetectorBankEntry data : dataEntries){
            for(Map.Entry<String,IDetectorTranslationTable> tr : this.translationTables.entrySet()){
                if(data.getDescriptor().getType()!=DetectorType.UNDEFINED) break;
                if(tr.getKey().compareTo("SVT")!=0){
                    //System.out.println(" TRYNIG WITH MODULE : " + tr.getKey());
                    if(tr.getValue().getSector(data.getDescriptor().getCrate(), 
                            data.getDescriptor().getSlot(), 
                            data.getDescriptor().getChannel())>0){
                        int sector = tr.getValue().getSector(
                                data.getDescriptor().getCrate(), 
                                data.getDescriptor().getSlot(), 
                                data.getDescriptor().getChannel());
                        int layer = tr.getValue().getLayer(
                                data.getDescriptor().getCrate(), 
                                data.getDescriptor().getSlot(), 
                                data.getDescriptor().getChannel());
                        int component = tr.getValue().getComponent(
                                data.getDescriptor().getCrate(), 
                                data.getDescriptor().getSlot(), 
                                data.getDescriptor().getChannel());
                        
                        int order = tr.getValue().getOrder( 
                                data.getDescriptor().getCrate(), 
                                data.getDescriptor().getSlot(), 
                                data.getDescriptor().getChannel()
                        );
                        data.getDescriptor().setSectorLayerComponent(sector,layer,component);
                        data.getDescriptor().setType(tr.getValue().getDetectorType());
                        data.getDescriptor().setOrder(order);
                        //data.getDescriptor().setType(tr.);
                    }
                }
            }
        }
        //Collections.sort(dataEntries);
    }    
        
    public DetectorRawBank  getDecodedData(List<DetectorRawData> dataEntries, IDetectorTranslationTable table){
        DetectorType    type = DetectorType.getType(table.getName());
        DetectorRawBank bank = new DetectorRawBank(DetectorType.getType(table.getName()));
        //ArrayList<RawDataEntry>  rawdata = new ArrayList<RawDataEntry>();
        for(DetectorRawData data : dataEntries){
            if(table.getSector(data.getDescriptor().getCrate(),
                    data.getDescriptor().getSlot(), 
                    data.getDescriptor().getChannel())>0){
                int sector = table.getSector(data.getDescriptor().getCrate(), 
                        data.getDescriptor().getSlot(), 
                        data.getDescriptor().getChannel());
                int layer = table.getLayer(data.getDescriptor().getCrate(), 
                        data.getDescriptor().getSlot(), 
                        data.getDescriptor().getChannel());
                int component = table.getComponent(data.getDescriptor().getCrate(), 
                        data.getDescriptor().getSlot(), 
                        data.getDescriptor().getChannel());
                data.setType(type);                
                data.getDescriptor().setSectorLayerComponent(sector,layer,component);
                bank.add(data);
                //rawdata.add(data);
            }
        }
        return bank;
    }
    
    
    

    public DetectorRawData  getDataEntry(List<DetectorRawData> data, int sector,
           int layer, int component ){
        for(DetectorRawData item : data){
            if(item.getDescriptor().getSector()==sector&&
                    item.getDescriptor().getLayer()==layer&&
                    item.getDescriptor().getComponent()==component){
                return item;
            }
        }
        return null;
    }
    
    
    
    public void addTranslationTable(IDetectorTranslationTable table){
        this.translationTables.put(table.getName(), table);
    }
    
    
    public EvioTreeBranch  getEventBranch(ArrayList<EvioTreeBranch> branches, int tag){
        for(EvioTreeBranch branch : branches){
            if(branch.getTag()==tag) return branch;
        }
        return null;
    }
    
    public List<DetectorBankEntry>  getDataEntriesTDC(EvioDataEvent event){
        
        ArrayList<DetectorBankEntry> tdcEntries = new ArrayList<DetectorBankEntry>();        
        ArrayList<EvioTreeBranch> branches = this.getEventBranches(event);
        
        for(EvioTreeBranch branch : branches){
            int  crate = branch.getTag();
            EvioTreeBranch cbranch = this.getEventBranch(branches, branch.getTag());
            for(EvioNode node : cbranch.getNodes()){ 
                if(node.getTag()==57607){
                    int[] intData = ByteDataTransformer.toIntArray(node.getStructureBuffer(false));
                    for(int loop = 0; loop < intData.length; loop++){
                        int  dataEntry = intData[loop];
                        int  slot      = DataUtils.getInteger(dataEntry, 27, 31 );
                        int  chan      = DataUtils.getInteger(dataEntry, 19, 25);
                        int  value     = DataUtils.getInteger(dataEntry,  0, 18);
                        
                        DetectorBankEntry  tdc = new DetectorBankEntry(crate,slot,chan);                        
                        tdc.setData(BankType.TDC, new int[]{value});
                        tdcEntries.add(tdc);
                    }
                }
            }
        }
        return tdcEntries;
    }
    /*
    public ArrayList<RawDataEntry> getDataEntries(EvioDataEvent event){
        ArrayList<RawDataEntry>  rawEntries = new ArrayList<RawDataEntry>();
        List<EvioTreeBranch> branches = this.getEventBranches(event);
        for(EvioTreeBranch branch : branches){
            ArrayList<RawDataEntry>  list = this.getDataEntries(event,branch.getTag());
            if(list != null){
                rawEntries.addAll(list);
            }
        }
        return rawEntries;
    }*/
    
    public ArrayList<DetectorBankEntry> getDataEntries(EvioDataEvent event){
        ArrayList<DetectorBankEntry>  rawEntries = new ArrayList<DetectorBankEntry>();
        List<EvioTreeBranch> branches = this.getEventBranches(event);
        for(EvioTreeBranch branch : branches){
            List<DetectorBankEntry>  list = this.getDataEntries(event,branch.getTag());
            if(list != null){
                rawEntries.addAll(list);
            }
        }        
        List<DetectorBankEntry>  tdcEntries = this.getDataEntriesTDC(event);
        rawEntries.addAll(tdcEntries);
        return rawEntries;
    }
    
    public List<DetectorBankEntry> getDataEntries(EvioDataEvent event, Integer crate){
        
        ArrayList<EvioTreeBranch> branches = this.getEventBranches(event);
        List<DetectorBankEntry>   bankEntries = new ArrayList<DetectorBankEntry>();
        
        EvioTreeBranch cbranch = this.getEventBranch(branches, crate);
        if(cbranch == null ) return null;
        
        for(EvioNode node : cbranch.getNodes()){ 
            //System.out.println(" analyzing tag = " + node.getTag());
            if(node.getTag()==57617){
                //System.out.println(" GETTING DATA FOR SVT");
                // This MODE is used for SVT
                return this.getDataEntries_57617(crate, node, event);
                //return this.getDataEntriesSVT(crate,node, event);                
            }
            
            if(node.getTag()==57602){
                //  This is regular integrated pulse mode, used for FTOF
                // FTCAL and EC/PCAL
                //return this.getDataEntries_57602(crate, node, event);
                return this.getDataEntries_57602(crate, node, event);
                //return this.getDataEntriesMode_7(crate,node, event);
            }
            
            if(node.getTag()==57601){
                //  This is regular integrated pulse mode, used for FTOF
                // FTCAL and EC/PCAL
                return this.getDataEntries_57601(crate, node, event);
                //return this.getDataEntriesMode_7(crate,node, event);
            }
            
            if(node.getTag()==57627){
                //  This is regular integrated pulse mode, used for FTOF
                // FTCAL and EC/PCAL
                //System.out.println(" Micromega = " + node.getTag());
                List<DetectorBankEntry>  entries = this.getDataEntries_57627(crate, node, event);
                bankEntries.addAll(entries);
                //return this.getDataEntries_57627(crate, node, event);
                //return this.getDataEntriesMode_7(crate,node, event);
            }
            /*
            if(node.getTag()==57622){
                // This is MODE=10 is used for DC, it contains only TDC
                return this.getDataEntries_57622(crate, node, event);
            }*/
        }
        return bankEntries;
        //EvioRawDataBank dataBank = new EvioRawDataBank();
        //return null;
    }
    
    public int[]  getRawBuffer(EvioDataEvent event, int crate){
        ArrayList<EvioTreeBranch> branches = this.getEventBranches(event);
        
        EvioTreeBranch cbranch = this.getEventBranch(branches, crate);
        if(cbranch == null ) return new int[1];
        
        for(EvioNode node : cbranch.getNodes()){ 
            if(node.getTag()==57604){
                // This MODE is used for SVT
                System.out.println("FOUND A NODE WITH TAG 57604 TYPE === " + node.getDataTypeObj());
                if(node.getDataTypeObj() == DataType.INT32 || node.getDataTypeObj()==DataType.UINT32){
                    ByteBuffer buff  =  node.getByteData(true);
                    int[] nodedata = ByteDataTransformer.toIntArray(buff);
                    if(nodedata!=null){
                        return nodedata;
                    }
                }
                //return this.getDataEntriesSVT(crate,node, event);                
            }
            
        }
        //EvioRawDataBank dataBank = new EvioRawDataBank();
        return new int[0];        
    }
    
    public ArrayList<RawDataEntry>  getDataEntries_57601_DEBUG(Integer crate, EvioNode node, EvioDataEvent event){
        ArrayList<RawDataEntry>  entries = new ArrayList<RawDataEntry>();
        if(node.getTag()==57601){
            try {
                ByteBuffer     compBuffer = node.getByteData(true);
                CompositeData  compData = new CompositeData(compBuffer.array(),event.getByteOrder());
                
                List<DataType> cdatatypes = compData.getTypes();
                List<Object>   cdataitems = compData.getItems();

                if(cdatatypes.get(3) != DataType.NVALUE){
                    System.err.println("[EvioRawDataSource] ** error ** corrupted "
                    + " bank. tag = " + node.getTag() + " num = " + node.getNum());
                    return null;
                }
                System.out.println("DEBUG :  SIZE = " + cdatatypes.size());
                
                
                
                
                Byte    slot = (Byte)     cdataitems.get(0);
                Integer trig = (Integer)  cdataitems.get(1);
                Long    time = (Long)     cdataitems.get(2);
                //EvioRawDataBank  dataBank = new EvioRawDataBank(crate, slot.intValue(),trig,time);
                
                
            } catch (EvioException ex) {
                Logger.getLogger(EvioRawEventDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return entries;
    }
    public ArrayList<DetectorBankEntry>  getDataEntries_57601(Integer crate, EvioNode node, EvioDataEvent event){
        
        ArrayList<DetectorBankEntry>  entries = new ArrayList<DetectorBankEntry>();
        if(node.getTag()==57601){
            try {
                
                ByteBuffer     compBuffer = node.getByteData(true);
                CompositeData  compData = new CompositeData(compBuffer.array(),event.getByteOrder());
                
                List<DataType> cdatatypes = compData.getTypes();
                List<Object>   cdataitems = compData.getItems();

                if(cdatatypes.get(3) != DataType.NVALUE){
                    System.err.println("[EvioRawDataSource] ** error ** corrupted "
                    + " bank. tag = " + node.getTag() + " num = " + node.getNum());
                    return null;
                }
                
                int position = 0;
                
                while(position<cdatatypes.size()-4){                     
                    Byte    slot = (Byte)     cdataitems.get(position+0);
                    Integer trig = (Integer)  cdataitems.get(position+1);
                    Long    time = (Long)     cdataitems.get(position+2);
                    //EvioRawDataBank  dataBank = new EvioRawDataBank(crate, slot.intValue(),trig,time);
                    
                    Integer nchannels = (Integer) cdataitems.get(position+3);
                    //System.out.println("Retrieving the data size = " + cdataitems.size()
                    //+ "  " + cdatatypes.get(3) + " number of channels = " + nchannels);
                    position += 4;
                    int counter  = 0;
                    while(counter<nchannels){
                        //System.err.println("Position = " + position + " type =  "
                        //+ cdatatypes.get(position));
                        Byte channel   = (Byte) cdataitems.get(position);
                        Integer length = (Integer) cdataitems.get(position+1);
                        DetectorBankEntry bank = new DetectorBankEntry(crate,slot.intValue(),channel.intValue());
                        //dataBank.addChannel(channel.intValue());
                        short[] shortbuffer = new short[length];
                        for(int loop = 0; loop < length; loop++){
                            Short sample    = (Short) cdataitems.get(position+2+loop);
                            shortbuffer[loop] = sample;
                            //dataBank.addData(channel.intValue(), 
                            //        new RawData(tdc,adc,pmin,pmax));
                        }
                        
                        bank.setData(shortbuffer);
                        bank.setTimeStamp(time);
                        //dataBank.addData(channel.intValue(), 
                        //            new RawData(shortbuffer));
                        entries.add(bank);
                        position += 2+length;
                        counter++;
                    }
                }
                return entries;
                
            } catch (EvioException ex) {
                Logger.getLogger(EvioRawEventDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return entries;
    }
    
    public ArrayList<DetectorBankEntry>  getDataEntries_57627(Integer crate, EvioNode node, EvioDataEvent event){
        
        ArrayList<DetectorBankEntry>  entries = new ArrayList<DetectorBankEntry>();
        if(node.getTag()==57627){
            try {
                
                ByteBuffer     compBuffer = node.getByteData(true);
                CompositeData  compData = new CompositeData(compBuffer.array(),event.getByteOrder());
                
                List<DataType> cdatatypes = compData.getTypes();
                List<Object>   cdataitems = compData.getItems();

                if(cdatatypes.get(3) != DataType.NVALUE){
                    System.err.println("[EvioRawDataSource] ** error ** corrupted "
                    + " bank. tag = " + node.getTag() + " num = " + node.getNum());
                    return null;
                }
                
                int position = 0;
                
                while(position<cdatatypes.size()-4){                     
                    Byte    slot = (Byte)     cdataitems.get(position+0);
                    Integer trig = (Integer)  cdataitems.get(position+1);
                    Long    time = (Long)     cdataitems.get(position+2);
                    //EvioRawDataBank  dataBank = new EvioRawDataBank(crate, slot.intValue(),trig,time);
                    
                    Integer nchannels = (Integer) cdataitems.get(position+3);
                    //System.out.println("Retrieving the data size = " + cdataitems.size()
                    //+ "  " + cdatatypes.get(3) + " number of channels = " + nchannels);
                    position += 4;
                    int counter  = 0;
                    while(counter<nchannels){
                        //System.err.println("Position = " + position + " type =  "
                        //+ cdatatypes.get(position));
                        Short  channel   = (Short) cdataitems.get(position);
                        Integer length = (Integer) cdataitems.get(position+1);
                        DetectorBankEntry bank = new DetectorBankEntry(crate,slot.intValue(),channel.intValue());
                        //dataBank.addChannel(channel.intValue());
                        short[] shortbuffer = new short[length];
                        for(int loop = 0; loop < length; loop++){
                            Short sample    = (Short) cdataitems.get(position+2+loop);
                            shortbuffer[loop] = sample;
                            //dataBank.addData(channel.intValue(), 
                            //        new RawData(tdc,adc,pmin,pmax));
                        }
                        
                        bank.setData(shortbuffer);
                        bank.setTimeStamp(time);
                        //dataBank.addData(channel.intValue(), 
                        //            new RawData(shortbuffer));
                        entries.add(bank);
                        position += 2+length;
                        counter++;
                    }
                }
                return entries;
                
            } catch (EvioException ex) {
                Logger.getLogger(EvioRawEventDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return entries;
    }
    
    public ArrayList<RawDataEntry>  getDataEntries_57602_DEBUG(Integer crate, EvioNode node, EvioDataEvent event){
        ArrayList<RawDataEntry>  entries = new ArrayList<RawDataEntry>();
        
        if(node.getTag()==57602){
            try {
                ByteBuffer     compBuffer = node.getByteData(true);
                CompositeData  compData = new CompositeData(compBuffer.array(),event.getByteOrder());
                
                List<DataType> cdatatypes = compData.getTypes();
                List<Object>   cdataitems = compData.getItems();
                System.out.println("DEBUG :  SIZE = " + cdatatypes.size());
            } catch (EvioException ex) {
                Logger.getLogger(EvioRawEventDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return entries;
    }
    
    public ArrayList<DetectorBankEntry>  getDataEntries_57602(Integer crate, EvioNode node, EvioDataEvent event){
        ArrayList<DetectorBankEntry>  entries = new ArrayList<DetectorBankEntry>();
        
        if(node.getTag()==57602){
            try {
                ByteBuffer     compBuffer = node.getByteData(true);
                CompositeData  compData = new CompositeData(compBuffer.array(),event.getByteOrder());
                
                List<DataType> cdatatypes = compData.getTypes();
                List<Object>   cdataitems = compData.getItems();
                
                if(cdatatypes.get(3) != DataType.NVALUE){
                    System.err.println("[EvioRawDataSource] ** error ** corrupted "
                    + " bank. tag = " + node.getTag() + " num = " + node.getNum());
                    return null;
                }

                int position = 0;
                //System.out.println("N-VALUE = " + cdataitems.get(3).toString());
                while((position+4)<cdatatypes.size()){                    
                
                    Byte    slot = (Byte)     cdataitems.get(position+0);
                    Integer trig = (Integer)  cdataitems.get(position+1);
                    Long    time = (Long)     cdataitems.get(position+2);
                    
                    //EvioRawDataBank  dataBank = new EvioRawDataBank(crate,slot.intValue(),trig,time);
                    Integer nchannels = (Integer) cdataitems.get(position+3);
                    //System.out.println(" N - CHANNELS = " + nchannels + "  position = " + position);
                    //System.out.println("Retrieving the data size = " + cdataitems.size()
                    //+ "  " + cdatatypes.get(3) + " number of channels = " + nchannels);
                    position += 4;
                    int counter  = 0;
                    while(counter<nchannels){
                        //System.err.println("Position = " + position + " type =  "
                        //+ cdatatypes.get(position));
                        //Byte    slot = (Byte)     cdataitems.get(0);
                        //Integer trig = (Integer)  cdataitems.get(1);
                        //Long    time = (Long)     cdataitems.get(2);
                        Byte channel   = (Byte) cdataitems.get(position);
                        Integer length = (Integer) cdataitems.get(position+1);
                        //dataBank.addChannel(channel.intValue());
                        
                        //System.out.println(" LENGTH = " + length);
                        position += 2;
                        for(int loop = 0; loop < length; loop++){
                            Short tdc    = (Short) cdataitems.get(position);
                            Integer adc  = (Integer) cdataitems.get(position+1);
                            Short pmin   = (Short) cdataitems.get(position+2);
                            Short pmax   = (Short) cdataitems.get(position+3);
                            DetectorBankEntry  entry = new DetectorBankEntry(crate,slot,channel);
                            entry.setData(BankType.ADCFPGA, new int[]{tdc, adc, pmin, pmax});
                            entry.setTimeStamp(time);
                            entries.add(entry);
                            position+=4;
                            //dataBank.addData(channel.intValue(), 
                            //       new RawData(tdc,adc,pmin,pmax));
                        }
                        //position += 6;
                        counter++;
                    }
                }
                return entries;
            } catch (EvioException ex) {
                Logger.getLogger(EvioRawEventDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return entries;
    }
    
    public ArrayList<DetectorRawData>  getDataEntries_57622(Integer crate, EvioNode node, EvioDataEvent event){
        ArrayList<DetectorRawData>  rawdata = new ArrayList<DetectorRawData>();
        if(node.getTag()==57622){
            try {
                ByteBuffer     compBuffer = node.getByteData(true);
                CompositeData  compData = new CompositeData(compBuffer.array(),event.getByteOrder());                
                List<DataType> cdatatypes = compData.getTypes();
                List<Object>   cdataitems = compData.getItems();
                int  totalSize = cdataitems.size();
                int  position  = 0;
                while( (position + 4) < totalSize){
                    Byte    slot = (Byte)     cdataitems.get(position);
                    Integer trig = (Integer)  cdataitems.get(position+1);
                    Long    time = (Long)     cdataitems.get(position+2);
                    //EvioRawDataBank  dataBank = new EvioRawDataBank(crate, slot.intValue(),trig,time);
                    Integer nchannels = (Integer) cdataitems.get(position+3);
                    int counter  = 0;
                    position = position + 4;
                    while(counter<nchannels){
                        //System.err.println("Position = " + position + " type =  "
                        //+ cdatatypes.get(position));
                        Byte   channel    = (Byte) cdataitems.get(position);
                        Short  tdc     = (Short) cdataitems.get(position+1);

                        position += 2;
                        counter++;
                        DetectorRawData  entry = new DetectorRawData(crate,slot,channel);
                        //entry.setSVT(half, channel, tdc, adc);
                        entry.set(tdc);
                        
                        rawdata.add(entry);
                    }
                }
            } catch (EvioException ex) {
                //Logger.getLogger(EvioRawDataSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IndexOutOfBoundsException ex){
                //System.out.println("[ERROR] ----> ERROR DECODING COMPOSITE DATA FOR ONE EVENT");
            }
        }
        return rawdata;
    }
    
    
    
    public ArrayList<DetectorBankEntry>  getDataEntries_57617(Integer crate, EvioNode node, EvioDataEvent event){
        ArrayList<DetectorBankEntry>  rawdata = new ArrayList<DetectorBankEntry>();
        if(node.getTag()==57617){
            try {
                ByteBuffer     compBuffer = node.getByteData(true);
                CompositeData  compData = new CompositeData(compBuffer.array(),event.getByteOrder());                
                List<DataType> cdatatypes = compData.getTypes();
                List<Object>   cdataitems = compData.getItems();
                int  totalSize = cdataitems.size();
                //ArrayList<EvioRawDataBank> bankArray = new ArrayList<EvioRawDataBank>();
                int  position  = 0;
                while( (position + 4) < totalSize){
                    Byte    slot = (Byte)     cdataitems.get(position);
                    Integer trig = (Integer)  cdataitems.get(position+1);
                    Long    time = (Long)     cdataitems.get(position+2);
                    //EvioRawDataBank  dataBank = new EvioRawDataBank(crate, slot.intValue(),trig,time);
                    Integer nchannels = (Integer) cdataitems.get(position+3);
                    int counter  = 0;
                    position = position + 4;
                    while(counter<nchannels){
                        //System.err.println("Position = " + position + " type =  "
                        //+ cdatatypes.get(position));
                        Byte   half    = (Byte) cdataitems.get(position);
                        Byte   channel = (Byte) cdataitems.get(position+1);
                        Byte   tdc     = (Byte) cdataitems.get(position+2);
                        int    tdcint  = DataUtils.getIntFromByte(tdc);
                        //Short   adc     = (Short)  cdataitems.get(position+3);
                        Byte   adc     = (Byte)  cdataitems.get(position+3);
                        
                        Integer channelKey = (half & 0x00000008)>>3;
                        //System.err.println(" HALF = " + half + "  CHANNEL = " + channel + " KEY = " + channelKey  );
                        //dataBank.addChannel(channelKey);
                        //dataBank.addData(channelKey, new RawData(channelKey,tdc,adc));
                        position += 4;
                        counter++;
                        DetectorBankEntry  entry = new DetectorBankEntry(crate,slot,channelKey);
                        entry.setData(BankType.SVT, new int[] {half, channel, adc, tdcint});
                        this.trSVT.translate(entry);
                        rawdata.add(entry);

                    }
                    //bankArray.add(dataBank);
                }
                
            } catch (EvioException ex) {
                //Logger.getLogger(EvioRawDataSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IndexOutOfBoundsException ex){
                //System.out.println("[ERROR] ----> ERROR DECODING COMPOSITE DATA FOR ONE EVENT");
            }
        }
        return rawdata;
    }
    
    public void list(EvioDataEvent event){
        ArrayList<EvioTreeBranch> branches = this.getEventBranches(event);
        for(EvioTreeBranch branch : branches){
            System.out.println(">>>>>>>>>>> HEAD NODE " + branch.getTag());
            for(EvioNode node : branch.getNodes()){
                System.out.println("\t\t -----> NODE " + node.getTag() + " : " + node.getDataTypeObj());
            }
        }
    }
    /**
     * Returns an array of the branches in the event.
     * @param event
     * @return 
     */
    public ArrayList<EvioTreeBranch>  getEventBranches(EvioDataEvent event){
        ArrayList<EvioTreeBranch>  branches = new ArrayList<EvioTreeBranch>();
        try {

            //EvioNode mainNODE = event.getStructureHandler().getScannedStructure();
            //List<EvioNode>  eventNodes = mainNODE.getChildNodes();
            //List<EvioNode>  eventNodes = mainNODE.getAllNodes();
            List<EvioNode>  eventNodes = event.getStructureHandler().getNodes();
            if(eventNodes==null){
                return branches;
            }
            
            //System.out.println(" ************** BRANCHES ARRAY SIZE = " + eventNodes.size());
            for(EvioNode node : eventNodes){
                
                EvioTreeBranch eBranch = new EvioTreeBranch(node.getTag(),node.getNum());
                //branches.add(eBranch);
                //System.out.println("  FOR DROP : " + node.getTag() + "  " + node.getNum());
                List<EvioNode>  childNodes = node.getChildNodes();
                if(childNodes!=null){
                    for(EvioNode child : childNodes){
                        eBranch.addNode(child);
                    }
                    branches.add(eBranch);
                }
            }
            
        } catch (EvioException ex) {
            Logger.getLogger(RawEventDecoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return branches;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        System.out.println("[RAW DATA DECODER] ----->  DECODER TRANSLATION TABLE LIST");
        for(Map.Entry<String,IDetectorTranslationTable> entry : this.translationTables.entrySet()){
            str.append(String.format("TRANSLATION TABLE : [%12s]", entry.getKey()));
        }
        return str.toString();
    }
    
    public static void main(String[] args){
        RawEventDecoder decoder = new RawEventDecoder();
        EvioSource reader = new EvioSource();
        reader.open("/Users/gavalian/Work/Software/Release-8.0/COATJAVA/sector2_000211.evio.0");
        /*
        while(reader.hasEvent()){
            System.out.println("\n\n====================>  EVENT");
            EvioDataEvent event = (EvioDataEvent) reader.getNextEvent();
            //decoder.list(event);            
            List<DetectorBankEntry>  entries = decoder.getDataEntries(event);
            for(DetectorBankEntry entry : entries){
                System.out.println(entry);
            }
        }*/
    }
}
