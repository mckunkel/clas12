/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.root.base.IDataSet;
import org.root.func.F1D;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;
import org.root.histogram.H2D;
import org.root.utils.DataFactory;

/**
 *
 * @author gavalian
 */
public class EmbeddedPad extends JPanel {
    
    DataSetFrame  dataSetFrame = new DataSetFrame();
    Color bgColor = Color.white;
    public EmbeddedPad(){
        super();
        this.setPreferredSize(new Dimension(500,500));
        this.dataSetFrame.getAxisFrame().setTitleFontSize(12);
        this.dataSetFrame.getAxisFrame().getAxisX().setTitleSize(12);
        this.dataSetFrame.getAxisFrame().getAxisX().setAxisFontSize(12);
        this.dataSetFrame.getAxisFrame().getAxisY().setTitleSize(12);
        this.dataSetFrame.getAxisFrame().getAxisY().setAxisFontSize(12);
        this.dataSetFrame.setStatBoxFontSize(12);
        //this.setSize(500, 500);
    }
    
    public EmbeddedPad(int xsize, int ysize){
        super();
        this.setPreferredSize(new Dimension(xsize,ysize));
        //this.setSize(500, 500);
        this.dataSetFrame.getAxisFrame().setTitleFontSize(12);
        this.dataSetFrame.getAxisFrame().getAxisX().setTitleSize(10);
        this.dataSetFrame.getAxisFrame().getAxisX().setAxisFontSize(10);
        this.dataSetFrame.getAxisFrame().getAxisY().setTitleSize(10);
        this.dataSetFrame.getAxisFrame().getAxisY().setAxisFontSize(10);
    }
    @Override
    public void paint(Graphics g){        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int w = this.getSize().width;
        int h = this.getSize().height;
        g2d.setBackground(bgColor);
        this.dataSetFrame.drawOnCanvas(g2d, 0,0, w, h);
    }
    
    
    public void draw(IDataSet ds){
        this.dataSetFrame.add(ds);
        this.repaint();
    }
    
    public DataSetFrame  getPad(){
        return this.dataSetFrame;
    }
    
    public void setBackgroundColor(Color color){
    	this.bgColor = color;
    }
    
    public Color getBackgroundColor(){
    	return this.bgColor;
    }
    
    public void setLogX(boolean logFlag){
        this.dataSetFrame.getAxisFrame().getAxisX().setLog(logFlag);
    }
    
    public void setLogY(boolean logFlag){
    	this.dataSetFrame.getAxisFrame().getAxisY().setLog(logFlag);
    }
    
    public void setLogZ(boolean logFlag){
    	this.dataSetFrame.getAxisFrame().getAxisZ().setLog(logFlag);
    }
    
    
    
    public boolean getLogX(){
       return this.dataSetFrame.getAxisFrame().getAxisX().isLog();
    }
    
    public boolean getLogY(){
        return this.dataSetFrame.getAxisFrame().getAxisY().isLog();
    }
    
    public boolean getLogZ(){
        return this.dataSetFrame.getAxisFrame().getAxisZ().isLog();
    }
    
    
    
    
    
    
    
    
    public void draw(IDataSet ds, String options){
        this.dataSetFrame.add(ds, options);
        this.repaint();
    }
    
    public void setAxisSize(int size){
        this.dataSetFrame.getAxisFrame().getAxisX().setAxisFontSize(size);
        this.dataSetFrame.getAxisFrame().getAxisY().setAxisFontSize(size);
    }
    
    public void setStatBoxSize(int size){
        this.dataSetFrame.setStatBoxFontSize(size);
    }
    
    public void setAxisTitleSize(int size){
        this.dataSetFrame.getAxisFrame().getAxisX().setTitleSize(size);
        this.dataSetFrame.getAxisFrame().getAxisY().setTitleSize(size);
    }
    
   /* public void setLogZ(boolean flag){
        this.dataSetFrame.getAxisFrame().getAxisZ().setLog(flag);
    }*/
    
    public void setTitleSize(int size){
        this.dataSetFrame.getAxisFrame().setTitleFontSize(size);
    }
    
    public void setAxisRange(double xmin, double xmax, double ymin, double ymax){
        this.setAxisRange("X", xmin, xmax);
        this.setAxisRange("Y", ymin, ymax);
    }
    
    public void setAxisRange(String axis, double min, double max){
        if(axis.compareTo("X")==0){
            ((GraphicsAxisNumber) this.dataSetFrame.getAxisFrame().getAxisX()).setRange(min, max);
            this.dataSetFrame.getAxisFrame().getAxisX().rangeFixed(true);
        }
        if(axis.compareTo("Y")==0){
            ((GraphicsAxisNumber) this.dataSetFrame.getAxisFrame().getAxisY()).setRange(min, max);
            this.dataSetFrame.getAxisFrame().getAxisY().rangeFixed(true);
        }
    }
    
    public GraphicsAxisFrame  getAxisFrame(){        
        return this.dataSetFrame.getAxisFrame();
    }
    
    public GraphicsAxis  getAxisX(){
        return this.dataSetFrame.getAxisFrame().getAxisX();
    }
    public GraphicsAxis  getAxisY(){
        return this.dataSetFrame.getAxisFrame().getAxisY();
    }
    public GraphicsAxis  getAxisZ(){
        return this.dataSetFrame.getAxisFrame().getAxisZ();
    }
    public int getDataSetCount(){
        return this.dataSetFrame.getCollection().getCount();
    }
    
    public IDataSet  getDataSet(int index){
        return this.dataSetFrame.getCollection().getDataSet(index);
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setSize(600,600);
        EmbeddedPad pad = new EmbeddedPad();
        //pad.setAxisRange("Y", 0.0, 600.0);
        //pad.setAxisRange(0.5,2.0, 0.0, 600.0);
        pad.setAxisSize(14);
        pad.setAxisTitleSize(14);
        pad.setTitleSize(14);
        pad.setStatBoxSize(12);
                        
        H1D  h1 = new H1D("h1",100,0.0,2.5);
        h1.setTitle("Sample Histogram");
        h1.setXTitle("M^2 (#phi)");
        h1.setYTitle("counts");
        h1.setFillColor(22);
        F1D f1 = new F1D("gaus",0.0,2.5);
                
        f1.setParameter(0, 150);
        f1.setParameter(1, 1.5);
        f1.setParameter(2, 0.5);

        //pad.draw(f1,"");
        DataFactory.createSampleH1D(h1, 4000, 1.5);
        GraphErrors graph = h1.getGraph();
        h1.fit(f1, "REQ");
        graph.setMarkerSize(2);
        f1.setLineColor(4);
        f1.setLineWidth(4);
        f1.setLineStyle(2);
        pad.draw(h1,"EPS");
        pad.draw(f1,"sameS");
        //pad.draw(h1,"S");
        //pad.draw(f1,"S");
        //pad.draw(f1,"S");
        //pad.draw(f1, "same");
        
        H2D  h2 = new H2D("h2",60,0.0,12.0,60,0.0,12.0);
        h2.setTitle("H2D RANDOM GAUSSIAN");
        h2.setXTitle("Random");
        h2.setYTitle("Random");
        DataFactory.createSampleH2D(h2, 864000);
        //pad.setLogZ(true);
        //pad.draw(h2, "");
        
        frame.add(pad,BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
