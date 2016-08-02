/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.base;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

/**
 *
 * @author gavalian
 */
public class LatexText {
    
    private String  textFamily   = "Helvetica";
    private int     textFontSize = 14;
    private Double  relativeX  = 0.0;
    private Double  relativeY  = 0.0;
    private AttributedString  latexString = null;
    private String asciiString = "";
    private Integer           textColor   = 1;

    
    public LatexText(String text, double xc, double yc){
        this.setText(text);
        this.setLocation(xc, yc);
        this.setFont(textFamily);
        this.setFontSize(14);
    }
    
     public LatexText(String text){
        this.setText(text);
        this.setLocation(0.0,0.0);
        this.setFont(textFamily);
        this.setFontSize(14);
    }
     
    public final void setText(String text){
    	asciiString = text;
        String ltx  = LatexTextTools.convertUnicode(text);
        latexString = LatexTextTools.converSuperScript(ltx);
        this.setFont(this.textFamily);
        this.setFontSize(this.textFontSize);
    }
    
    public final String getTextString(){
        return asciiString;
    }
    
    public final void setLocation(double xr, double yr){
        this.relativeX = xr;
        this.relativeY = yr;
    }
    
    public int    getColor(){return this.textColor;}
    public void    setColor(int color){ this.textColor = color;}
    
    public double getX(){ return this.relativeX;}
    public double getY(){ return this.relativeY;}
    
    public AttributedString getText(){ return this.latexString;}
    
    public void setFont(String fontname){
        this.textFamily = fontname;
        if(this.latexString.getIterator().getEndIndex()>0){
        //System.out.println("INDEX = " + this.latexString.getIterator().getEndIndex());
            latexString.addAttribute(TextAttribute.FAMILY, fontname);
        }
    }
    
    public void setFontSize(int size){
        this.textFontSize = size;
        if(this.latexString.getIterator().getEndIndex()>0){
            latexString.addAttribute(TextAttribute.SIZE, (float) size);
        }
    }
    
    public  Rectangle2D getBounds( Graphics2D g2d){
        FontMetrics fmg = g2d.getFontMetrics(new Font(this.textFamily,Font.PLAIN,this.textFontSize));
        Rectangle2D rect = fmg.getStringBounds(this.latexString.getIterator(), 0,
                this.latexString.getIterator().getEndIndex(),g2d);
        return rect;
    }
    
    public  Rectangle2D getBounds(FontMetrics  fm, Graphics2D g2d){
        FontMetrics fmg = g2d.getFontMetrics(new Font(this.textFamily,Font.PLAIN,this.textFontSize));
        Rectangle2D rect = fmg.getStringBounds(this.latexString.getIterator(), 0,
                this.latexString.getIterator().getEndIndex(),g2d);
        return rect;
    }
}
