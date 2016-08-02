/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.calib;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.jlab.clas.detector.ConstantsTable;
import org.jlab.clas.detector.ConstantsTablePanel;
import org.jlab.clas.detector.DetectorType;
import org.root.pad.TEmbeddedCanvas;

/**
 *
 * @author gavalian
 */
public class CalibrationControlPanel extends JPanel {
    
    String[] buttons = new String[]{"Fit","Custom Fit","View"};
    TEmbeddedCanvas canvas = new TEmbeddedCanvas(600,600,1,1);
    ConstantsTablePanel  constPanel = null;
    
    JPanel  buttonPanel   = null;
    JPanel  upPanel       = null;
    JPanel  downPanel     = null;
    JSplitPane splitPane  = null;
    
    
    
    public CalibrationControlPanel(){
        super();
        this.setLayout(new BorderLayout());
        this.init();
    }
    
    
    public JPanel  getButtonPane(){ return this.buttonPanel;}
    public JPanel  getTopPane(){ return this.upPanel;}
    public JPanel  getButtomPane(){ return this.downPanel;}
    
    public final void init(){
        
        this.splitPane  = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.upPanel    = new JPanel();
        this.downPanel  = new JPanel();
        
        this.upPanel.setLayout(new BorderLayout());
        this.downPanel.setLayout(new BorderLayout());
        
        this.splitPane.setTopComponent(this.upPanel);
        this.splitPane.setBottomComponent(this.downPanel);
        
        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new FlowLayout());
        
        this.add(splitPane,BorderLayout.CENTER);
        this.add(buttonPanel,BorderLayout.PAGE_END);
        /*
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setLayout(new FlowLayout());
        for(String bt : buttons){
            JButton btn = new JButton(bt);
            btn.setBackground(Color.CYAN);
            panel.add(btn);
        }

        
        JPanel cpanel = new JPanel();
        cpanel.setLayout(new BorderLayout());
        cpanel.add(canvas,BorderLayout.CENTER);
        
        ConstantsTable      table = new ConstantsTable(DetectorType.FTOF,new String[]{"geoMean","error","Quality"});
        
        table.addEntry(1, 1, 1);
        table.addEntry(1, 1, 2);
        table.addEntry(1, 1, 3);
        table.addEntry(1, 1, 4);
        table.getEntry(1, 1, 1).setData(0, 0.5);
        constPanel = new ConstantsTablePanel(table);
        
        this.add(cpanel,BorderLayout.PAGE_START);
        this.add(constPanel,BorderLayout.CENTER);
        this.add(panel,BorderLayout.PAGE_END);
        */
    }
    
    public static void main(String[] args){
        
        JFrame frame = new JFrame();
        frame.setSize(900, 700); 
        CalibrationControlPanel control = new CalibrationControlPanel();
        control.getTopPane().add(new TEmbeddedCanvas(500,500,1,1));
        
        control.getButtonPane().add(new JButton("Fit"));
        
        ConstantsTable      table = new ConstantsTable(DetectorType.FTOF,new String[]{"geoMean","error","Quality"});
        control.getButtomPane().add(new ConstantsTablePanel(table));
        frame.add(control,BorderLayout.CENTER);
        //frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
