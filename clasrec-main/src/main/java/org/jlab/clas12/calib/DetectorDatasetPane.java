/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.calib;

import java.awt.BorderLayout;
import java.awt.Dialog;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.jlab.clas.detector.DetectorCollection;
import org.jlab.clas.detector.DetectorCollectionPane;
import org.jlab.clas.detector.IDetectorCollectionListener;
import org.root.base.IDataSet;
import org.root.histogram.H1D;
import org.root.pad.TEmbeddedCanvas;

/**
 *
 * @author gavalian
 */
public class DetectorDatasetPane extends JPanel implements IDetectorCollectionListener {

    DetectorCollection<? extends IDataSet> detectorCollection = null;
    
    JSplitPane  splitPane = null;
    TEmbeddedCanvas canvas  = null;
    DetectorCollectionPane tree = null;
    
    public DetectorDatasetPane(DetectorCollection<? extends IDataSet> collection){
        super();
        this.detectorCollection = collection;
        this.init();
    }

    public void init(){
        
        this.setLayout(new BorderLayout());
        this.splitPane = new JSplitPane();        
        this.canvas = new TEmbeddedCanvas();
        this.tree   = new DetectorCollectionPane();
        this.tree.setCollection(detectorCollection);
        this.tree.addListener(this);
        
        this.splitPane.setLeftComponent(this.tree);
        this.splitPane.setRightComponent(this.canvas);
        
        this.add(this.splitPane,BorderLayout.CENTER);
        
    }
    
    public void detectorAction(int sector, int layer, int component) {
        if(this.detectorCollection.hasEntry(sector, layer, component)==true){
            canvas.clear();
            canvas.divide(1, 1);
            canvas.cd(0);
            canvas.draw(this.detectorCollection.get(sector, layer, component));
        }
    }
    
    public static void showDialog(DetectorCollection<? extends IDataSet> coll){
        DetectorDatasetPane      pane = new DetectorDatasetPane(coll);
        JDialog dialog = new JDialog();
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(800, 800);
        dialog.setContentPane(pane);
        dialog.setVisible(true);
        
    }
    
    public static void main(String[] args){
        DetectorCollection<H1D>  histograms = new DetectorCollection<H1D>();
        histograms.add(1,1,1, new H1D("h1",20,10.0,50.0));
        DetectorDatasetPane      pane = new DetectorDatasetPane(histograms);
        JDialog dialog = new JDialog();
        dialog.setSize(800, 800);
        dialog.setContentPane(pane);
        dialog.setVisible(true);
        //JOptionPane.showMessageDialog(pane,"Title","InformationMessage",JOptionPane.INFORMATION_MESSAGE);
    }
}
