/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.physics;

import java.util.ArrayList;
import java.util.List;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas.physics.Vector3;
import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataBank;

/**
 *
 * @author gavalian
 */
public class ClasTools {
    
    private static final double EC_THETA  = 0.4363323;
    private static final double EC_Y_LOW  = -182.974;
    private static final double EC_Y_HI   = 189.956;
    private static final double EC_TGRHO  = 1.95325;
    private static final double EC_SINRHO = 0.8901256;
    private static final double EC_COSRHO = 0.455715;
    private int   debugMode = 0;
    
    private final List<IDetectorEventProcessor>  processorList = new ArrayList<IDetectorEventProcessor>();
    
    public ClasTools(){
        
        
    }
    
    public void setDebug(int mode){
        this.debugMode = mode;
    }
    
    public void addProcessor(IDetectorEventProcessor proc){
        this.processorList.add(proc);
    }
    
    public DetectorEvent getDetectorEvent(DataEvent event){
        DetectorEvent detectorEvent = ClasTools.readDetectorEvent(event);

        for(IDetectorEventProcessor proc : this.processorList){
            try {
                proc.processDetectorEvent(detectorEvent);
            } catch (Exception e){
                System.out.println("[CLAS TOOLS] warning --> problem processing event with : " 
                + proc.getClass().getName());
                if(this.debugMode!=0){
                    e.printStackTrace();
                }
            }
        }
        return detectorEvent;
    }
    
    public static Vector3  getEC_UVW(Vector3 xyz){
        double[][] rot = new double[3][3];
        double phi = Math.atan2(xyz.y(), xyz.x())*57.29578;
        if(phi<0.0) phi += 360;
        phi += 30;
        if(phi>360.0) phi -= 360.0;
        double ec_phi = (phi/60.0)*1.0471975;
        rot[0][0] = Math.cos(ClasTools.EC_THETA)*Math.cos(ec_phi);
        rot[0][1] = -Math.sin(ec_phi);
        rot[0][2] = Math.sin(ClasTools.EC_THETA)*Math.cos(ec_phi);
        rot[1][0] = Math.cos(ClasTools.EC_THETA)*Math.sin(ec_phi);
        rot[1][1] = Math.cos(ec_phi);
        rot[1][2] = Math.sin(ClasTools.EC_THETA)*Math.sin(ec_phi);
        rot[2][0] = -Math.sin(ClasTools.EC_THETA);
        rot[2][1] = 0.0;
        rot[2][2] = Math.cos(ClasTools.EC_THETA);
        double yi = xyz.x()*rot[0][0] + xyz.y()*rot[1][0]+xyz.z()*rot[2][0];
        double xi = xyz.x()*rot[0][1] + xyz.y()*rot[1][1]+xyz.z()*rot[2][1];
        double zi = xyz.x()*rot[0][2] + xyz.y()*rot[1][2]+xyz.z()*rot[2][2];
        zi = zi - 510.32;
        
        double u = (yi-ClasTools.EC_Y_LOW)/ClasTools.EC_SINRHO;
        double v = (ClasTools.EC_Y_HI-ClasTools.EC_Y_LOW)/ClasTools.EC_TGRHO
                - xi + (ClasTools.EC_Y_HI-yi)/ClasTools.EC_TGRHO;
        double w = ((ClasTools.EC_Y_HI - ClasTools.EC_Y_LOW)/ClasTools.EC_TGRHO
                + xi + (ClasTools.EC_Y_HI-yi)/ClasTools.EC_TGRHO)/2.0/ClasTools.EC_COSRHO;
        return new Vector3(u,v,w);
    }
    
    public static DetectorEvent readDetectorEvent(DataEvent event){
        DetectorEvent detEvent = new DetectorEvent();
        if(event.hasBank("EVENT::particle")==true){
            
            float startTime = 0.0f;
            
            if(event.hasBank("HEADER::info")==true){
                EvioDataBank  header = (EvioDataBank) event.getBank("HEADER::info");
                startTime = header.getFloat("stt", 0);
            }
            
            EvioDataBank  bank = (EvioDataBank)  event.getBank("EVENT::particle");            
            EvioDataBank  bankSC = (EvioDataBank) event.getBank("DETECTOR::scpb");
            EvioDataBank  bankEC = (EvioDataBank) event.getBank("DETECTOR::ecpb");
            EvioDataBank  bankCC = (EvioDataBank) event.getBank("DETECTOR::ccpb");
            
            int nrows = bank.rows();
            for(int loop = 0; loop < nrows; loop++){
            
                DetectorParticle particle = new DetectorParticle();
                int indexSC = bank.getByte("scstat", loop)-1;
                int indexEC = bank.getByte("ecstat", loop)-1;
                int indexCC = bank.getByte("ccstat", loop)-1;
                int pstatus = bank.getByte("status", loop);
                
                particle.setStatus(pstatus);
                particle.vector().setXYZ(
                        bank.getFloat("px", loop),
                        bank.getFloat("py", loop),
                        bank.getFloat("pz", loop)
                        );
                particle.vertex().setXYZ(
                        bank.getFloat("vx", loop),
                        bank.getFloat("vy", loop),
                        bank.getFloat("vz", loop)
                        );
                particle.setPid(bank.getInt("pid", loop));
                particle.setCharge((int) bank.getByte("charge", loop));
                particle.setMass(bank.getFloat("mass", loop));
                
                if(bankSC!=null&&indexSC>=0){
                    DetectorResponse  response = new DetectorResponse();
                    response.getDescriptor().setType(DetectorType.SC);
                    response.getDescriptor().setSectorLayerComponent(
                            bankSC.getByte("sector", indexSC),
                            0,
                            bankSC.getByte("paddle", indexSC)
                    );
                    response.setPath(bankSC.getFloat("path", indexSC));
                    response.setTime(bankSC.getFloat("time", indexSC)-startTime);
                    response.setEnergy(bankSC.getFloat("edep", indexSC));
                    particle.addResponse(response,false);
                }
                
                 if(bankEC!=null&&indexEC>=0){
                     DetectorResponse  resECIN  = new DetectorResponse();
                     DetectorResponse  resECOUT = new DetectorResponse();
                     DetectorResponse  resECTOT = new DetectorResponse();
                     resECIN.getDescriptor().setType(DetectorType.EC);
                     resECOUT.getDescriptor().setType(DetectorType.EC);
                     resECTOT.getDescriptor().setType(DetectorType.EC);
                     int sector = bankEC.getByte("sector", indexEC);
                     resECIN.getDescriptor().setSectorLayerComponent( sector, 0, -1);
                     resECOUT.getDescriptor().setSectorLayerComponent(sector, 1, -1);
                     resECTOT.getDescriptor().setSectorLayerComponent(sector, 2, -1);
                     
                     resECIN.setEnergy(bankEC.getFloat("ein", indexEC));
                     resECOUT.setEnergy(bankEC.getFloat("eout", indexEC));
                     resECTOT.setEnergy(bankEC.getFloat("etot", indexEC));
                     double time = bankEC.getFloat("time", indexEC) - startTime;
                     double path = bankEC.getFloat("path", indexEC);
                     
                     resECIN.setTime(time);
                     resECOUT.setTime(time);
                     resECTOT.setTime(time);
                     
                     resECIN.setPath(path);
                     resECOUT.setPath(path);
                     resECTOT.setPath(path);
                     
                     double x = bankEC.getFloat("x", indexEC);
                     double y = bankEC.getFloat("y", indexEC);
                     double z = bankEC.getFloat("z", indexEC);
                     
                     resECIN.getPosition().setXYZ(x, y, z);
                     resECOUT.getPosition().setXYZ(x, y, z);
                     resECTOT.getPosition().setXYZ(x, y, z);
                     
                     particle.addResponse(resECIN,false);
                     particle.addResponse(resECOUT,false);
                     particle.addResponse(resECTOT,false);
                 }             
                 
                 if(bankCC!=null){
                     DetectorResponse resCC = ClasTools.getDetectorResponse_CLAS6_CC(bankCC, indexCC);
                     if(resCC!=null){
                         resCC.setTime(resCC.getTime()-startTime);
                         particle.addResponse(resCC,false);
                     }
                 }
                detEvent.addParticle(particle);
            }
        }
        return detEvent;
    }

    private static DetectorResponse getDetectorResponse_CLAS6_CC(EvioDataBank bank, int index){
        if(bank.getDescriptor().getName().compareTo("DETECTOR::ccpb")==0){
            if(index>=bank.rows()||index<0) return null;
            //System.out.println("  CC bank fill : " + bank.rows() + "   index = " + index);
            DetectorResponse response = new DetectorResponse();
            response.getDescriptor().setType(DetectorType.CC);
            response.getDescriptor().setSectorLayerComponent(
                    bank.getByte("sector", index), 0, 0);
            response.getPosition().setXYZ(0.0, 0.0, 0.0);
            response.getMatchedPosition().setXYZ(0.0, 0.0, 0.0);
            response.setEnergy(bank.getFloat("nphe", index));
            response.setTime(bank.getFloat("time", index));
            response.setPath(bank.getFloat("path", index));
            return response;
        }
        return null;
    }
    
}
