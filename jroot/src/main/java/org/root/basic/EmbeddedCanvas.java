/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.basic;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.root.base.IDataSet;
import org.root.histogram.H1D;
import org.root.utils.DataFactory;

/**
 *
 * @author gavalian
 */
public class EmbeddedCanvas extends JPanel {
     public   ArrayList<EmbeddedPad>  canvasPads = new  ArrayList<EmbeddedPad>();
    private  int                     canvas_COLUMNS = 1;
    private  int                     canvas_ROWS    = 1;
    private  Integer                     currentPad = 0;
    
    public EmbeddedCanvas(){
     super();
     this.setPreferredSize(new Dimension(500,500));
     this.divide(1, 1);
    }
    /**
     * Constructor with initial canvas size with one pad division
     * @param xsize
     * @param ysize 
     */
    public EmbeddedCanvas(int xsize, int ysize){
        super();
        this.setPreferredSize(new Dimension(xsize,ysize));
        this.divide(1, 1);
    }
    /**
     * Constructor with initial size and divisions
     * @param xsize
     * @param ysize
     * @param rows
     * @param cols 
     */
    public EmbeddedCanvas(int xsize, int ysize, int rows, int cols){
        super();
        this.setPreferredSize(new Dimension(xsize,ysize));
        this.divide(rows,cols);
    }
    /**
     * Change active pad on the canvas
     * @param pad 
     */
    public void cd(int pad){
        if(pad<0){
             this.currentPad = 0;
             return;
         }
         
         if(pad>=this.canvasPads.size()){
             this.currentPad = 0;
         }
         this.currentPad = pad;
    }
    /**
     * Draw data set on current canvas
     * @param ds 
     */
    public void draw(IDataSet ds){
        this.canvasPads.get(this.currentPad).draw(ds);
    }
    /**
     * Draw data set on current canvas with a options
     * @param ds
     * @param options 
     */
    public void draw(IDataSet ds, String options){
        this.canvasPads.get(this.currentPad).draw(ds,options);
    }
    /**
     * Draw Data set on given pad
     * @param pad
     * @param ds
     * @param options 
     */
    public void draw(int pad, IDataSet ds, String options){
        if(pad>=0&&pad<this.canvasPads.size()){
            this.canvasPads.get(pad).draw(ds,options);
        }
    }
    /**
     * Change Axis font size for all pads
     * @param size 
     */
    public void setAxisFontSize(int size){
        for(EmbeddedPad pad : this.canvasPads){
            pad.setAxisSize(size);
        }
    }
    /**
     * Change axis title string font size
     * @param size 
     */
    public void setAxisTitleFontSize(int size){
        for(EmbeddedPad pad : this.canvasPads){
            pad.setAxisTitleSize(size);
        }
    }
    /**
     * Change pad title font size
     * @param size 
     */
    public void setTitleFontSize(int size){
        for(EmbeddedPad pad : this.canvasPads){
            pad.setTitleSize(size);
        }
    }
    /**
     * Change font size for start box fonts
     * @param size 
     */
    public void setStatBoxFontSize(int size){
         for(EmbeddedPad pad : this.canvasPads){
             pad.setStatBoxSize(size);
             //pad.getPad().setStatBoxFontSize(size);
         }
     }
    /**
     * Divide canvas into given number of column and rows
     * @param rows
     * @param cols 
     */
    public final void divide(int rows, int cols){
        this.canvasPads.clear();
        this.removeAll();
        this.revalidate();
        this.canvas_COLUMNS = rows;
        this.canvas_ROWS    = cols;
        this.setLayout(new GridLayout(cols,rows));
        int xsize = this.getWidth()/cols;
        int ysize = this.getHeight()/rows;
        for(int loop = 0; loop < cols*rows; loop++){
            EmbeddedPad pad = new EmbeddedPad(xsize,ysize);
            canvasPads.add(pad);
            this.add(pad);
        }
        this.revalidate();
        //this.update();
        this.repaint();
    }
    
    public EmbeddedPad  getPad(){
        return this.canvasPads.get(this.currentPad);
    }
    
    public void setDivisionsX(int div){
        //this.getPad().setDivisionsX(div);
    }
     
    public void setDivisionsY(int div){
        //this.getPad().setDivisionsY(div);
    }
    
    public void setAxisRange(double xmin, double xmax, double ymin, double ymax){
        this.canvasPads.get(this.currentPad).setAxisRange(xmin, xmax, ymin, ymax);
    }
    
    public void setGridX(boolean flag){
        this.canvasPads.get(this.currentPad).dataSetFrame.getAxisFrame().setGridX(flag);
    }
    
    public void setGridY(boolean flag){
        this.canvasPads.get(this.currentPad).dataSetFrame.getAxisFrame().setGridY(flag);
    }
    public int getCurrentPad(){
        return this.currentPad;
    }
    
    public void setLogZ(){
        this.setLogZ(true);
    }
    
    
    public void setLogX(boolean logFlag){
        this.canvasPads.get(this.currentPad).dataSetFrame.getAxisFrame().getAxisX().setLog(logFlag);
    }
    
    public void setLogY(boolean logFlag){
        this.canvasPads.get(this.currentPad).dataSetFrame.getAxisFrame().getAxisY().setLog(logFlag);
    }
    
    public void setLogZ(boolean logFlag){
        this.canvasPads.get(this.currentPad).dataSetFrame.getAxisFrame().getAxisZ().setLog(logFlag);
    }
    
    public void update(){
        this.repaint();
    }
    
    public void save(String filename){
        int w = this.getSize().width;
        int h = this.getSize().height;
        try {
            List<DataSetFrame>  pads = new ArrayList<DataSetFrame>();
            for(EmbeddedPad  pad : this.canvasPads){
                pads.add(pad.getPad());
            }
            //TImageCanvas.save(filename, w,h,this.canvas_COLUMNS,this.canvas_ROWS,
            //        pads);
            System.out.println(
                    String.format("[TGCanvas::save] ----> size ( %d x %d )  dim (%dx%d)  FILE : %s",
                            w,h,this.canvas_COLUMNS,this.canvas_ROWS,filename));
        } catch (Exception e){
            
        }
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setLayout(new GridLayout(1,3));
        
        EmbeddedCanvas canvas = new EmbeddedCanvas(500,500,4,4);
        frame.setSize(800, 600);
        canvas.setAxisFontSize(10);
        canvas.setTitleFontSize(10);
        canvas.setAxisTitleFontSize(10);
        canvas.setStatBoxFontSize(8);
        H1D h1a = new H1D("h1a",120,0.0,5.0);
        H1D h1b = new H1D("h1b",120,0.0,5.0);
        h1b.setFillColor(44);
        h1a.setFillColor(48);
        h1b.setTitle("Histogram 1D Random");
        h1b.setXTitle("Random Histogram");
        h1b.setYTitle("Counts");
        
        DataFactory.createSampleH1D(h1a, 2500, 2.5);
        DataFactory.createSampleH1D(h1b, 1500, 1.5);
        canvas.cd(0);
        canvas.draw(h1a);
        canvas.cd(1);
        canvas.draw(h1b);
        canvas.draw(h1a, "same");
        for(int loop = 2; loop < 16; loop++){
            canvas.cd(loop);
            canvas.draw(h1b,"S");
        }
        //canvas.setTitleFontSize(2);
        //canvas.setAxisFontSize(12);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }
}
