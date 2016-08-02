/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import org.root.base.AxisRegionFrameTest;
import org.root.base.LatexText;

/**
 *
 * @author gavalian
 */
public class GraphicsAxis {
    
    private List<Double>  axisMarks       = new ArrayList<Double>();
    private List<String>  axisMarksString = new ArrayList<String>();
    private int           axisLength      = 100;
    private LatexText     axisTitle       = new LatexText("",0.5,0.0);
    private Font          axisFont        = new Font("Avenir",Font.PLAIN,10);
    private double        axisMinimum     = 0.0;
    private double        axisMaximum     = 1.0;
    private boolean       isAxisVertical  = false;
    private int           axisTickMarkLength = 5;
    private double        axisZoomedMin      = 0.0;
    private double        axisZoomedMax      = 1.0;
    private boolean       colorBand          = true;
    private boolean       isLogarithmic      = true;
    private boolean       isRangeFixed       = false;
    private String        axisFontName       = "Avenir";
    private int           axisFontSize       = 10;
    private String        axisTitleFontName  = "Avenir";
    private int           axisTitleFontSize  = 12;
        
    public GraphicsAxis(){
        
    }
    
    public void setLog(boolean flag){
        this.isLogarithmic = flag;
    }
    public void setVertical(boolean flag){
        this.isAxisVertical = flag;
    }
    
    public final void setLength(int len){
        this.axisLength = len;
    }
    
    public boolean rangeFixed(){
        return this.isRangeFixed;
    }
    
    public double getMin(){
        return this.axisMinimum;
    }
    
    public double getMax(){
        return this.axisMaximum;
    }
    
    public void rangeFixed(boolean flag){
        this.isRangeFixed = flag;
    }
    public void setMinMax(double min, double max){
        this.axisMinimum = min;
        this.axisMaximum = max;
        this.unZoom();
    }
    
    public final void zoom(int x, int y, int cxstart, int cxend){
        if(this.isAxisVertical==false){
            
        }
    }
    
    public final void setAxis(double[] values, String[] markers){
        if(values.length!=markers.length){
            return;
        }
        this.axisMarks.clear();
        this.axisMarksString.clear();
        for(int i = 0; i < values.length; i++){
            this.axisMarks.add(values[i]);
            this.axisMarksString.add(markers[i]);
        }
    }
    
    public void unZoom(){
        this.axisZoomedMin = this.axisMinimum;
        this.axisZoomedMax = this.axisMaximum;
    }
    
    public int  getAxisFontSize(){
        return this.axisFontSize;
    }
    
    public String getAxisFontName(){
        return this.axisFontName;
    }
    
    public String getAxisTitleFontName(){
        return this.axisTitleFontName;
    }
    
    public int getAxisTitleFontSize(){
        return this.axisTitleFontSize;
    }
    
    public void setAxisFontSize(int size){
        this.axisFontSize = size;
        //this.axisFont = new Font("Avenir",Font.BOLD,size);
        this.axisFont     = new Font(this.axisFontName,Font.PLAIN, this.axisFontSize);
    }
    
    public void setAxisFontName(String name){
        this.axisFontName = name;
        this.axisFont     = new Font(this.axisFontName,Font.PLAIN, this.axisFontSize);
    }
    
    public void setTitleFont(String fontname){        
        this.axisTitle.setFont(fontname);
    }
    
    public void setTitleFontName(String fontname){ 
        this.axisTitleFontName = fontname;
        this.axisTitle.setFont(fontname);
    }
    
    public void setTitleFontSize(int size){
        this.axisTitleFontSize = size;
        this.axisTitle.setFontSize(size);
    }
    
    public void setTitleSize(int size){
        this.axisTitle.setFontSize(size);
    }
    
    public String getTitle(){
        return this.axisTitle.toString();
    }
    
    public String getTitleString(){
        return this.axisTitle.getTextString();
    }
    
    
    public void setTitle(String title){
        this.axisTitle.setText(title);
    }
    public void show(){
        for(int i = 0; i < axisMarks.size(); i++){
            System.out.println(String.format("%4d : %9.4f %25s", i,
                    this.axisMarks.get(i),this.axisMarksString.get(i)));
        }
    }
    
    public double getPosition( double value){
        double range = (this.axisMaximum-this.axisMinimum);
        double pos   =  this.axisLength*(value-this.axisMinimum)/range;
        return pos;                        
    }
    /**
     * Draw the axis on the Canvas graphics object
     * @param g2d
     * @param x
     * @param y 
     */
    public void drawOnCanvas(Graphics2D g2d, int x, int y){        
        FontMetrics fm =  g2d.getFontMetrics(this.axisFont);
        g2d.setColor(Color.BLACK);
        g2d.setFont(axisFont);
        if(this.isAxisVertical==false){
            g2d.drawLine(x, y, x + this.axisLength, y);
            for(int loop = 0; loop < this.axisMarks.size(); loop++){
                double pos = this.getPosition( this.axisMarks.get(loop));
                g2d.drawLine((int) (x+pos), y-this.axisTickMarkLength, (int) (x+pos), y);
                int fw = fm.stringWidth(this.axisMarksString.get(loop));
                int fh = fm.getHeight();
                g2d.drawString(this.axisMarksString.get(loop), (int) (x+pos-fw*0.5), y + (int) (fh ));                
                //System.out.println("drawing tick mark on " + (int) pos);
            }
            int titleOffset = fm.getHeight();
            Rectangle2D  rect = this.axisTitle.getBounds(g2d);
            //this.axisTitle.setFontSize(24);
            g2d.drawString(this.axisTitle.getText().getIterator(), 
                    (int) (x + this.axisLength*0.5 - rect.getWidth()*0.5),
                    y + titleOffset + fm.getHeight());
        } else{
            
            int maxStringLength = 0;            
            g2d.drawLine(x, y, x, y-this.axisLength);
            for(int loop = 0; loop < this.axisMarks.size();loop++){
                double pos = this.getPosition(this.axisMarks.get(loop));
                g2d.drawLine(x, (int) (y-pos), x+this.axisTickMarkLength, (int) (y-pos));
                int fw = fm.stringWidth(this.axisMarksString.get(loop));
                int fh = fm.getHeight();
                int fww = (int) (fw - fh*0.2);
                if(fww> maxStringLength){
                    maxStringLength = fww;
                }
                g2d.drawString(this.axisMarksString.get(loop), (int) (x - fw - fh*0.2), (int) (y-pos+fh*0.35));
            }
            AffineTransform orig = g2d.getTransform();
//            g2d.rotate(-Math.PI/2);
            g2d.rotate(-Math.PI/2);
            //int  yoffset = 
            Rectangle2D  rect = this.axisTitle.getBounds( g2d);
            int xt = (int) -(y-this.axisLength*0.5+rect.getWidth()*0.5);
            
            //System.out.println("Drawing Y - title ");            
            g2d.drawString(this.axisTitle.getText().getIterator(), xt, (int) (x - maxStringLength - fm.getHeight()));
            g2d.setTransform(orig);
        }
    }
    
    
    public void update(Graphics2D  g2d, int width, int height){
        
    }
    
    public boolean isVertical(){
        return this.isAxisVertical;
    }
    
    public boolean isLog(){
        return this.isLogarithmic;
    }
    
    public int  getLength(){
        return this.axisLength;
    }
    
    public Font getAxisFont(){
        return this.axisFont;
    }
    
    public int getMargin(Graphics2D g2d){
        int margin = 0;
        if(this.isAxisVertical==false){
            FontMetrics  fma = g2d.getFontMetrics(this.axisFont); 
            Rectangle2D  region = this.axisTitle.getBounds(g2d);
            return (int) (fma.getHeight()*1.4 + region.getHeight()*1.5);
        } else {
            FontMetrics  fma = g2d.getFontMetrics(this.axisFont); 
            Rectangle2D  region = this.axisTitle.getBounds(g2d);
            double maxL = 0.0;
            //System.out.println(" AXIS Strings SIZE = " + this.axisMarksString.size() + "  " + this.axisMarks.size());
            for(String str : this.axisMarksString){
                if(fma.stringWidth(str)>maxL){
                    maxL = fma.stringWidth(str);
                    //System.out.println(" STRING [" + str + "]  size = " + fma.stringWidth(str) );
                }
                //System.out.println("MAXL = " + maxL + "  region Height = " + region.getHeight());

            }
            return (int) (maxL*1.4 + region.getHeight()*1.5);
        }        
    }
    
    public List<String>  getAxisMarksString(){
        return this.axisMarksString;
    }
    
    public List<Double>  getAxisMarks(){
        return this.axisMarks;
    }
            
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        NewGraphicsTest panel = new NewGraphicsTest();
        frame.add(panel,BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
