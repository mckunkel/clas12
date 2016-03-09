/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.pad;

import de.erichseifert.vectorgraphics2d.PDFGraphics2D;
import de.erichseifert.vectorgraphics2d.SVGGraphics2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.root.base.DataSetPad;
import org.root.base.IDataSet;
import org.root.basic.DataSetFrame;

/**
 *
 * @author gavalian
 */
public class TImageCanvas {
    
    private String  imageType = "png";
    //private ArrayList<DataSetPad>  dataSets = new ArrayList<DataSetPad>();
    private ArrayList<DataSetFrame>  dataSets = new ArrayList<DataSetFrame>();
    private Integer canvasWidth  = 500;
    private Integer canvasHeight = 500;
    private Integer nDivisionsX  = 1;
    private Integer nDivisionsY  = 1;
    private Integer currentPad   = 0;
    
    public TImageCanvas(){
        
    }
    
    public TImageCanvas(String name, String title, int xsize, int ysize, int ncol, int nrow){
        this.setSize(xsize,ysize);
        this.divide(ncol, nrow);
    }
    
    public final void setSize(int sx, int sy){
        this.canvasWidth = sx;
        this.canvasHeight = sy;
    }
    
    public final void divide(int nx, int ny){
        this.nDivisionsX = nx;
        this.nDivisionsY = ny;
        this.dataSets.clear();
        for(int loop = 0; loop < nx*ny; loop++){
            this.dataSets.add(new DataSetFrame());
        }
        this.currentPad = 0;
    }
    
    public void draw(IDataSet set, String options){
        /*if(options.contains("same")==false){
            this.dataSets.get(currentPad).clear();
        }
        this.dataSets.get(currentPad).add(set);
        */
        this.dataSets.get(currentPad).add(set,options);
    }
    
    public void draw(IDataSet set){
        this.dataSets.get(currentPad).add(set);
        //this.dataSets.get(currentPad).add(set);
    }
    
    public void cd(int pad){
        currentPad = pad;
        if(pad<0){
            currentPad = 0;
        } else if(currentPad>=this.dataSets.size()){
            currentPad = this.dataSets.size() - 1;
        }
        
    }
    
    public void setDataSet(){
        
    }
    
    public void saveSVG(String filename){
        SVGGraphics2D ig2 = new SVGGraphics2D(0.0, 0.0, this.canvasWidth, this.canvasHeight);
        int padWidth  = (int) this.canvasWidth/this.nDivisionsX;
        int padHeight = (int) this.canvasHeight/this.nDivisionsY;
        
        int counter = 0;
        for(int cY = 0; cY < this.nDivisionsY; cY++){
            for(int cX = 0; cX < this.nDivisionsX; cX++){
                int offsetX = cX * padWidth;
                int offsetY = cY * padHeight;
                this.dataSets.get(counter).drawOnCanvas(ig2, offsetX, offsetY, padWidth,padHeight);
                counter++;
            }
        } 
        try {
            FileOutputStream file = new FileOutputStream(filename);
            
            file.write(ig2.getBytes());        
            file.close();
        } catch(IOException e){
            
        }
    }
    
    public void savePDF(String filename){
        PDFGraphics2D ig2 = new PDFGraphics2D(0.0, 0.0, this.canvasWidth, this.canvasHeight);
         int padWidth  = (int) this.canvasWidth/this.nDivisionsX;
        int padHeight = (int) this.canvasHeight/this.nDivisionsY;
        
        int counter = 0;
        for(int cY = 0; cY < this.nDivisionsY; cY++){
            for(int cX = 0; cX < this.nDivisionsX; cX++){
                int offsetX = cX * padWidth;
                int offsetY = cY * padHeight;
                this.dataSets.get(counter).drawOnCanvas(ig2, offsetX, offsetY, padWidth,padHeight);
                counter++;
            }
        } 
        try {
            FileOutputStream file = new FileOutputStream(filename);
            
            file.write(ig2.getBytes());        
            file.close();
        } catch(IOException e){
            
        }
    }
    
    public void save(String filename){
         try {
            /*
            System.err.println("*** SAVE *** : size --> ( "
                    + width + " x " + height + " ) File = " + file);*/
            byte[] imageBytes = this.getCanvasImage();
            FileOutputStream output = new FileOutputStream(new File(filename));
            output.write(imageBytes);
        } catch (IOException ex) {
            Logger.getLogger(TImageCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void save(String filename,int xsize, int ysize, int columns, int rows, List<DataSetPad>  pads){
        try {
            /*
            System.err.println("*** SAVE *** : size --> ( "
                    + width + " x " + height + " ) File = " + file);*/
            byte[] imageBytes = TImageCanvas.getCanvasImage(xsize, ysize, columns, rows, pads);
            FileOutputStream output = new FileOutputStream(new File(filename));
            output.write(imageBytes);
        } catch (IOException ex) {
            Logger.getLogger(TImageCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    public static byte[]  getCanvasImage(int xsize, int ysize, int columns, int rows, List<DataSetPad>  pads) throws IOException{
        BufferedImage bi = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = bi.createGraphics();
        ig2.setColor(Color.WHITE);
        ig2.fillRect(0, 0, xsize, ysize);
        
        int padWidth  = (int) xsize/columns;
        int padHeight = (int) ysize/rows;
        
        int counter = 0;
        for(int cY = 0; cY < rows; cY++){
            for(int cX = 0; cX < columns; cX++){
                int offsetX = cX * padWidth;
                int offsetY = cY * padHeight;
                pads.get(counter).drawOnCanvas(ig2, offsetX, offsetY, padWidth,padHeight);
                counter++;
            }
        }
        byte[] result = new byte[1];// = ImageIO.
        //return result;
        
        //ImageIO.write(bi, "png", new File("saveFile.png"));
        ByteArrayOutputStream biStream = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", biStream);
        biStream.flush();
        result = biStream.toByteArray();
        return result;
    }
    
        /**
     * Change Axis font size for all pads
     * @param size 
     */
    public void setAxisFontSize(int size){
        for(DataSetFrame pad : this.dataSets){
            pad.getAxisFrame().getAxisX().setAxisFontSize(size);
            pad.getAxisFrame().getAxisY().setAxisFontSize(size);
        }
    }
    /**
     * Change axis title string font size
     * @param size 
     */
    public void setAxisTitleFontSize(int size){
        for(DataSetFrame pad : this.dataSets){
            pad.getAxisFrame().getAxisX().setTitleSize(size);
            pad.getAxisFrame().getAxisY().setTitleSize(size);
        }
    }
    /**
     * Change pad title font size
     * @param size 
     */
    public void setTitleFontSize(int size){
        for(DataSetFrame pad : this.dataSets){
            pad.getAxisFrame().setTitleSize(size);
        }
    }
    /**
     * Change font size for start box fonts
     * @param size 
     */
    public void setStatBoxFontSize(int size){
        for(DataSetFrame pad : this.dataSets){
            pad.setStatBoxFontSize(size);
        }
     }
    
    public byte[] getCanvasImage() throws IOException {
        
        BufferedImage bi = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = bi.createGraphics();
        ig2.setColor(Color.WHITE);
        ig2.fillRect(0, 0, canvasWidth, canvasHeight);
        
        int padWidth  = (int) this.canvasWidth/this.nDivisionsX;
        int padHeight = (int) this.canvasHeight/this.nDivisionsY;
        
        int counter = 0;
        for(int cY = 0; cY < this.nDivisionsY; cY++){
            for(int cX = 0; cX < this.nDivisionsX; cX++){
                int offsetX = cX * padWidth;
                int offsetY = cY * padHeight;
                this.dataSets.get(counter).drawOnCanvas(ig2, offsetX, offsetY, padWidth,padHeight);
                counter++;
            }
        }
        byte[] result = new byte[1];// = ImageIO.
        //return result;
        
        //ImageIO.write(bi, "png", new File("saveFile.png"));
        ByteArrayOutputStream biStream = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", biStream);
        biStream.flush();
        result = biStream.toByteArray();
        return result;
    }
    
}
