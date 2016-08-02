/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.basic;

import java.awt.Color;
import java.awt.Graphics2D;
import org.root.base.DataRegion;
import org.root.base.DataSetCollection;
import org.root.base.IDataSet;
import org.root.base.PaveText;
import org.root.func.F1D;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;
import org.root.histogram.H2D;

/**
 *
 * @author gavalian
 */
public class DataSetFrame {
    
    private  DataSetCollection   collection   = new DataSetCollection();
    private  GraphicsAxisFrame   axisFrame    = new GraphicsAxisFrame();
    private  PaveText            datasetStats = new PaveText();
    private  int                 statBoxFontSize = 12;
    private  boolean 			 showStats = true;
    private  boolean[]           statsOptions = {true,true,true,true};
    private  String              statBoxFont = "Avenir";
    public  DataSetFrame(){
        
    }
    
    
    public GraphicsAxisFrame getAxisFrame(){
        return this.axisFrame;
    }
    
    public void drawOnCanvas(Graphics2D g2d, int xoffset, int yoffset, int w, int h){
        if(collection.getCount()==0){
            g2d.setColor(Color.WHITE);
            g2d.fillRect(xoffset, yoffset, w, h);
            return;
        }
        
        this.updateStatBox();
        DataRegion  region = this.collection.getDataRegion();
        
        if(this.axisFrame.getAxisX().rangeFixed()==false){

            ( (GraphicsAxisNumber) this.axisFrame.getAxisX()).setRange(
                    region.MINIMUM_X,region.MAXIMUM_X);
        }
        if(this.axisFrame.getAxisY().rangeFixed()==false){
            ( (GraphicsAxisNumber) this.axisFrame.getAxisY()).setRange(
                    region.MINIMUM_Y,this.collection.getDataRegion().MAXIMUM_Y);
        }
        //System.out.println(region);
        
        this.axisFrame.drawOnCanvas(g2d, xoffset, yoffset, w, h);
        
        g2d.setClip(
                xoffset + this.axisFrame.getMargins().x + 1, 
                yoffset + this.axisFrame.getMargins().y + 1 , 
                this.axisFrame.getMargins().width  - 1, 
                this.axisFrame.getMargins().height - 1
                );
        for(int loop = 0; loop < this.collection.getCount(); loop++){
            if(this.collection.getDataSet(loop) instanceof H1D){
                if(this.collection.getDataSetOption(loop).contains("E")==true){
                    AbstractGraphicsFrameDraw.drawOnCanvasHistogram1D_EP(
                            g2d, axisFrame, this.collection.getDataSet(loop), 
                            xoffset, yoffset,w, h);
                } else {
                    AbstractGraphicsFrameDraw.drawOnCanvasHistogram1D(
                            g2d, axisFrame, this.collection.getDataSet(loop), 
                            xoffset, yoffset,w, h);
                }
            }
            
            if(this.collection.getDataSet(loop) instanceof F1D){
                AbstractGraphicsFrameDraw.drawOnCanvasFunction(
                        g2d, axisFrame, this.collection.getDataSet(loop), 
                        xoffset, yoffset,w, h);
            }
            
            if(this.collection.getDataSet(loop) instanceof GraphErrors){
                AbstractGraphicsFrameDraw.drawOnCanvasGraph(
                        g2d, axisFrame, this.collection.getDataSet(loop), 
                        xoffset, yoffset,w, h);
            }
            
            if(this.collection.getDataSet(loop) instanceof H2D){
                AbstractGraphicsFrameDraw.drawOnCanvasHistogram2D(
                            g2d, axisFrame, this.collection.getDataSet(loop), 
                            xoffset, yoffset,w, h);
            }
        }
        g2d.setClip(null);
        if(this.datasetStats.getTexts().size()>0){
            this.datasetStats.drawOnCanvas(g2d, 
                    xoffset + this.axisFrame.getMargins().x + this.axisFrame.getMargins().width,
                    yoffset + this.axisFrame.getMargins().y,
                    2);
        }
        //this.axisFrame.drawOnCanvas(g2d, xoffset, yoffset, w, h);
    }
    
    public void clear(){
        this.collection.clear();
    }
    public void remove(IDataSet ds){
    	this.collection.removeDataSet(ds);
    }
    
    public void add(IDataSet ds){
        this.collection.clear();
        this.add(ds, "");
    }
    
    
    public void add(IDataSet ds,String option){
        boolean alreadyInCollection = false;
        int pos = 0;
        String previousOptions = "";
        for(int i=0; i<this.collection.getCount(); i++){
        	if(this.collection.getDataSet(i).equals(ds)){
        		pos = i;
        		alreadyInCollection = true;
        		previousOptions = this.collection.getDataSetOption(i);
        	}
        }
    	
        if(option.contains("same")==false){
            this.collection.clear();
        }
        if(!alreadyInCollection){
        	this.collection.addDataSet(ds,option);
        }else{
        	if(!previousOptions.equals(option)){
        		this.collection.setDataSetOption(pos,option);
        	}
        }
        if(this.collection.getCount()==1){
            //System.out.println("Adding the title");
            try {
                this.axisFrame.setTitle(ds.getAttributes().getProperties().getProperty("title"));
                this.axisFrame.getAxisX().setTitle(ds.getAttributes().getProperties().getProperty("xtitle"));
                this.axisFrame.getAxisY().setTitle(ds.getAttributes().getProperties().getProperty("ytitle"));           
            } catch (Exception e){
                
            }            
        }
        //this.updateStatBox();
    }
    
    public void updateStatBox(){
        this.datasetStats.clear();
        int ndatasets = this.collection.getCount();
        for(int i = 0; i < ndatasets; i++){
            if(this.collection.getDataSetOption(i).contains("S")==true){
                if(this.collection.getDataSet(i) instanceof H1D){
                    H1D h = (H1D) this.collection.getDataSet(i);
                    this.datasetStats.addText(String.format("%-9s %10s", "Name",h.getName()));
                    this.datasetStats.addText(String.format("%-9s %10d", "Entries",h.getEntries()));
                    this.datasetStats.addText(String.format("%-9s %10.5f", "Mean",h.getMean()));
                    this.datasetStats.addText(String.format("%-9s %10.5f", "RMS",h.getRMS()));                
                }
                if(this.collection.getDataSet(i) instanceof F1D){
                    F1D f = (F1D) this.collection.getDataSet(i);
                    for(int p = 0; p < f.getNParams(); p++){
                        this.datasetStats.addText(String.format("%-9s %10.5f", 
                                f.parameter(p).name(),
                                f.parameter(p).value()));
                    }
                }
            }             
        }
        this.setStatBoxFontSize(this.statBoxFontSize);
    }
    
    public void setStatBoxFontSize(int size){
        this.statBoxFontSize = size;
        this.datasetStats.setFont(statBoxFont, size);
    }
    
    public void setStatBoxFontName(String font){
        this.statBoxFont = font;
        this.datasetStats.setFont(statBoxFont, this.statBoxFontSize);
    }
    
    public int getStatBoxFontSize(){
        return this.statBoxFontSize;
     }
    
    public String getStatBoxFontName(){
       return this.statBoxFont;
    }
    
    public DataSetCollection  getCollection(){
        return this.collection;
    }
    
    public void  setCollection(DataSetCollection collection){
        this.collection = collection;
    }
    public PaveText getStatsPaveText(){
    	return this.datasetStats;
    }
    
    public void setStatsPaveText(PaveText text){
    	this.datasetStats = text;
    }
}
