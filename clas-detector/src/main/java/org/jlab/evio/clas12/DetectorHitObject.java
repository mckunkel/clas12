/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.evio.clas12;

import java.util.HashMap;
import java.util.Map;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.data.io.DataBank;
import org.jlab.geom.prim.Vector3D;

/**
 *
 * @author gavalian
 */
public class DetectorHitObject {
    
    Map<String,Vector3D>  hitVectors   = new HashMap<String,Vector3D>();
    DetectorDescriptor    detectorDesc = new DetectorDescriptor();
    double   hitEnergy = 0.0;
    double   hitTime   = 0.0;
    
    public DetectorHitObject(){
        
    }
    
    public void setPosition(double x, double y, double z){
        hitVectors.put("position", new Vector3D(x,y,z));
    }
    
    public void setVertex(double x, double y, double z){
        hitVectors.put("vertex", new Vector3D(x,y,z));
    }
    
    public Vector3D  position(){
        if(hitVectors.containsKey("position")==false){
            System.out.println("[Hit Object] ---> does not have a position vector");
            return new Vector3D();
        }
        return hitVectors.get("position");
    }
    
    public Vector3D  vertex(){
        if(hitVectors.containsKey("vertex")==false){
            System.out.println("[Hit Object] ---> does not have a position vector");
            return new Vector3D();
        }
        return hitVectors.get("vertex");
    }
    
    public DetectorDescriptor  descriptor(){ return this.detectorDesc;}
    
    public void setEnergy(double en){
        this.hitEnergy = en;
    }
    
    public void setTime(double time){
        this.hitTime = time;
    }
    
    public double getEnergy(){
        return this.hitEnergy;
    }
    
    public double getTime(){ return this.hitTime;}
    
    public void readPosition(DataBank bank, int row){
        
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("%3d %3d %3d", 
                detectorDesc.getSector(),
                detectorDesc.getLayer(),
                detectorDesc.getComponent()));
        str.append(String.format("  %8.3f %8.3f",hitEnergy,hitTime));
        if(this.hitVectors.containsKey("position")==true){
            str.append(String.format("  %8.3f %8.3f %8.3f",
                    position().x(),
                    position().y(),
                    position().z()
                    ));
        }
        
        return str.toString();
    }
}
