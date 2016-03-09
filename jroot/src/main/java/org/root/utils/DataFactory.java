/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.root.utils;

import org.root.data.DataSetXY;
import org.root.func.F1D;
import org.root.func.RandomFunc;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;
import org.root.histogram.H2D;

/**
 *
 * @author gavalian
 */
public class DataFactory {
    
   public static void createSampleH1D(H1D h1, int count, double peak){
       double min = h1.getXaxis().min();
       double max = h1.getXaxis().max();
       F1D f1 = new F1D("gaus+p2",min,max);
       double mean  = min + (max-min)/2.0;
       mean = peak;
       double sigma = (max-min)*0.05;
       h1.setXTitle("M^x (K^+) [MeV]");
       h1.setYTitle("Counts");
       h1.setTitle("H(e #rarrow epK^+)");
       //System.out.println("MEAN/SIGMA = " + mean + " " + sigma);
       f1.setParameter(0,  220.0);
       f1.setParameter(1,  mean);
       f1.setParameter(2,  sigma);
       f1.setParameter(3,    5.0);
       f1.setParameter(4,    0.2);
       
       RandomFunc rndm = new RandomFunc(f1);

       for(int i = 0; i < count; i++){
           h1.fill(rndm.random());
       }
   }
   
   public static void createSampleH2D(H2D h2, int count){
       
       double minX = h2.getXAxis().min();
       double maxX = h2.getXAxis().max();
       
       F1D f1x = new F1D("gaus+p2",minX,maxX);
       double mean  = minX + (maxX-minX)/2.0;
       double sigma = (maxX-minX)*0.1;
       
       //System.out.println("MEAN/SIGMA = " + mean + " " + sigma);
       f1x.setParameter(0,  120.0);
       f1x.setParameter(1,  mean);
       f1x.setParameter(2,  sigma);
       f1x.setParameter(3,  24.0);
       f1x.setParameter(4,  7.0);
       RandomFunc rndmX = new RandomFunc(f1x);
       
       double minY = h2.getYAxis().min();
       double maxY = h2.getYAxis().max();
       
       F1D f1y = new F1D("gaus+p2",minY,maxY);
       
       f1y.setParameter(0,  120.0);
       f1y.setParameter(1,  minY + (maxY-minY)/2.0);
       f1y.setParameter(2,  (maxY-minY)*0.1);
       f1y.setParameter(3,  24.0);
       f1y.setParameter(4,  7.0);
       RandomFunc rndmY = new RandomFunc(f1y);
       
       for(int loop = 0; loop < count; loop++){
           h2.fill(rndmX.random(), rndmY.random());
       }
   }
   
   public static void createGraphSin(GraphErrors gr, int npoints, double min, double max){
       for(int loop = 0; loop < npoints; loop++){
           double x = min + loop*(max-min)/npoints;
           double y = 25.0*Math.sin(x);
           gr.add(x, y);
       }
   }
   
   public static void createGraphCos(GraphErrors gr, int npoints, double min, double max){
       for(int loop = 0; loop < npoints; loop++){
           double x = min + loop*(max-min)/npoints;
           double y = Math.cos(x);
           gr.add(x, y);
       }
   }
   
}
