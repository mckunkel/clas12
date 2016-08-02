/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx.base;


import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author gavalian
 */
public class FXEmbeddedPad {
    
    private Dimension2D  regionPad  = new Dimension2D();
    private Dimension2D  regionAxis = new Dimension2D();
    private Dimension2D  regionData = new Dimension2D();
    
    private Region2D  graphicsPadRegion  = new Region2D();
    private Region2D  graphicsPadFrame   = new Region2D();
    private Region2D  graphicsDataRegion = new Region2D();
    
    private Color     graphicsPadBackground = Color.WHITE;
    
    public  List<IRegionDataPlotter>   dataPlotters = new ArrayList<>();
    
    public FXEmbeddedPad(){
        
    }
    
    public FXEmbeddedPad(double x, double y, double width, double height){
        //this.graphicsPadRegion.set(x, y, width,height);
        regionPad.set(x, x + width, y, y + height);
    }
    
    public Region2D  getRegion(){ return this.graphicsPadRegion;}
    public Region2D  getFrame() { return this.graphicsPadFrame;}
    
    public Dimension2D  getPadRegion(){ return this.regionPad;}
    public Dimension2D  getAxisRegion(){ return this.regionAxis;}
    public Dimension2D  getDataRegion(){ return this.regionData;}
    
    public void clear(){
        this.dataPlotters.clear();
    }
    
    public void addDataPlotter(IRegionDataPlotter plotter){
        this.dataPlotters.add(plotter);
    }
    
    public void draw(GraphicsContext gc){
        
        updateFrame();
        gc.clearRect( 
                this.regionPad.getDimension(0).getMin(),
                this.regionPad.getDimension(1).getMin(),
                this.regionPad.getDimension(0).getLength(),
                this.regionPad.getDimension(1).getLength());
        gc.setFill(graphicsPadBackground);
        
        gc.fillRect(this.regionPad.getDimension(0).getMin(),
                this.regionPad.getDimension(1).getMin(),
                this.regionPad.getDimension(0).getLength(),
                this.regionPad.getDimension(1).getLength());
                
        
        gc.setStroke(Color.BLACK);
        
        gc.strokeRect(this.regionAxis.getDimension(0).getMin(),
                this.regionAxis.getDimension(1).getMin(),
                this.regionAxis.getDimension(0).getLength(),
                this.regionAxis.getDimension(1).getLength());
                
        
        
        this.updateDataRegion();
        /*
        gc.save();
        gc.beginPath();        
        gc.rect(
                regionAxis.getDimension(0).getMin(),
                regionAxis.getDimension(1).getMin(),
                regionAxis.getDimension(0).getLength(),
                regionAxis.getDimension(1).getLength()
                );
        gc.closePath();
        gc.clip();
        */
        
        for(IRegionDataPlotter plotter : this.dataPlotters){
            plotter.draw(gc, regionAxis, regionData);
        }
        //gc.restore();    
        
    }
    
    private void updateDataRegion(){
        if(this.dataPlotters.size()>0){
            this.regionData.copy(this.dataPlotters.get(0).getDataRegion());
            System.out.println("--> " + this.dataPlotters.get(0).getDataRegion());
        }

        System.out.println(this.regionData.toString());
    }
    
    public void  updateFrame(){
        this.regionAxis.set(
                this.regionPad.getDimension(0).getMin()+5.0,
                this.regionPad.getDimension(0).getMax()-5.0,
                this.regionPad.getDimension(1).getMin()+5.0,
                this.regionPad.getDimension(1).getMax()-5.0
                
        );

    }
}
