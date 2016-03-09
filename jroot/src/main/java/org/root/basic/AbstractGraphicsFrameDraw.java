/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.basic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import org.root.attr.ColorPalette;
import org.root.attr.MarkerPainter;
import org.root.base.DataRegion;

import org.root.base.IDataSet;
import org.root.basic.GraphicsAxisFrame;
import org.root.func.F1D;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;
import org.root.histogram.H2D;

/**
 *
 * @author gavalian
 */
public class AbstractGraphicsFrameDraw {
    
    public static float[]  dashPattern1 = new float[]{10.0f,5.0f};
    public static float[]  dashPattern2 = new float[]{10.0f,5.0f,2.0f,5.0f};
    public static float[]  dashPattern3 = new float[]{2.0f,5.0f,2.0f,5.0f};
    
    
    
    public static void drawOnCanvasGraph(Graphics2D g2d, GraphicsAxisFrame frame,
            IDataSet ds,
            int startX, int startY, int width, int height){
        
        MarkerPainter  mPainter = new MarkerPainter();
        int markerStyle = ds.getAttributes().getAsInt("marker-style");
        int markerColor = ds.getAttributes().getAsInt("marker-color");
        int markerSize  = ds.getAttributes().getAsInt("marker-size");
        int errorLineWidth  = ds.getAttributes().getAsInt("line-width");
        int errorLineColor  = ds.getAttributes().getAsInt("line-color");
        int markerLineWidth = ds.getAttributes().getAsInt("marker-width");
        int markerFillColor = ds.getAttributes().getAsInt("fill-color");
        
        /*
        g2d.setStroke(new BasicStroke(lineWidth));
        g2d.setColor(ColorPalette.getColor(lineColor));
        */
        //BasicStroke  errorStroke = new BasicStroke(errorLineWidth);
        //BasicStroke  errorStroke = new BasicStroke(1);
        BasicStroke  errorStroke = new BasicStroke(1);
        Color        lineColor   = ColorPalette.getColor(errorLineColor);
        int npoints = ds.getDataSize();
        
        for(int loop = 0; loop < npoints; loop++){
            double xp =  startX + frame.getFrameX(ds.getDataX(loop));
            double yp =  startY + frame.getFrameY(ds.getDataY(loop));
            
            //double ey = ds.getDataY(loop)-ds.getErrorY(loop);
            //System.out.println(" EY = " + ey);
            
            double ey_start = startY + frame.getFrameY(ds.getDataY(loop)-ds.getErrorY(loop));
            double ey_end   = startY + frame.getFrameY(ds.getDataY(loop)+ds.getErrorY(loop));
            
            g2d.setStroke(errorStroke);
            g2d.drawLine( (int) xp, (int) ey_start, (int) xp, (int) ey_end);
            
            double ex_start = startX + frame.getFrameX(ds.getDataX(loop)-ds.getErrorX(loop)*0.5);
            double ex_end   = startX + frame.getFrameX(ds.getDataX(loop)+ds.getErrorX(loop)*0.5);
            
            g2d.drawLine( (int) ex_start, (int) yp, (int) ex_end, (int) yp);
            
            mPainter.drawMarkerBasic(g2d, startX + (int) xp, startY + (int) yp, markerStyle,
                        markerSize,
                        markerLineWidth,markerColor,markerFillColor);
        }

    }
    
    public static void drawOnCanvasHistogram1D_EP(Graphics2D g2d, GraphicsAxisFrame frame,
            IDataSet ds,
            int startX, int startY, int width, int height){
        H1D h1 = (H1D) ds;
        
        GraphErrors graph = h1.getGraph();
        AbstractGraphicsFrameDraw.drawOnCanvasGraph(g2d, frame, graph, startX, startY, width, height);
    }
    
    public static void drawOnCanvasHistogram2D(Graphics2D g2d, GraphicsAxisFrame frame,
            IDataSet ds,
            int startX, int startY, int width, int height){
        int dataSizeX = ds.getDataSize(0);
        int dataSizeY = ds.getDataSize(1);
        DataRegion  region = ds.getDataRegion();
        double maxValue = region.MAXIMUM_Z - region.MINIMUM_Z;
        boolean isLog = frame.getAxisZ().isLog();
        H2D  h2d = (H2D) ds;
         //System.out.println("HISTOGRAM MAXIMUM = " + h2d.getMaximum()
         //+ " FRACTION X = " + fractionX + "  Y = " + fractionY);
         ColorPalette map = new ColorPalette();
         double fractionX   = ( (double) frame.getMargins().width)/dataSizeX;;
         double fractionY   = ((double) frame.getMargins().height)/( (double) dataSizeY);
         double xr = 0.0;
         double yr = 0.0;
         double wr = fractionX;         
         double hr = fractionY;
         for(int Lx = 0; Lx < dataSizeX; Lx++){
             for(int Ly = 0; Ly < dataSizeY; Ly++){
                 double xc = startX + frame.getMargins().x + xr;
                 //double yc = startY + axis.getFrame().y + yr;
                 double yc = startY + (frame.getMargins().y + frame.getMargins().height - yr - hr);                 
                 double value = ds.getData(Lx, Ly) - region.MINIMUM_Z;
                 Color  color = map.getColor3D(value, maxValue, isLog);                 
                 g2d.setColor(color);
                 g2d.fillRect( (int) xc, (int) yc, (int) wr+1, (int) hr+1);                
                 yr = yr + hr;
             }
             yr = 0.0;
             xr = xr + wr;
         }
    }
    public static void drawOnCanvasHistogram1D(Graphics2D g2d, GraphicsAxisFrame frame,
            IDataSet ds,
            int startX, int startY, int width, int height){
        int lineColor   = ds.getAttributes().getAsInt("line-color");
        int lineWidth   = ds.getAttributes().getAsInt("line-width");
        
        g2d.setStroke(new BasicStroke(lineWidth));
        g2d.setColor(ColorPalette.getColor(lineColor));
        
        int npoints = ds.getDataSize();
        GeneralPath path = new GeneralPath();
        
        double xp = startX + frame.getFrameX(ds.getDataX(0)-ds.getErrorX(0)*0.5);
        double yp = startY + frame.getFrameY(0.0);
        
        path.moveTo(xp, yp);
        
        for(int loop = 0; loop < npoints; loop++){
            xp =  startX + frame.getFrameX(ds.getDataX(loop)-ds.getErrorX(0)*0.5);
            yp =  startY + frame.getFrameY(ds.getDataY(loop));
            path.lineTo(xp, yp);
            xp =  startX + frame.getFrameX(ds.getDataX(loop)+ds.getErrorX(0)*0.5);
            path.lineTo(xp, yp);
        }
        xp =  startX + frame.getFrameX(ds.getDataX(npoints-1)+ds.getErrorX(0)*0.5);
        yp =  startY + frame.getFrameY(0.0);
        path.lineTo(xp, yp);
        
        int fillColor = ds.getAttributes().getAsInt("fill-color");
        if(fillColor>0){
            g2d.setColor(ColorPalette.getColor(fillColor));
            g2d.fill(path);
        }
        
        g2d.setColor(ColorPalette.getColor(lineColor));
        g2d.draw(path);
    }
    /**
     * Drawing a data set as a function.
     * @param g2d
     * @param frame
     * @param ds
     * @param startX
     * @param startY
     * @param width
     * @param height 
     */
    
    public static void drawOnCanvasFunction(Graphics2D g2d, GraphicsAxisFrame frame,
            IDataSet ds,
            int startX, int startY, int width, int height){
        int line_color = 1;
        int line_width = 1;
        int line_style = 1;
        if(ds.getClass().isAssignableFrom(F1D.class)==true){
            F1D func = (F1D) ds;
            line_color = func.getLineColor();
            line_width = func.getLineWidth();
            line_style = func.getLineStyle();
        }
        int npoints = ds.getDataSize();
        g2d.setColor(ColorPalette.getColor(line_color));
        GeneralPath  path = new GeneralPath();
        g2d.setStroke(new BasicStroke(line_width));
        if(line_style==2){
            g2d.setStroke(new BasicStroke(line_width, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 20.0f, dashPattern1, 0.0f));
        }
        
        if(line_style==3){
            g2d.setStroke(new BasicStroke(line_width, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 20.0f, dashPattern2, 0.0f));
        }
        
        if(line_style==4){
            g2d.setStroke(new BasicStroke(line_width, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 20.0f, dashPattern3, 0.0f));
        }
        
        for(int loop = 0; loop < npoints; loop++){
            
            double xp = startX + frame.getFrameX(ds.getDataX(loop));
            double yp = startY + frame.getFrameY(ds.getDataY(loop));
                //System.out.println(" POINT = " + loop + "  " + xr + "  " + yr
                //+ " " + xf + " " + yf);
                if(loop == 0){
                    path.moveTo( (int) xp, (int) yp);
                } else {
                    path.lineTo((int) xp, (int) yp);
                }

        }            
        g2d.draw(path);
    }
}
