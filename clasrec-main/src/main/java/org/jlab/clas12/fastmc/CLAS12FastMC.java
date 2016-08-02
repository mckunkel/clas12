/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastmc;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.PhysicsEvent;
import org.jlab.clasrec.utils.DataBaseLoader;
import org.jlab.geom.DetectorHit;
import org.jlab.geom.base.ConstantProvider;
import org.jlab.geom.base.Detector;
import org.jlab.geom.detector.dc.DCFactory;
import org.jlab.geom.detector.ec.ECFactory;
import org.jlab.geom.detector.ftof.FTOFFactory;
import org.jlab.geom.prim.Arc3D;
import org.jlab.geom.prim.Cylindrical3D;
import org.jlab.geom.prim.Path3D;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Transformation3D;
import org.jlab.geom.prim.Vector3D;

/**
 *
 * @author gavalian
 */
public class CLAS12FastMC {
    private ArrayList<Cylindrical3D>  SVTDetector = new ArrayList<Cylindrical3D>();
    
    private ParticleSwimmer  particleSwimmer = null;
    private final TreeMap<String,Detector>  detectors = new TreeMap<String,Detector>();
    private final TreeMap<String,Integer>   detectorMinHits = new TreeMap<String,Integer>();
    private final TreeMap<Integer,IParticleResolution>  pResolutions = new TreeMap<Integer,IParticleResolution>();
    
    private double torusScale = 0.0;
    private double solenoidScale = 0.0;
    public boolean isSmeared     = true;
    
    
    public CLAS12FastMC(double torus, double solenoid){
        this.torusScale = torus;
        this.solenoidScale = solenoid;
        
        particleSwimmer = new ParticleSwimmer(torus,solenoid);
        
        this.initDetectors();
        this.initDetectosHits();
        if(torus<0){
            this.addResolutionFunc(   11, new ParticleResolutionCentralIn());
            this.addResolutionFunc( -211, new ParticleResolutionCentralIn());
            this.addResolutionFunc( -321, new ParticleResolutionCentralIn());
            this.addResolutionFunc( -2212, new ParticleResolutionCentralIn());
            this.addResolutionFunc(  321, new ParticleResolutionCentralOut());
            this.addResolutionFunc(  211, new ParticleResolutionCentralOut());
            this.addResolutionFunc(  2212, new ParticleResolutionCentralOut());
            this.addResolutionFunc(  -11, new ParticleResolutionCentralOut());
            
        } else {
            this.addResolutionFunc(   11, new ParticleResolutionCentralOut());
            this.addResolutionFunc( -211, new ParticleResolutionCentralOut());
            this.addResolutionFunc( -321, new ParticleResolutionCentralOut());
            this.addResolutionFunc( -2212, new ParticleResolutionCentralOut());
            this.addResolutionFunc(  321, new ParticleResolutionCentralIn());
            this.addResolutionFunc(  211, new ParticleResolutionCentralIn());
            this.addResolutionFunc(  2212, new ParticleResolutionCentralIn());
            this.addResolutionFunc(  -11, new ParticleResolutionCentralIn());
        }
    }
    
    private void initDetectors(){
        // Init DC detector
        DCFactory      dcfactory = new DCFactory();
        ConstantProvider  dcdata = DataBaseLoader.getConstantsDC(10,"default");
        Detector geomDC = dcfactory.createDetectorCLAS(dcdata);
        this.detectors.put("DC", geomDC);
        // Init FTOF detector
        FTOFFactory ftoffactory = new FTOFFactory();
        ConstantProvider  ftofdata = DataBaseLoader.getTimeOfFlightConstants();
        Detector geomFTOF = ftoffactory.createDetectorCLAS(ftofdata);
        this.detectors.put("FTOF", geomFTOF);
        // Init EC detector 
        ECFactory ecfactory = new ECFactory();
        ConstantProvider  ecdata = DataBaseLoader.getCalorimeterConstants();
        Detector geomEC = ecfactory.createDetectorCLAS(ecdata);
        this.detectors.put("EC", geomEC);
        double[]  arcLength = new double[]{6.5588, 9.3198, 12.0608,16.1533};
        double[]  ztransform = new double[]{
            11.44 - 33.51/2.0,
            15.38 - 33.51/2.0,
            19.30 - 33.51/2.0,
            25.14 - 33.51/2.0
        };
        
        for(int loop = 0; loop < 4; loop++){
        
            Arc3D arc = new Arc3D(new Point3D(arcLength[loop],0.0,0.0),
                    new Point3D(0.0,0.0,0.0), new Vector3D(0.0,0.0,1.0),
                    2.0*Math.PI
            );
            
            Cylindrical3D region = new Cylindrical3D(arc,33.51);
            Transformation3D  trans = new Transformation3D();
            trans.translateXYZ(0.0, 0.0, ztransform[loop]);
            trans.apply(region);
            this.SVTDetector.add(region);
        }
        
    }
    
    
    
    public void setSmearing(boolean flag){
        this.isSmeared = flag;
    }
    public void addResolutionFunc(int pid,IParticleResolution res){
        this.pResolutions.put(pid, res);
    }
    
    private void initDetectosHits(){
        this.detectorMinHits.put("DC"   , 36);
        //this.detectorMinHits.put("FTOF" , 1);
        //this.detectorMinHits.put("EC"   , 30);
    }
    
    public List<DetectorHit>  getDetectorHits(String det, Path3D path){
        if(this.detectors.containsKey(det)==true){
            return this.detectors.get(det).getHits(path);
        }
        return new ArrayList<DetectorHit>();
    }
    
    public boolean isNeutralDetected(Particle part){
        Path3D  path = this.particleSwimmer.particlePath(part);
        List<DetectorHit> ecHits    = this.detectors.get("EC").getLayerHits(path);
        if(ecHits.size()<this.detectorMinHits.get("EC")) return false;
        return true;
    }
    
    public Particle getParticle(Particle part){
        if(this.isParticleDetected(part)==true){
            Particle detected = new Particle(part);
            if(this.isSmeared == true){
                if(this.pResolutions.containsKey(detected.pid())==true){
                    this.pResolutions.get(detected.pid()).apply(detected, torusScale, solenoidScale);
                }
            }
            return detected;
        }
        return new Particle(part.pid(),0.0,0.0,0.0,0.0,0.0,0.0);
    }
    
    public ArrayList<DetectorHit> getDetectorHits(Particle part){
        Path3D path = this.particleSwimmer.particlePath(part);
        ArrayList<DetectorHit> particleHits = new ArrayList<DetectorHit>();
        List<DetectorHit> ecHits = this.getDetectorHits("EC", path);
        particleHits.addAll(ecHits);
        
        if(part.charge()!=0){
            List<DetectorHit> ftofHits = this.getDetectorHits("FTOF", path);
            particleHits.addAll(ecHits);
            
            List<DetectorHit> dcHits = this.getDetectorHits("DC", path);
            particleHits.addAll(ecHits);
            
        }
        
        return particleHits;
    }
    
    
    public boolean isParticleDetected(Particle  part){
        
        if(part.charge()==0) return this.isNeutralDetected(part);
                
        Path3D  path = this.particleSwimmer.particlePath(part);
        Boolean centralStatus = this.isDetectedInCentral(path);
        //if(centralStatus==true) return true;  
        
        List<DetectorHit>  dcHits = this.getDetectorHits("DC", path);
        if(dcHits==null) return false;
        if(dcHits.size()<this.detectorMinHits.get("DC")) return false;

        //List<DetectorHit>  ftofHits = this.getDetectorHits("FTOF", path);
        //if(ftofHits.size()<this.detectorMinHits.get("FTOF")) return false;
        
        //List<DetectorHit> ecHits    = this.detectors.get("EC").getLayerHits(path);        
        //System.out.println(" EC LAYER HITS = " + ecHits.size());
        //if(ecHits.size()<this.detectorMinHits.get("EC")) return false;
        return true;
    }
    
    public boolean isDetectedInCentral(Path3D path){
        //if(part.charge()==0) return false;
        //Path3D  path = this.particleSwimmer.particlePath(part);
        ArrayList<Point3D>  intP = new ArrayList<Point3D>();
        int nRegionsCrossed = 0;
        for(int loop = 0; loop < this.SVTDetector.size(); loop++){
            int crossed = 0;
            for(int p = 0; p < path.getNumLines(); p++){
                intP.clear();
                this.SVTDetector.get(loop).intersectionSegment(path.getLine(p), intP);
                if(intP.size()>0) crossed++;
            }
            if(crossed>0) nRegionsCrossed++;
        }
        
        return (nRegionsCrossed==4);
    }
    
    public PhysicsEvent getEvent(PhysicsEvent genEvent){
        
        PhysicsEvent recEvent = new PhysicsEvent();
        
        recEvent.setBeamParticle(genEvent.beamParticle());
        recEvent.setTargetParticle(genEvent.targetParticle());
        
        for(int loop = 0; loop < genEvent.count(); loop++){
            if(this.isParticleDetected(genEvent.getParticle(loop))==true){
                recEvent.addParticle(new Particle(genEvent.getParticle(loop)));
            }
        }
        /**
         * Apply resolution smearing if the flag is set.
         */
        if(this.isSmeared==true){
            for(int p = 0; p < recEvent.count(); p++){
                int pid = recEvent.getParticle(p).pid();                
                if(this.pResolutions.containsKey(pid)==true){
                    this.pResolutions.get(pid).apply(recEvent.getParticle(p),this.torusScale,this.solenoidScale);
                }
            }
        }
        return recEvent;
    }
    
    public Vector3D  getField(double x, double y, double z){
        float[] result = new float[3];
        this.particleSwimmer.getField().field((float) x, (float) y, (float) z, result);
        return new Vector3D(result[0],result[1],result[2]);
    }
}
