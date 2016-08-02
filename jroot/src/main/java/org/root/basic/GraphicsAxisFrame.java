/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.basic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import static org.root.base.AxisRegion.dashPattern1;
import org.root.base.LatexText;

/**
 *
 * @author gavalian
 */
public class GraphicsAxisFrame {
    
    private GraphicsAxis  axisX = new GraphicsAxisNumber(0.0,5.0);
    private GraphicsAxis  axisY = new GraphicsAxisNumber(450.0,500.0);
    private GraphicsAxis  axisZ = new GraphicsAxisNumber(0.0,1.0);
    
    private Rectangle     frameMargins = new Rectangle();
    private LatexText     frameTitle   = new LatexText("");
    private Boolean       axisGridX    = true;
    private Boolean       axisGridY    = true;
    private String        frameTitltFontName = "Avenir";
    private int           frameTitleFontSize = 12;
    public static float[]  dashPattern1 = new float[]{3.0f,3.0f};
    public static float[]  dashPattern2 = new float[]{10.0f,5.0f,2.0f,5.0f};
    public static float[]  dashPattern3 = new float[]{2.0f,5.0f,2.0f,5.0f};
    
    public GraphicsAxisFrame(){
        this.axisY.setVertical(true);
        this.axisX.setTitleSize(12);
        this.axisX.setAxisFontSize(12);
        this.axisY.setAxisFontSize(12);
        //this.axisY.setTitleSize(14);
        //this.frameTitle.setFont("Avenir");
        //this.frameTitle.setFontSize(18);
    }
    
    public GraphicsAxis  getAxisX(){
        return this.axisX;
    }
    
    public GraphicsAxis  getAxisY(){
        return this.axisY;
    }
    
    public GraphicsAxis getAxisZ(){
        return this.axisZ;
    }
    
    public Rectangle  getMargins(){
        return this.frameMargins;
    }
    
    public boolean getGridX(){
        return this.axisGridX;
    }
    
    public boolean getGridY(){
        return this.axisGridY;
    }
    
    public void setGridX(boolean flag){
        this.axisGridX = flag;
    }
    
    public void setGridY(boolean flag){
        this.axisGridY = flag;
    }
    
    public void updateFrameMargins(Graphics2D  g2d, int width, int height){
        int xmargin = axisX.getMargin(g2d);
        int ymargin = axisY.getMargin(g2d);
        
        Rectangle2D region = this.frameTitle.getBounds(g2d);
        //System.out.println(" X margin = " + ymargin);
        frameMargins.x = (int) (ymargin);
        frameMargins.y = (int) (region.getHeight()*1.5);
        frameMargins.width  = width - frameMargins.x - 15;
        frameMargins.height = height - xmargin - frameMargins.y;
        //System.out.println("TITLE SIZE = " + region.getHeight());
    }
    
    public int  getFrameX(double value){
        int position = (int) this.axisX.getPosition(value);
        return this.frameMargins.x + position;
    }
    
    public int  getFrameY(double value){
        int position = (int) this.axisY.getPosition(value);
        return this.frameMargins.y + this.frameMargins.height - position;
    }
    
    public void drawOnCanvas(Graphics2D g2d, int startX, int startY, int width, int height){
        
        g2d.setColor(Color.white);
        g2d.fillRect(startX, startY, width, height);
        
        this.updateFrameMargins(g2d, width, height);
        this.axisX.setLength(this.frameMargins.width);
        this.axisY.setLength(this.frameMargins.height);
        
        this.axisX.update(g2d, width, height);
        this.axisY.update(g2d, width, height);

        this.axisX.setLength(frameMargins.width);
        this.axisY.setLength(frameMargins.height);
        if(this.axisGridX==true)
            this.drawOnCanvasGirdX(g2d, startX, startY,width, height);
        if(this.axisGridY==true)
            this.drawOnCanvasGirdY(g2d, startX , startY,width, height);
        
        g2d.setColor(Color.BLACK);
        g2d.drawRect(startX + this.frameMargins.x, startY + frameMargins.y, 
                frameMargins.width,frameMargins.height);    
        
        this.axisX.drawOnCanvas(g2d, startX + frameMargins.x, startY + frameMargins.height + frameMargins.y );
        this.axisY.drawOnCanvas(g2d, startX + frameMargins.x, startY + frameMargins.height + frameMargins.y);

        Rectangle2D region = this.frameTitle.getBounds(g2d);
        //g2d.setFont(new Font("Avenir",Font.PLAIN,24));
        g2d.drawString(
                this.frameTitle.getText().getIterator(), 
                startX + (int) (this.frameMargins.x + this.frameMargins.width*0.5 - region.getWidth()*0.5)
                , startY + this.frameMargins.y - 5);
    }
    
    public void drawOnCanvasGirdX(Graphics2D g2d, int startX, int startY, int width, int height){
        BasicStroke   gridStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 20.0f, dashPattern1, 0.0f);
        g2d.setStroke(gridStroke);
        g2d.setColor(new Color(180,180,180));
        for(Double xl : this.axisX.getAxisMarks()){
            double xp = this.getFrameX(xl);
            g2d.drawLine(startX + (int) xp, startY + frameMargins.y,
                    startX + (int) xp, startY + frameMargins.height+frameMargins.y);
        }
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
    }
    
    public void drawOnCanvasGirdY(Graphics2D g2d,int startX, int startY,int width, int height){
        BasicStroke   gridStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 20.0f, dashPattern1, 0.0f);
        g2d.setStroke(gridStroke);
        g2d.setColor(new Color(180,180,180));
        for(Double yl : this.axisY.getAxisMarks()){
            double yp = this.getFrameY(yl);
            g2d.drawLine(startX + frameMargins.x,
                    startY + (int) yp, 
                    startX +frameMargins.width+frameMargins.x,startY + (int) yp);
        }
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
    }
    
    public void setTitle(String title){
        this.frameTitle.setText(title);
    }
    
    public String getTitle(){
        return this.frameTitle.getTextString();
    }
    
    public void setTitleFontName(String font){
        this.frameTitltFontName = font;
        this.frameTitle.setFont(font);
    }
    
    public void setTitleFontSize(int size){
        this.frameTitleFontSize = size;
        this.frameTitle.setFontSize(size);
    }
    
    public int getTitleFontSize(){
        return this.frameTitleFontSize;
    }
    
    public String getTitleFontName(){
        return this.frameTitltFontName;
    }
}
