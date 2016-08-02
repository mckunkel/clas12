/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.calib;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import org.jlab.clasrec.main.DetectorCalibration;
import org.jlab.evio.clas12.EvioDataEvent;
import org.root.attr.TStyle;
import org.root.pad.TEmbeddedCanvas;

/**
 *
 * @author gavalian
 */
public class DetectorCalibrationFrame extends JInternalFrame implements ActionListener {
    
    JSplitPane       splitPane = new JSplitPane();
    TEmbeddedCanvas   canvas    = new TEmbeddedCanvas();
    DetectorShapeTabView tabView = null;
    private		      JTabbedPane rightTabbedPane = null;
    
    private DetectorCalibration  calibration = null;
    
    public DetectorCalibrationFrame(DetectorCalibration calib){                 
        super("Detector Plugin",true,true,true,true);
        TStyle.setStatBoxFont("Courier New", 12);
        tabView = new DetectorShapeTabView();
        this.calibration = calib;
        tabView.initWith(calib); 
        this.rightTabbedPane = new JTabbedPane();
        this.rightTabbedPane.add("Canvas", this.canvas);
        splitPane = new JSplitPane();
        splitPane.setSize(1200, 600);
        splitPane.setPreferredSize(new Dimension(1200,600));
        splitPane.setDividerLocation(550);
        splitPane.setLeftComponent(this.tabView);
        
        splitPane.setRightComponent(this.rightTabbedPane);
        //splitPane.setRightComponent(this.canvas);
        
        tabView.setCalibrationModule(calib);
        tabView.setCanvas(canvas);
        this.add(splitPane);
        this.initMenuBar();
        this.pack();
        this.setVisible(true);
    }
    
    public void processEvent(EvioDataEvent event){
        try{
            this.calibration.processEvent(event);
        } catch (Exception e){
            
        }
    }
    
    public void updateView(EvioDataEvent event){
        try {
            this.calibration.updateView(event);
        } catch(Exception e){
            System.out.println("----> something went wrong with this event");
        }
    }
    public void reset(){
        this.calibration.init();
    }
    
    public void initMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu    menuOptions = new JMenu("Options");
        String[] tokens = this.calibration.getOptions();
        for(int loop = 0; loop < tokens.length; loop++){
            JMenuItem item = new JMenuItem(tokens[loop]);
            item.addActionListener(this);
            menuOptions.add(item);
        }
        menuBar.add(menuOptions);
        this.setJMenuBar(menuBar);
        
    }
    
    public static void main(String[] args){
        
    }

    public void actionPerformed(ActionEvent e) {
        this.tabView.setDrawOptions(e.getActionCommand());
    }
}
