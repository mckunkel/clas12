/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.root.attr.TStyle;


/**
 *
 * @author gavalian
 */
public class PaveText {
    
    private ArrayList<LatexText>  latexTexts = new ArrayList<LatexText>();
    
    private int       borderSizeX  = 5;
    private int       borderSizeY  = 5;
    private Color     backgroundColor = new Color(255,255,255,250);
    private Color     borderColor = new Color(0,0,0);
    
    public PaveText(){
        
    }
    
    public void addText(String text){
        LatexText lt = new LatexText(text);
        this.latexTexts.add(lt);
    }
    
    public void setFont(String name, int size){
        for(LatexText t : this.latexTexts){
            t.setFont(name);
            t.setFontSize(size);
        }
    }
    
    public Rectangle2D  getBounds(FontMetrics  fm, Graphics2D g2d){
        Rectangle2D bounds = this.latexTexts.get(0).getBounds(fm, g2d);
        int counter = 0;
        double  maxWidth  = 0.0;
        double  maxHeight = 0.0;
        
        for(LatexText text : this.latexTexts){
            Rectangle2D r = text.getBounds(fm, g2d);
            if(r.getWidth()>maxWidth) maxWidth = r.getWidth();
            maxHeight += r.getHeight();//*TStyle.getStatBoxTextGap();
            //if(r.getHeight()>maxHeight) maxHeight = r.getHeight();
            //System.out.println("STRING TEXT # " + counter + "  " + r.getWidth() + "  " + r.getHeight());
            //counter++;
            //r            
        }
        bounds.setRect(0, 0, maxWidth, maxHeight);
        return bounds;
    }
    public Rectangle2D  getBounds( Graphics2D g2d){
        Rectangle2D bounds = this.latexTexts.get(0).getBounds( g2d);
        int counter = 0;
        double  maxWidth  = 0.0;
        double  maxHeight = 0.0;
        
        for(LatexText text : this.latexTexts){
            Rectangle2D r = text.getBounds( g2d);
            if(r.getWidth()>maxWidth) maxWidth = r.getWidth();
            maxHeight += r.getHeight();//*TStyle.getStatBoxTextGap();
            //if(r.getHeight()>maxHeight) maxHeight = r.getHeight();
            //System.out.println("STRING TEXT # " + counter + "  " + r.getWidth() + "  " + r.getHeight());
            //counter++;
            //r            
        }
        bounds.setRect(0, 0, maxWidth + this.borderSizeX*2, maxHeight+this.borderSizeY*2);
        return bounds;
    }
    public void clear(){
        this.latexTexts.clear();
    }
    
    public List<LatexText>  getTexts(){
        return this.latexTexts;
    }
    
    
    public void drawOnCanvas(Graphics2D g2d,int x, int y, int adjustment){
        Rectangle2D  rect = this.getBounds(g2d);
        
        int startX = x;
        int startY = y;
        if(adjustment==1){
            startX -= (int) (rect.getWidth()*0.5);
        }
        if(adjustment==2){
            startX -= (int) rect.getWidth();
        }
        //System.out.println(" [PAVE TEXT] W/H " + rect.getWidth() + " " + rect.getHeight());
        g2d.setColor(this.backgroundColor);
        g2d.fillRect(startX, startY, (int) rect.getWidth(),(int) rect.getHeight());

        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(this.borderColor);
        
        g2d.drawRect(startX, startY, (int) rect.getWidth(),(int) rect.getHeight());
        int offset = 0;
        for(int i = 0; i < this.latexTexts.size(); i++){
            Rectangle2D bound = this.latexTexts.get(i).getBounds(g2d);
            offset += bound.getHeight();
            g2d.drawString(this.latexTexts.get(i).getText().getIterator(), 
                    startX + this.borderSizeX, startY + this.borderSizeY + offset);
            
        }
        int startPoint = y;
        
    }
}
