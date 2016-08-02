/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.EventFilter;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.PhysicsEvent;
import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;

/**
 *
 * @author gavalian
 */
public class GenericKinematicFitter {
    
    private final   EventFilter filter = new EventFilter();
    private Double  beamEnergy  = 11.0;
    private Boolean forceFilter = false;
    private Boolean generatedEventMatching = true;
    private IDetectorEventProcessor   detectorEventProcessor = null;
    private List<IDetectorEventProcessor> detectorProcessors = new ArrayList<IDetectorEventProcessor>();
    
    public GenericKinematicFitter(double beam){
        this.beamEnergy = beam;
        this.filter.setFilter("X+:X-:Xn");
        this.detectorEventProcessor = new CLAS6DetectorEventProcess();
    }
    
    public GenericKinematicFitter(){
        this.beamEnergy = 11.0;
        this.filter.setFilter("X+:X-:Xn");
        this.detectorEventProcessor = new CLAS6DetectorEventProcess();
    }
    
    public GenericKinematicFitter(double beam, String filterString){
        this.beamEnergy = beam;
        this.filter.setFilter(filterString);
        this.detectorEventProcessor = new CLAS6DetectorEventProcess();
    }
    
    /**
     * Returns PhysicsEvent object with generated particles in the event.
     * @param event
     * @return 
     */
    public PhysicsEvent getGeneratedEvent(DataEvent  event){
        if(event instanceof EvioDataEvent){
            if(event.hasBank("GenPart::true")==true){
                return this.getGeneratedEventClas12((EvioDataEvent) event);
            }
        }
        return new PhysicsEvent(this.beamEnergy);
    }
    /**
     * Returns PhysicsEvent object with reconstructed particles.
     * @param event - DataEvent object
     * @return PhysicsEvent : event containing particles.
     */
    public PhysicsEvent  getPhysicsEvent(DataEvent  event){
        if(event instanceof EvioDataEvent){
            //System.out.println("   CHECK FOR  PARTICLE = " + event.hasBank("EVENT::particle"));
            if(event.hasBank("EVENTHB::particle")==true){
                PhysicsEvent genEvent = this.getGeneratedEvent(event);
                PhysicsEvent recEvent =  this.getPhysicsEventClas12((EvioDataEvent) event);
                if(this.generatedEventMatching==true) this.matchGenerated(genEvent, recEvent);
                return recEvent;
            }
            if(event.hasBank("EVENT::particle")==true){
                PhysicsEvent recEvent =  this.getPhysicsEventClas6((EvioDataEvent) event);
                return recEvent;
            }
        }
        return new PhysicsEvent(this.beamEnergy);
    }    
    
    public void addEventProcessor(IDetectorEventProcessor proc){
        System.out.println("\n----> adding detector processing unit : " +
                proc.getClass().getCanonicalName());
        this.detectorProcessors.add(proc);
    }
    
    public void addEventProcessor(String name){
        try {
            Class c = Class.forName(name);
            if(IDetectorEventProcessor.class.isAssignableFrom(c)==true){
                this.addEventProcessor( (IDetectorEventProcessor) c.newInstance());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GenericKinematicFitter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(GenericKinematicFitter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GenericKinematicFitter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setDetectorEventProcessor(String name){
        try {
            Class c = Class.forName(name);
            if(IDetectorEventProcessor.class.isAssignableFrom(c)==true){
                this.detectorEventProcessor = (IDetectorEventProcessor) c.newInstance();
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GenericKinematicFitter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(GenericKinematicFitter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GenericKinematicFitter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Returns a PhysicsEvent after processing the detector event through
     * modules added to the fitter.
     * @param event
     * @return 
     */
    public PhysicsEvent getPhysicsEventProcessed(EvioDataEvent event){
        if(event.hasBank("EVENT::particle")==true){
            DetectorEvent detevent = this.getDetectorEvent(event);
            for(IDetectorEventProcessor proc : this.detectorProcessors){
                proc.processDetectorEvent(detevent);                
            }
            return GenericKinematicFitter.detectorEventToPhysicsEvent(detevent);
            //return this.detectorEventProcessor.getPhysicsEvent(detevent);
        } else {
            //System.out.println();
        }
        return new PhysicsEvent();
    }
    /**
     * 
     * @param proc 
     */
    public void setDetectorEventProcessor(IDetectorEventProcessor proc){
        this.detectorEventProcessor = proc;
    }
    
    public void setMatching(Boolean matching_flag){
        this.generatedEventMatching = matching_flag;
    }
    
    public void matchGenerated(PhysicsEvent gen, PhysicsEvent rec){
        int nrows = rec.count();
        for(int loop = 0; loop < nrows; loop++){
            Particle rpart = rec.getParticle(loop);
            Particle gpart = gen.closestParticle(rpart);
            if(rpart.cosTheta(gpart)>0.998){
                if(rpart.charge()!=0){
                    rpart.changePid(gpart.pid());
                } else {
                    if(Math.toDegrees(gpart.theta())>6.0){
                        double px = rpart.px()*gpart.p();
                        double py = rpart.py()*gpart.p();
                        double pz = rpart.pz()*gpart.p();
                        rpart.vector().setPxPyPzM(px, py, pz, 0.0);
                        rpart.changePid(22);
                    }
                }
            }
        }
    }
    
    private PhysicsEvent  getGeneratedEventClas12(EvioDataEvent event){
        PhysicsEvent physEvent = new PhysicsEvent();
        physEvent.setBeam(this.beamEnergy);
        if(event.hasBank("GenPart::true")){
            EvioDataBank evntBank = (EvioDataBank) event.getBank("GenPart::true");
            int nrows = evntBank.rows();
            for(int loop = 0; loop < nrows; loop++){
                Particle genParticle = new Particle(
                        evntBank.getInt("pid", loop),
                        evntBank.getDouble("px", loop)*0.001,
                        evntBank.getDouble("py", loop)*0.001,
                        evntBank.getDouble("pz", loop)*0.001,
                        evntBank.getDouble("vx", loop),
                        evntBank.getDouble("vy", loop),
                        evntBank.getDouble("vz", loop));
                if(genParticle.p()<10.999&&
                        Math.toDegrees(genParticle.theta())>2.0){
                    physEvent.addParticle(genParticle);    
                }
            }
        }
        return physEvent;
    }
    
    private PhysicsEvent  getPhysicsEventClas6(EvioDataEvent event){
        //System.out.println("---------- CLAS 6 --------");
        PhysicsEvent physEvent = new PhysicsEvent();
        physEvent.setBeam(this.beamEnergy);
        if(event.hasBank("EVENT::particle")){
            EvioDataBank evntBank = (EvioDataBank) event.getBank("EVENT::particle");
            int nrows = evntBank.rows();
            for(int loop = 0; loop < nrows; loop++){
                int status = evntBank.getByte("status", loop);
                int dcstat = evntBank.getByte("dcstat", loop);
                int scstat = evntBank.getByte("scstat", loop);
                int pid    = evntBank.getInt("pid", loop);
                if(PDGDatabase.isValidPid(pid)==true){
                    Particle part = new Particle(
                            evntBank.getInt("pid", loop),
                            evntBank.getFloat("px", loop),
                            evntBank.getFloat("py", loop),
                            evntBank.getFloat("pz", loop),
                            evntBank.getFloat("vx", loop),
                            evntBank.getFloat("vy", loop),
                            evntBank.getFloat("vz", loop));
                    
                    if(status>0) physEvent.addParticle(part);
                    
                } else {
                    Particle part = new Particle();
                    part.setParticleWithMass(evntBank.getFloat("mass", loop),
                            evntBank.getByte("charge", loop),
                            evntBank.getFloat("px", loop),
                            evntBank.getFloat("py", loop),
                            evntBank.getFloat("pz", loop),
                            evntBank.getFloat("vx", loop),
                            evntBank.getFloat("vy", loop),
                            evntBank.getFloat("vz", loop)
                    );
                    if(status>0) physEvent.addParticle(part);
                }
            }
        }
        return physEvent;
    }
    
    private PhysicsEvent  getPhysicsEventClas12(EvioDataEvent event){
        PhysicsEvent physEvent = new PhysicsEvent();
        physEvent.setBeam(this.beamEnergy);
        if(event.hasBank("EVENTHB::particle")){
            EvioDataBank evntBank = (EvioDataBank) event.getBank("EVENTHB::particle");
            int nrows = evntBank.rows();
            for(int loop = 0; loop < nrows; loop++){
                
                int pid    = evntBank.getInt("pid", loop);
                if(PDGDatabase.isValidPid(pid)==true){
                    Particle part = new Particle(
                            evntBank.getInt("pid", loop),
                            evntBank.getFloat("px", loop),
                            evntBank.getFloat("py", loop),
                            evntBank.getFloat("pz", loop),
                            evntBank.getFloat("vx", loop),
                            evntBank.getFloat("vy", loop),
                            evntBank.getFloat("vz", loop));
                    physEvent.addParticle(part);
                } else {
                    Particle part = new Particle();
                    int charge = evntBank.getInt("charge", loop);
                    part.setParticleWithMass(evntBank.getFloat("mass", loop),
                            (byte) charge,
                            evntBank.getFloat("px", loop),
                            evntBank.getFloat("py", loop),
                            evntBank.getFloat("pz", loop),
                            evntBank.getFloat("vx", loop),
                            evntBank.getFloat("vy", loop),
                            evntBank.getFloat("vz", loop)
                    );
                    physEvent.addParticle(part);
                }
            }
        }
        return physEvent;
    }
    
    
    public DetectorEvent  getDetectorEvent(EvioDataEvent event){
        
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
                     DetectorResponse resCC = this.getDetectorResponse_CLAS6_CC(bankCC, indexCC);
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
    
    private DetectorResponse getDetectorResponse_CLAS6_CC(EvioDataBank bank, int index){
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
    
    
    public static PhysicsEvent detectorEventToPhysicsEvent(DetectorEvent event){
        PhysicsEvent   physEvent = new PhysicsEvent();
        for(int loop = 0; loop < event.getParticles().size(); loop++){
            DetectorParticle part = event.getParticles().get(loop);
            if(part.getStatus()>=0){
                int pid = part.getPid();
                if(PDGDatabase.isValidPid(pid)==true){
                    Particle particle = new Particle(part.getPid(),
                            part.vector().x(),part.vector().y(),part.vector().z(),
                            part.vertex().x(),part.vertex().y(),part.vertex().z()
                    );
                    physEvent.addParticle(particle);
                } else {
                    Particle particle = new Particle();
                    particle.setParticleWithMass(part.getMass(), (byte) part.getCharge(),
                            part.vector().x(),part.vector().y(),part.vector().z(),
                            part.vertex().x(),part.vertex().y(),part.vertex().z()
                    );
                    physEvent.addParticle(particle);
                }
            }
        }
        return physEvent;
    }
}
