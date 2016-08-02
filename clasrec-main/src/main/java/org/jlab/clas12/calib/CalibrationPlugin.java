/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.calib;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jlab.evio.clas12.EvioDataEvent;
import org.root.pad.TEmbeddedCanvas;

/**
 *
 * @author gavalian
 */
public abstract class CalibrationPlugin implements ActionListener {
    
    private DetectorH1D     store = new DetectorH1D();
    private TEmbeddedCanvas canvas = new TEmbeddedCanvas();
    private JPanel         panel  = new JPanel();
    private JTable         table  = new JTable();
    
    public CalibrationPlugin(){
        
    }
    
    public String[] getButtons(){
        return new String[]{"Fit","Analyze","Check"};
    }
    
    
    
    public abstract void process(EvioDataEvent event);
    public abstract void init();
 
    
    public TEmbeddedCanvas getCanvas() { return this.canvas;}
    public JPanel         getPanel()  { return this.panel;}
    public JTable         getTable()  { return this.table; }
    public DetectorH1D    getStore()  { return this.store; }

    /*
    public void updateTable(){
        DefaultTableModel  model = new DefaultTableModel(
                new Object[] {"Sector","Layer","Comp","Fit","Error"},0
        );
        
        this.table.setModel(model);
       
    }*/
    
    public void actionPerformed(ActionEvent e) {
        this.action(e.getActionCommand());        
    }
    
    public void action(String button){
        
    }
    
    public void draw(){
        
    }
}
