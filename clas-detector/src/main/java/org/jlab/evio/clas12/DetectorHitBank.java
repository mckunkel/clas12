/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.evio.clas12;

import java.util.ArrayList;
import java.util.List;
import org.jlab.data.io.DataBank;
import org.jlab.data.io.DataEvent;

/**
 *
 * @author gavalian
 */
public class DetectorHitBank {
    
    List<DetectorHitObject>  bankHitObjects = new ArrayList<DetectorHitObject>();
    
    public DetectorHitBank(){
        
    }
    
    public void readBank(String name, DataEvent event){
        this.bankHitObjects.clear();
        
        boolean isPositionAvailable = false;
        boolean isVertexAvailable   = false;
        
        if(event.hasBank(name)==true){
            DataBank  bank = event.getBank(name);
            if(bank.getDescriptor().hasEntries("x","y","z")==true) isPositionAvailable = true;
            if(bank.getDescriptor().hasEntries("vx","vy","vz")==true) isVertexAvailable = true;
            
            int nrows = bank.rows();
            for(int row = 0; row < nrows; row++){
                DetectorHitObject  hitObject = new DetectorHitObject();
                readDescriptor(hitObject,bank,row);
                if(isPositionAvailable==true){
                    readPosition(hitObject,bank,row);
                }
                this.bankHitObjects.add(hitObject);
            }            
        }
    }
    
    public void clear(){
        this.bankHitObjects.clear();
    }
    
    public void addHit(DetectorHitObject hit){
        this.bankHitObjects.add(hit);
    }
    
    public void  readDescriptor(DetectorHitObject obj, DataBank bank, int row){
        int sector = 0;
        int layer  = 0;
        int component = 0;
        if(bank.getDescriptor().hasEntry("sector")==true){
            sector = bank.getInt("sector", row);
        }
        if(bank.getDescriptor().hasEntry("layer")==true){
            layer = bank.getInt("layer", row);
        }
        if(bank.getDescriptor().hasEntry("component")==true){
            component = bank.getInt("component", row);
        }
        obj.descriptor().setSectorLayerComponent(sector, layer, component);
    }
    
    /**
     * Reads position into hitobject.
     * @param obj
     * @param bank
     * @param row 
     */
    public void readPosition(DetectorHitObject obj, DataBank bank, int row){        
            obj.setPosition(
                    bank.getDouble("x", row), 
                    bank.getDouble("y", row), 
                    bank.getDouble("z", row)
                    );
    }
    
    public void readVertex(DetectorHitObject obj, DataBank bank, int row){        
            obj.setPosition(
                    bank.getDouble("vx", row), 
                    bank.getDouble("vy", row), 
                    bank.getDouble("vz", row)
                    );
    }
    
    public void readEnergyTime(DetectorHitObject obj, DataBank bank, int row){
        obj.setEnergy(bank.getDouble("energy", row));
        obj.setTime(bank.getDouble("time", row));
    }
    
    
    public int  getSize(){
        return this.bankHitObjects.size();
    }
    
    public DetectorHitObject  getHit(int index){
        return this.bankHitObjects.get(index);
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(DetectorHitObject hit : this.bankHitObjects){
            str.append(hit.toString());
            str.append("\n");
        }
        return str.toString();
    }
    
    
    public static void main(String[] args){
        DetectorHitBank  bank = new DetectorHitBank();
        
        DetectorHitObject  obj1 = new DetectorHitObject();
        obj1.descriptor().setSectorLayerComponent(1, 2, 3);
        obj1.setPosition(120.0, 130.0, 140.0);
        obj1.setEnergy(0.5);
        obj1.setTime(28.0);
        
        bank.addHit(obj1);
        
        DetectorHitObject  obj2 = new DetectorHitObject();
        obj2.descriptor().setSectorLayerComponent(2, 2, 3);
        obj2.setPosition(220.0, 230.0, 240.0);
        obj2.setEnergy(0.8);
        obj2.setTime(24.0); 
        bank.addHit(obj2);
        
        System.out.println(bank);
    }
}
