/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.clasrec.rec;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.clas.tools.benchmark.Benchmark;
import org.jlab.clas.tools.utils.CommandLineTools;
import org.jlab.clas.tools.utils.JarFileScanner;
import org.jlab.clas.tools.utils.StringTable;

import org.jlab.clasrec.loader.ClasPluginLoader;
import org.jlab.clasrec.main.DetectorReconstruction;
import org.jlab.clasrec.utils.ServiceConfiguration;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.evio.clas12.EvioDataSync;
import org.jlab.evio.clas12.EvioSource;
/**
 *
 * @author gavalian
 */
public class CLASReconstruction {
    
    private RunConditions  runCondition = new RunConditions();
    private Boolean        showStackTrace = false;
    private final ArrayList<DetectorReconstruction>  detectorFactory =
            new ArrayList<DetectorReconstruction>();
    private final ArrayList<String>  detectorFactoryNames = new 
            ArrayList<String>();
    private TreeMap<String,DetectorReconstruction>  detectorFactoryStore
             = new TreeMap<String,DetectorReconstruction>();
    
    private final ClasPluginLoader pluginLoader = new ClasPluginLoader();
    
    private EvioSource  reader               = new EvioSource();
    private Integer     debugLevel           = 0;
    private Integer     maxEventsReconstruct = -1;
    private Integer     skipEvents           = 0;
    private ServiceConfiguration   serviceConfig = new ServiceConfiguration();
    
    public CLASReconstruction(){
        
    }
    
    public void setDebug(boolean flag){
        this.showStackTrace = flag;
    }
    
    public final void setDetectors(String detectorList){
        detectorFactoryNames.clear();
        String[] tokens = detectorList.split(":");
        for(String item : tokens){
            this.detectorFactoryNames.add(item);
        }
    }
    
    public void setDebugLevel(int level){
        this.debugLevel = level;
    }
    
    
    
    public void initServiceConfiguration(ArrayList<String> items){
        for(String configItem : items){
            String[] tokens = configItem.split("=");
            if(tokens.length==2){
                String[] systemItem = tokens[0].split("::");
                if(systemItem.length==2){
                    this.serviceConfig.addItem(systemItem[0], 
                            systemItem[1], tokens[1]);
                }
            }
        }
        System.out.println("****************************************");
        this.serviceConfig.show();
        System.out.println("****************************************");
    }
    
    public void addDetector(DetectorReconstruction rec){
        this.detectorFactory.add(rec);
    }
    
    public void addDetector(String classname){
         try {
            Class   c = Class.forName(classname);
            if(c.getSuperclass()==DetectorReconstruction.class){
                DetectorReconstruction dr = (DetectorReconstruction) c.newInstance();
                this.addDetector(dr);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CLASReconstruction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CLASReconstruction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CLASReconstruction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addDetectorToFactory(String classname){
        try {
            Class   c = Class.forName(classname);
            if(c.getSuperclass()==DetectorReconstruction.class){
                DetectorReconstruction dr = (DetectorReconstruction) c.newInstance();
                this.detectorFactory.add(dr);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CLASReconstruction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CLASReconstruction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CLASReconstruction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void init(){
        for(DetectorReconstruction detectorRec : this.detectorFactory ){
            try {
                System.err.println("[DET-INIT] ---> starting initialization for detector "
                        + detectorRec.getName() );
                detectorRec.setDebugLevel(this.debugLevel);
                detectorRec.configure(serviceConfig);

                if(this.serviceConfig.hasItem("CCDB", "GEOMRUN")==true){
                    detectorRec.setGeometryRun(
                            Integer.parseInt(this.serviceConfig.asString("CCDB", "GEOMRUN")));
                }
                if(this.serviceConfig.hasItem("CCDB", "GEOMVAR")==true){
                    detectorRec.setGeometryVariation(this.serviceConfig.asString("CCDB", "GEOMVAR"));
                }
                
                if(this.serviceConfig.hasItem("CCDB", "CALIBRUN")==true){
                    detectorRec.setCalibrationRun(
                            Integer.parseInt(this.serviceConfig.asString("CCDB", "CALIBRUN")));
                }
                
                if(this.serviceConfig.hasItem("CCDB", "CALIBVAR")==true){
                    detectorRec.setGeometryVariation(this.serviceConfig.asString("CCDB", "CALIBVAR"));
                }
                detectorRec.init();                
                System.err.println("[DET-INIT] ---> detector initialization "
                        + detectorRec.getName() + " ......... success");
            } catch (Exception e) {
                System.err.println("---> (error) initializing detector "
                + detectorRec.getName() + " ......... unsuccesful");
            }
        }
    }
    
    public void initDetectorModules(){
        for(DetectorReconstruction detectorRec : this.detectorFactory ){
            try {
                detectorRec.setDebugLevel(this.debugLevel);
                detectorRec.configure(serviceConfig);
                detectorRec.init();
                System.err.println("[INIT-DETECTORS] ----> detector initialization "
                        + detectorRec.getName() + " ......... ok");
            } catch (Exception e) {
                System.err.println("[INIT-DETECTORS] ----> ERROR initializing detector "
                + detectorRec.getName());
            }
        }
    }
    
    public void initDetectors() {
        
        JarFileScanner  scanner = new JarFileScanner();
        List<String>    classes = scanner.scanDir("lib/plugins", "org.jlab.clasrec.main.DetectorReconstruction");
        
        for(String classname : classes){
            try {
                Class c = Class.forName(classname);
                DetectorReconstruction rec = (DetectorReconstruction) c.newInstance();
                detectorFactoryStore.put(rec.getName(), rec);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(CLASReconstruction.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(CLASReconstruction.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(CLASReconstruction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        this.detectorFactory.clear();
        
        for(String reported : this.detectorFactoryNames){
            if(this.detectorFactoryStore.containsKey(reported)==true){
                this.detectorFactory.add(this.detectorFactoryStore.get(reported));
            }
        }
            /*
            this.detectorFactory.clear();
            TreeMap<String,ICService> detectorClasses = pluginLoader.getPluginLoader().getClassMap();
            for(String detector : this.detectorFactoryNames){
            if(detectorClasses.containsKey(detector)==true){
            System.err.println("[INIT-DETECTORS] ---> found detector ["+detector+"]");
            detectorFactory.add((DetectorReconstruction) detectorClasses.get(detector));
            } else {
            System.err.println("[INIT-DETECTORS] ---> ERROR : detector ["
            +detector+"] not found");
            }
            }
            //for(Map.Entry<String,ICService> service : detectorClasses.entrySet()){
            //}
            for(DetectorReconstruction detectorRec : this.detectorFactory ){
            try {
            detectorRec.setDebugLevel(this.debugLevel);
            detectorRec.configure(serviceConfig);
            detectorRec.init();
            System.err.println("[INIT-DETECTORS] ----> detector initialization "
            + detectorRec.getName() + " ......... ok");
            } catch (Exception e) {
            System.err.println("[INIT-DETECTORS] ----> ERROR initializing detector "
            + detectorRec.getName());
            }
            }*/         
        
    }
    
    public void showAvailableDetectors(){
        
        System.out.println("\n\nSHOW AVAILABLE DETECTORS : " + this.detectorFactoryStore.size());
        System.out.println("\n");
        System.out.println("\t" + StringTable.getCharacterString("*", 65));
        System.out.println("\t" + String.format("* %-12s * %-24s * %8s * %8s *",
                    "MODULE","AUTHOR","VERSION","LANGUAGE"
            )); 
        System.out.println("\t" + StringTable.getCharacterString("*", 65));
        for(Map.Entry<String,DetectorReconstruction> recM : this.detectorFactoryStore.entrySet()){
            
            DetectorReconstruction rec = recM.getValue();
            System.out.println(String.format("\t" + "* %-12s * %-24s * %8s * %8s *", 
                    rec.getName(),rec.getAuthor(),rec.getVersion(),rec.getLanguage()));
        }
        System.out.println("\t" + StringTable.getCharacterString("*", 65));
        System.out.println("\n\n");
    }
    
    public void setSkip(Integer skip){
        this.skipEvents = skip;
    }
    
    public void setMaxEvents(int maxevt){
        this.maxEventsReconstruct = maxevt;
    }
    
    public void initPlugins(){
        System.err.println("[PLUGIN] ----> Initializing Plugins");
        pluginLoader.loadPluginDirectory();
        pluginLoader.show();
    }
    
    public void run(String filename, String output){
        reader.open(filename);
        //this.initDetectors();
        
        
        
        EvioDataSync writer = new EvioDataSync();
        writer.open(output);
        
        Benchmark bench = new Benchmark();
        bench.addTimer("WRITER");
        bench.addTimer("TOTAL");               
        
        Long  processTime = System.currentTimeMillis();
        if(this.serviceConfig.hasItem("DAQ", "data")==false){
            while(reader.hasEvent()&&this.runCondition.isValid()==false){
                EvioDataEvent event  = (EvioDataEvent) reader.getNextEvent();
                String[]      header = event.getString(5, 1);
                this.runCondition.parse(header);
            }
            
            
            this.runCondition.show();
            if(runCondition.hasItem("TORUS_FIELD_SCALE")==true&&this.serviceConfig.hasItem("MAG", "torus")==false){
                this.serviceConfig.addItem("MAG", "torus", runCondition.getDouble("TORUS_FIELD_SCALE"));            
            }
            
            if(runCondition.hasItem("SOLENOID_FIELD_SCALE")==true&&this.serviceConfig.hasItem("MAG", "solenoid")==false){
                this.serviceConfig.addItem("MAG", "solenoid", runCondition.getDouble("SOLENOID_FIELD_SCALE"));            
            }
            
        }
        
        this.initDetectors();
        this.init();
        
        
        for(DetectorReconstruction rec : this.detectorFactory){
            bench.addTimer(rec.getName());
        }
        this.serviceConfig.show();
        int iEventCounter = 0;
        while(reader.hasEvent()){
            iEventCounter++;
            bench.resume("TOTAL");
            EvioDataEvent event = (EvioDataEvent) reader.getNextEvent();
            if(iEventCounter >= this.skipEvents){
                for(DetectorReconstruction rec : this.detectorFactory){
                    bench.resume(rec.getName());
                    try {
                        rec.processEvent(event);
                    } catch (Exception e) {
                        System.err.println("[CLAS-REC] -----> ERROR : excpetion thrown by "
                                + rec.getName());                    
                        e.printStackTrace();
                    }
                    bench.pause(rec.getName());
                }            
                bench.resume("WRITER");
                writer.writeEvent(event);
                bench.pause("WRITER");
            }
            
            bench.pause("TOTAL");
            Long currentTime = System.currentTimeMillis();
            if((currentTime-processTime)>10000){
                processTime = currentTime;
                System.err.println(bench.getTimer("TOTAL").toString());
            }
            if(this.maxEventsReconstruct>0&&iEventCounter>=this.maxEventsReconstruct) break;
        }
        writer.close();
        System.err.println("\n\n" + bench.toString());
    }
    
    public static void printUsage(){
        System.err.println("\n");
        System.err.println("\t FLAGS : use -config SYSTEM::ITEM=value syntax to pass configuration.");
        System.err.println("\n\n Available Configurations :\n");
        System.err.println("\t -config CCDB::GEOMRUN=10        : set ccdb run number to 10 for loading geometry");
        System.err.println("\t -config CCDB::GEOMVAR='custom'  : set ccdb variation to 'custom' for loading geometry");
        System.err.println("\t -config CCDB::CALIBRUN=10       : set ccdb run number to 10 for calibration constant");
        System.err.println("\t -config CCDB::CALIBVAR='custom' : set ccdb variation to 'custom' for calibration constants");
        System.err.println("\t -config MAG::torus=0.75         : set scale for TORUS magnet to 3/4");
        System.err.println("\t -config MAG::solenoid=0.5       : set scale for SOLENOID magnet to 1/2");
        System.err.println("\t -config DCTB::kalman=true       : enable kalman filter in time based tracking");
        System.err.println("\n\n");
        
        System.out.println("\nDATABASE OPTIONS: ");
        System.out.println("\t to use local sqlite database type : setenv CCDB_DATABASE etc/database/clas12database.db ");
        System.out.println();
    }
    
    public static void main(String[] args){
        
        CommandLineTools  cmdParser = new CommandLineTools();
        
        cmdParser.addRequired("-i");
        //cmdParser.addRequired("-s");
        cmdParser.addDescription("-i", "input file name");
        cmdParser.addDescription("-s", "service list to run (e.g. BST:FTOF:EB )");
        cmdParser.addDescription("-o", "output file name");
        cmdParser.addDescription("-n", "number of events to run");
        cmdParser.addDescription("-l", "number of events to skip");
        
        cmdParser.parse(args);
        
        
        Integer nEventsToRun = -1;
        String  outputFileName = "rec_output.evio";
        
        if(cmdParser.isComplete()==false){
            System.err.println(cmdParser.usageString());
            CLASReconstruction.printUsage();
            
            PrintStream originalStreamOut = System.out;
            PrintStream originalStreamErr = System.out;

            PrintStream dummyStream    = new PrintStream(new OutputStream(){
                public void write(int b) {
                    //NO-OP
                }
            });
            System.setOut(dummyStream);
            System.setErr(dummyStream);
            CLASReconstruction scan = new CLASReconstruction();            
            scan.initDetectors();
            System.setOut(originalStreamOut);
            System.setErr(originalStreamErr);
            scan.showAvailableDetectors();
            System.exit(0);
        }
        //if(args.length<2){
        //    CLASReconstruction.printUsage();

        //}
        
        String serviceList  = "FMT:DCHB:DCTB:CVT:FTCAL:FTHODO:FTMATCH:HTCC:FTOFRec:CTOFRec:CTOF:ECREC:EB";
        if(cmdParser.hasOption("-s")){
           serviceList = cmdParser.asString("-s");
        }
        
        String inputFile    = cmdParser.asString("-i");
        String outputFile   = "rec_output.evio";
        
        if(cmdParser.hasOption("-o")==true){
            outputFile = cmdParser.asString("-o");
        }
        
                
        if(cmdParser.hasOption("-n")==true){
            nEventsToRun = cmdParser.asInteger("-n");
        }
        
        
        //if(args.length>2) {
        //    outputFile = args[2];
        //}
        
        CLASReconstruction  clasRec = new CLASReconstruction();
        
        clasRec.initServiceConfiguration(cmdParser.getConfigItems());
        clasRec.setMaxEvents(nEventsToRun);
        clasRec.setDetectors(serviceList);
        
        /*
        clasRec.initDetectors();
        clasRec.init();
        */
        
        //clasRec.initPlugins();
        
        if(cmdParser.hasOption("-d")==true){
            int debugmode = cmdParser.asInteger("-d");
            if(debugmode>0){
                clasRec.setDebug(true);
            }
        }
        
        if(cmdParser.hasOption("-l")==true){
            Integer skip = cmdParser.asInteger("-l");            
            clasRec.setSkip(skip);
        }
        
        clasRec.run(inputFile, outputFile);
        
    }
}
