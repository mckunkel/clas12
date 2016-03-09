/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.pad;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.root.attr.TStyle;
import org.root.base.AbsDataSetDraw;
import org.root.base.AxisRegion;
import org.root.base.DataRegion;
import org.root.base.DataSetCollection;
import org.root.base.DataSetPad;
import org.root.base.IDataSet;
import org.root.base.LatexText;
import org.root.basic.DataSetFrame;
import org.root.data.DataSetXY;
import org.root.func.F1D;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;
import org.root.utils.DataFactory;

/**
 *
 * @author gavalian
 */
public class EmbeddedPad extends JPanel implements MouseMotionListener, MouseListener {
    
    private  final DataSetPad   dPad = new DataSetPad();
    private  final DataSetFrame dFramePad = new DataSetFrame();
    private  int dragMove_startX   = 0;
    private  int dragMove_startY   = 0;
    
    public EmbeddedPad(){
        super();
        this.setPreferredSize(new Dimension(500,500));
        this.getPad().setStatBoxFontSize(12);
        this.getPad().setAxisFontSize(12);
        this.getPad().setTitleFontSize(12);
    }
    
    public EmbeddedPad(int xsize, int ysize){
        super();
        this.setPreferredSize(new Dimension(xsize,ysize));
        this.getPad().setStatBoxFontSize(12);
        this.getPad().setAxisFontSize(12);
        this.getPad().setTitleFontSize(12);
    }
    
    
    public final DataSetPad getPad(){ return this.dPad;}
    
    public void drawOnCanvas(Graphics2D g2d, int xoffset, int yoffset, int w, int h){
        dPad.drawOnCanvas(g2d, xoffset, yoffset, w, h);
        //this.dFramePad.drawOnCanvas(g2d, xoffset, yoffset, w, h);
    }
    
    public void add(IDataSet ds,String option){
        this.dPad.add(ds, option);
        //this.dFramePad.add(ds, option);
    }
    
    public void add(IDataSet ds){
        this.dPad.add(ds);
        //this.dFramePad.add(ds);
    }
    
    public void addText(LatexText txt){
        this.dPad.addText(txt);
    }
    
    public void setAutoScale(Boolean flag){
        this.dPad.setAutoScale(flag);
    }
    
    public void setAxisRange(double xmin, double xmax, double ymin, double ymax){
        this.dPad.setAxisRange(xmin, xmax, ymin, ymax);
    }
    
    public void setDivisionsX(int div){
        this.dPad.setDivisionsX(div);
    }
     public void setDivisionsY(int div){
        this.dPad.setDivisionsY(div);
    }
     
    @Override
    public void paint(Graphics g){        
        Graphics2D g2d = (Graphics2D) g;
        
        int w = this.getSize().width;
        int h = this.getSize().height;
        this.drawOnCanvas(g2d, 0,0, w, h);
        g2d.setStroke(new BasicStroke(1));
        /*
        for(int loop = 0; loop < 120000; loop++){
            int rX = (int) (100+Math.random()*300);
            int rY = (int) (100+Math.random()*300);
            g2d.drawLine(rX,rY,rX,rY);
        }*/
    }
    
    public void setLog(String axis, boolean flag){
        if(axis.compareTo("X")==0){
            this.dPad.setLogX(flag);
            return;
        }
        
        if(axis.compareTo("Y")==0){
            this.dPad.setLogY(flag);
            return;
        }
       
        if(axis.compareTo("Z")==0){
            this.dFramePad.getAxisFrame().getAxisZ().setLog(flag);
        }
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        
        double[] x = new double[]{1.0,2.0,3.0,4.0,5.0};
        double[] y = new double[]{0.5,0.2,0.1,0.45,0.7};
        double[] yn = new double[]{0.9,1.2,1.6,2.45,1.7};
        
        DataSetXY  dsXY = new DataSetXY("data",x,y);
        dsXY.setMarkerStyle(2);
        dsXY.setMarkerColor(3);
        dsXY.setMarkerSize(10);
        
        DataSetXY  dsXY2 = new DataSetXY("data",x,yn);
        dsXY2.setMarkerStyle(2);
        dsXY2.setMarkerColor(4);
        dsXY2.setMarkerSize(10);
        
        H1D  h1 = new H1D("h1",50,0.0,2.0);
        H1D  h2 = new H1D("h2",50,0.0,2.0);
        
        H1D  h3 = new H1D("h3",120,0.0,550.0);
        H1D  h4 = new H1D("h4",120,0.0,550.0);
        F1D  f3 = new F1D("gaus+p2",0.0,550.0);
        
        DataFactory.createSampleH1D(h3, 30000,220);
        DataFactory.createSampleH1D(h4, 15000,350);
        GraphErrors gr = new GraphErrors();
        
        DataFactory.createGraphCos(gr, 25,0.0,6.28);
        
        for(int loop = 0; loop < 5000; loop++){
            h1.fill(Math.random()*2.0);
        }
        
        for(int loop = 0; loop < 3000; loop++){
            h2.fill(Math.random()*2.0);
        }
        

        
        h1.setLineColor(2);
        h1.setLineWidth(3);
        h2.setLineColor(4);
        h2.setLineWidth(3);
        
        //h1.setFillColor(3);

        //h2.setFillColor(9);
        f3.setParameter(0, 200);
        f3.setParameter(1, 120.0);
        f3.setParameter(2, 10);
        
        GraphErrors sinGraph = new GraphErrors();
        
        EmbeddedPad pad  = new EmbeddedPad(600,400);
        pad.getPad().setAxisFontSize(18);
        pad.getPad().setStatBoxFontSize(14);
        pad.getPad().setTitleFontSize(14);
        h3.setLineColor(1);
        h3.setFillColor(2);
        h4.setFillColor(33);
        
        h3.setLineWidth(2);
        h3.fit(f3);
        System.out.println(gr);
        pad.add(h3);
        pad.add(h4);
        //pad.add(f3);
        gr.setMarkerColor(2);
        gr.setMarkerStyle(2);
        gr.setMarkerSize(12);
        //pad.add(gr);
        //pad.add(h1);
        //pad.add(h2);
        //pad.setLog("Y", true);
        LatexText tex = new LatexText("M^x(ep#rarrow e^'p #phi #eps #pi^+ #pi^- (#gamma) (e^#uarrow^#darrow) )",0.05,0.1);
        tex.setColor(1);
        tex.setFontSize(24);
        pad.addText(tex);
        //TStyle.setStatBoxFont("Helvetica", 24);
        //pad.add(dsXY);
        //pad.add(dsXY2);
        pad.setAutoScale(Boolean.TRUE);
        frame.setLayout(new GridLayout(1,3));
        frame.setSize(800, 600);
        frame.add(pad);
        frame.pack();
        frame.setVisible(true);
    }

    public void mouseDragged(MouseEvent e) {
        int  dx = e.getX();
        int  dy = e.getY();
        System.out.println("MOUSE Drugged from " + this.dragMove_startX
        + " " + this.dragMove_startY + " " + dx + " " + dy);
    }

    public void mouseMoved(MouseEvent e) {
        
    }

    public void mouseClicked(MouseEvent e) {
        this.dragMove_startX = e.getX();
        this.dragMove_startY = e.getY();
        System.out.println(" MOUSE CLICKED " + this.dragMove_startX + "  " + this.dragMove_startY);
    }

    public void mousePressed(MouseEvent e) {
         this.dragMove_startX = e.getX();
        this.dragMove_startY = e.getY();
        System.out.println(" MOUSE PRESSED " + this.dragMove_startX + "  " + this.dragMove_startY);
    }

    public void mouseReleased(MouseEvent e) {
        System.out.println(" MOUSE RELEASED ");
    }

    public void mouseEntered(MouseEvent e) {
        
    }

    public void mouseExited(MouseEvent e) {
        
    }

    
}
