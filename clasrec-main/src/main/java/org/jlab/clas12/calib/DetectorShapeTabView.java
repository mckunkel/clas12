/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.calib;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clasrec.main.DetectorCalibration;
import org.jlab.geom.prim.Path3D;
import org.root.pad.TEmbeddedCanvas;

/**
 *
 * @author gavalian
 */
public class DetectorShapeTabView extends JPanel implements ActionListener {
    
    private		      JTabbedPane tabbedPane;
    private final             TreeMap<String, DetectorShapeView2D>  detectorView = new TreeMap<String, DetectorShapeView2D>();
    private DetectorCalibration  calibrationModule = null;
    private TEmbeddedCanvas       drawCanvas        = null;
    private String               drawOptions       = "default";
    private List<IDetectorListener>         detectorListeners = new ArrayList<IDetectorListener>();
    
    public DetectorShapeTabView(){
        super();
        this.setPreferredSize(new Dimension(600,600));
        this.setLayout(new BorderLayout());
        this.initComponents();
    }
    
    public DetectorShapeTabView(int sizeX, int sizeY){
        super();
        this.setPreferredSize(new Dimension(sizeX,sizeY));
        this.setLayout(new BorderLayout());
        this.initComponents();
    }
    
    private void initComponents(){
        tabbedPane = new JTabbedPane();        
        this.add(tabbedPane,BorderLayout.CENTER);
    }
    
    public void setDrawOptions(String options){
        this.drawOptions = options;
    }
    
    public void setCalibrationModule(DetectorCalibration calib){
        this.calibrationModule = calib;
        List<DetectorShapeView2D>  views = calib.getDetectorShapes();
        this.detectorView.clear();
        this.tabbedPane.removeAll();
        for(DetectorShapeView2D view : views){
            this.addDetectorLayer(view);
        }
    }
    
    public void addDetectorLayer( DetectorShapeView2D view){
        tabbedPane.addTab( view.getName(), view);
        detectorView.put(view.getName(), view);
        view.setActionListener(this);
    }
    
    
    public void addDetectorListener(IDetectorListener lt){
        for(Map.Entry<String, DetectorShapeView2D> entry : this.detectorView.entrySet()){
            entry.getValue().addDetectorListener(lt);
        }
    }
    public void setCanvas(TEmbeddedCanvas cvn){
        this.drawCanvas = cvn;
    }
    
    public void initWith(DetectorCalibration calib){
        TreeMap<String,DetectorShapeView2D>  map = calib.getDetectorList();
        for(Map.Entry<String,DetectorShapeView2D> entry : map.entrySet()){
            this.addDetectorLayer(entry.getValue());
        }
    }
    
    public static void main(String[] args){
        
        JFrame frame = new JFrame();
        DetectorShapeTabView  tab = new DetectorShapeTabView();
        //DetectorCalibration calib = new DetectorCalibration("a","b","1.0");
        //tab.initWith(calib);
        DetectorShapeView2D shapeView = new DetectorShapeView2D("FTOF");
        
        int ncounters = 30;
        double angle  = 360.0/30.0; 
        
        for(int loop = 0; loop < ncounters; loop++){
            DetectorShape2D shape = new DetectorShape2D();
            
            //shape.createArc(80, 120, loop*60-25, loop*60+25);
            shape.createTrapXY(60, 80, 80);
            shape.getShapePath().translateXYZ(0, -350
                    , 0);
            shape.getShapePath().rotateZ(Math.toRadians(angle*loop));
            //shape.createBarXY(18, 50);
            //shape.getShapePath().translateXYZ(20*loop, 0, 0);
            if(loop%2==0){
                shape.setColor(0, 0, 200);
            } else {
                shape.setColor(0, 200, 0);
            }
            shapeView.addShape(shape);
        }
        
        shapeView.addHit(0, 0, 0,0,0,0);
        Path3D  path = new Path3D();
        path.addPoint(0, 0, 0);
        path.addPoint(10,10,10);
        path.addPoint(30,40,10);
        shapeView.addPath(path, 255,0,0);
        
        DetectorShapeView2D shapeView2 = new DetectorShapeView2D("EC");
        
        for(int loop = 0; loop < 35; loop++){
            DetectorShape2D shape = new DetectorShape2D();
            shape.getDescriptor().setType(DetectorType.EC);
            shape.getDescriptor().setSectorLayerComponent(0,0,loop);
            shape.createBarXY(18, 50);
            shape.getShapePath().translateXYZ(20*loop, 0, 0);
            if(loop%2==0){
                shape.setColor(200, 100, 0);
            } else {
                shape.setColor(0, 200, 100);
            }
            shapeView2.addShape(shape);
        }
        
        DetectorShapeView2D shapeView3 = new DetectorShapeView2D("DC");
        
        for(int sector = 0; sector<6; sector++){
            for(int superlayer = 0; superlayer<6; superlayer++){
                DetectorShape2D shape = new DetectorShape2D();
                shape.getDescriptor().setType(DetectorType.DC);
                shape.getDescriptor().setSectorLayerComponent(sector,superlayer,0);
                shape.createBarXY(20,40);
                shape.getShapePath().translateXYZ(superlayer*25,sector*45,0);
                if(sector%2==0){
                    if(superlayer%2==0){
                        shape.setColor(200, 100, 0);
                    } else {
                        shape.setColor(180,  90, 0);
                    }
                } else {
                    if(superlayer%2==0){
                        shape.setColor(0, 200, 100);
                    } else {
                        shape.setColor(0, 180,  90);
                    }
                }
                shapeView3.addShape(shape);
            }
        }
        
        
        tab.addDetectorLayer(shapeView);
        tab.addDetectorLayer(shapeView2);
        tab.addDetectorLayer(shapeView3);
        frame.add(tab);
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        //System.out.println("Action performed " + e.getActionCommand());
        String viewName = e.getActionCommand();
        if(detectorView.containsKey(viewName)==true){
            DetectorShapeView2D  viewer = detectorView.get(viewName);
            for(DetectorShape2D shape : viewer.getShapes()){
                this.calibrationModule.update(shape);
            }
            viewer.repaint();
            
            DetectorShape2D shape = detectorView.get(viewName).getSelectedShape();
            if(shape!=null){
                //System.out.println("DESCRIPTOR = " + shape.getDescriptor().toString());
                
                this.calibrationModule.draw(drawCanvas, shape.getDescriptor(), this.drawOptions);
            }
        }
    }
}
