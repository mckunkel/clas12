/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.detector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas.detector.IDetectorUnit;
import org.jlab.geom.prim.Line3D;

/**
 *
 * @author gavalian
 */
public class DetectorCounter implements IDetectorUnit {
    
    DetectorDescriptor  descriptor = new DetectorDescriptor();
    List<DetectorChannel>     photoTubes = new ArrayList<DetectorChannel>();
    Map<String,Double>  calibrationConstants = new TreeMap<String,Double>();    
    final Line3D        detectorUnitLine = new Line3D();
    private  long       timeStamp        = 0L;
    
    public DetectorCounter(DetectorType type,int sector, int layer, int component){
        this.descriptor.setType(type);
        this.descriptor.setSectorLayerComponent(sector, layer, component);
    }
    
    public DetectorCounter(DetectorDescriptor desc){
        this.descriptor.setType(desc.getType());
        this.descriptor.setSectorLayerComponent(desc.getSector(),desc.getLayer(),desc.getComponent());
        this.descriptor.setCrateSlotChannel(desc.getCrate(), desc.getSlot(), desc.getChannel());
    }
    
    public DetectorDescriptor getDescriptor() {
        return this.descriptor;
    }
    
    public void setTimeStamp(long ts){ this.timeStamp = ts;}
    public long getLongStamp(){return this.timeStamp;}
    
    public void addChannel(DetectorChannel tube){
        this.photoTubes.add(tube);
    }
    
    public  double getConstant(String key){
        return this.calibrationConstants.get(key);
    }
    
    public  void setConstant(String key, double value){
        this.calibrationConstants.put(key, value);
    }
    
    public  List<DetectorChannel> getChannels(){
        return this.photoTubes;
    }
    
    public double getEnergy(){
        double energy = 0.0;
        for(DetectorChannel tube : this.photoTubes){
            for(int loop = 0; loop < tube.getADC().size(); loop++){
                energy += tube.getEnergy(loop);
            }
        }
        return energy;
    }
    
    public boolean isMultiHit(){
        for(DetectorChannel tube : this.photoTubes){
            if(tube.getADC().size()==1&&tube.getTDC().size()==1){
                
            } else {
                return true;
            }
        }
        return false;
    }
    
    public double getTime(){
        double time = 0;
        
        if(this.isMultiHit()==true){
            System.out.println("---> error determining time. there are multiple hits per PMT.");
            return 0.0;
        }
        
        if(this.photoTubes.size()==1){
            return this.photoTubes.get(0).getTime(0);
        }
        
        if(this.photoTubes.size()==2){
            return 0.5 *(this.photoTubes.get(0).getTime(0) + this.photoTubes.get(1).getTime(0));
        }
        System.out.println("---> I do not know how to calculate time for "
                + this.photoTubes.size() + " photo tubues. you do it.");
        return 0;
    }
    
    public void addTDC(int channel, int tdc){
        
        int required = 1 + channel - this.photoTubes.size();
        if(required>0){
            for(int b = 0; b < required; b++){
                this.addChannel(new DetectorChannel());
            }
        }
        
        this.getChannels().get(channel).getTDC().add(tdc);
     
    }
    
    public void addADC(int channel, int adc){
        
        int required = 1 + channel - this.photoTubes.size();
        if(required>0){
            for(int b = 0; b < required; b++){
                this.addChannel(new DetectorChannel());
            }
        }
        
        this.getChannels().get(channel).getADC().add(adc);     
    }
    
    public void addADCPulse(int channel, short[] adc){
        
        int required = 1 + channel - this.photoTubes.size();
        if(required>0){
            for(int b = 0; b < required; b++){
                this.addChannel(new DetectorChannel());
            }
        }
        
        this.getChannels().get(channel).setPulse(adc);
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(this.descriptor.toString());
        str.append("\n");
        for(DetectorChannel tube : this.photoTubes){
            str.append("\t----> ");
            str.append(tube.toString());
            str.append("\n");
        }
        str.append("\n\t\t---->");
        for(Map.Entry<String,Double> entry : this.calibrationConstants.entrySet()){
            str.append(String.format("%s=%f,",entry.getKey(),entry.getValue()));
        }        
        return str.toString();
    }
}
