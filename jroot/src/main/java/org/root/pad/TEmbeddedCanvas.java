/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.pad;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.root.base.DataSetPad;
import org.root.base.IDataSet;

/**
 *
 * @author gavalian
 */
public class TEmbeddedCanvas extends JPanel {
    public   ArrayList<EmbeddedPad>  canvasPads = new  ArrayList<EmbeddedPad>();
    private  int                     canvas_COLUMNS = 1;
    private  int                     canvas_ROWS    = 1;
    private  Integer currentPad = 0;
    
    public TEmbeddedCanvas(){
     super();
     this.setPreferredSize(new Dimension(500,500));
     this.divide(1, 1);
    }
    
    public TEmbeddedCanvas(int xsize, int ysize){
        super();
        this.setPreferredSize(new Dimension(xsize,ysize));
        this.divide(1, 1);
    }
    
     public TEmbeddedCanvas(int xsize, int ysize, int rows, int cols){
        super();
        this.setPreferredSize(new Dimension(xsize,ysize));
        this.divide(rows,cols);
    }
     
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
     
     public void clear(){
         this.canvasPads.get(this.currentPad).getPad().clear();
     }
     
     public void draw(IDataSet dataset){
         //this.canvasPads.get(this.currentPad).getPad().add(dataset);
         //this.canvasPads.get(this.currentPad).repaint();
         this.draw(dataset,"");
     }
     
     public void draw(IDataSet dataset, String option){
         if(option.contains("same")==false){
             this.clear();
         }
         this.canvasPads.get(this.currentPad).getPad().add(dataset,option);
         this.canvasPads.get(this.currentPad).repaint();
     }
     
     public void setLogZ(){
         this.canvasPads.get(this.currentPad).getPad().setLogZ(true);
     }
     
     
     public void setLogX(boolean logFlag){
         this.canvasPads.get(this.currentPad).setLog("X", logFlag);
     }
     
     public void setLogY(boolean logFlag){
         this.canvasPads.get(this.currentPad).setLog("Y", logFlag);
     }
     
     public void setLogZ(boolean logFlag){
         this.canvasPads.get(this.currentPad).setLog("Z", logFlag);
     }
     
     public void setAxisFontSize(int size){
         for(EmbeddedPad pad : this.canvasPads){
             pad.getPad().setAxisFontSize(size);
         }
     }
     
     public void setTitleFontSize(int size){
         for(EmbeddedPad pad : this.canvasPads){
             pad.getPad().setTitleFontSize(size);
         }
     }
     
     public void setStatBoxFontSize(int size){
         for(EmbeddedPad pad : this.canvasPads){
             pad.getPad().setStatBoxFontSize(size);
         }
     }
     
     public void setDivisionsX(int div){
         this.getPad().setDivisionsX(div);
     }
     
     public void setDivisionsY(int div){
         this.getPad().setDivisionsY(div);
     }
     
     public EmbeddedPad getPad(){
         return this.canvasPads.get(this.currentPad);
     }
     
     public void setAxisRange(double xmin, double xmax, double ymin, double ymax){
        this.getPad().setAxisRange(xmin, xmax, ymin, ymax);
    }
     
     public int getCurrentPad(){
         return this.currentPad;
     }
     
     public void incrementPad(){         
         this.currentPad++;
         if(this.currentPad>=this.canvasPads.size()) this.currentPad = 0;
     }
     
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
    
    
    public void update(){
        this.repaint();
    }
    
    public void save(String filename){
        int w = this.getSize().width;
        int h = this.getSize().height;
        try {
            List<DataSetPad>  pads = new ArrayList<DataSetPad>();
            for(EmbeddedPad  pad : this.canvasPads){
                pads.add(pad.getPad());
            }
            TImageCanvas.save(filename, w,h,this.canvas_COLUMNS,this.canvas_ROWS,
                    pads);
            System.out.println(
                    String.format("[TGCanvas::save] ----> size ( %d x %d )  dim (%dx%d)  FILE : %s",
                            w,h,this.canvas_COLUMNS,this.canvas_ROWS,filename));
        } catch (Exception e){
            
        }
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setLayout(new GridLayout(1,3));
        
        TEmbeddedCanvas canvas = new TEmbeddedCanvas(500,500,2,3);
        frame.setSize(800, 600);
        canvas.setTitleFontSize(2);
        canvas.setAxisFontSize(12);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }
}
