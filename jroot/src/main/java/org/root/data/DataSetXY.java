/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.root.data;

import java.util.Map;
import java.util.TreeMap;
import org.root.attr.Attributes;
import org.root.base.DataRegion;
import org.root.base.EvioWritableTree;
import org.root.base.IDataSet;

/**
 *
 * @author gavalian
 */
public class DataSetXY implements EvioWritableTree,IDataSet {
    
    private final DataVector dataX = new DataVector();
    private final DataVector dataY = new DataVector();
    private final DataVector dataEX = new DataVector();
    private final DataVector dataEY = new DataVector();
    
    private String dataSetName = "dataSetXY";
    private String dataTitle  = "";
    private String dataXtitle = "";
    private String dataYtitle = "";
    private Attributes  attr = new Attributes();
    
    public DataSetXY(){
        this.initAttributes();
        //this.attr.addFillProperties();
        //this.attr.addLineProperties();
        //this.attr.addMarkerAttributes();
    }
    
    public DataSetXY(String name){
        this.dataSetName = name;
        this.initAttributes();
        /*
        this.attr.addFillProperties();
        this.attr.addLineProperties();
        this.attr.addMarkerAttributes();
                */
    }
    
    public void setName(String name){
        this.dataSetName = name;
    }
    
    public String getName(){ return this.dataSetName;}
    
    
    private void initAttributes(){        
        this.attr.getProperties().setProperty("line-color"   , "1");
        this.attr.getProperties().setProperty("line-width"   , "1");
        this.attr.getProperties().setProperty("line-style"   , "1");
        this.attr.getProperties().setProperty("marker-color" , "1");
        this.attr.getProperties().setProperty("marker-size"  , "12");
        this.attr.getProperties().setProperty("marker-style" , "1");
        this.attr.getProperties().setProperty("marker-width" , "2");
        this.attr.getProperties().setProperty("fill-color"   , "0");
        this.attr.getProperties().setProperty("title"   , "Graph Errors");
        this.attr.getProperties().setProperty("xtitle"   , "x");
        this.attr.getProperties().setProperty("ytitle"   , "y");

    }
    
    public DataSetXY(String name, DataVector x, DataVector y){
        this.dataSetName = name;
        dataX.copy(x);
        dataY.copy(y);
        this.initAttributes();
    }
    
    public DataSetXY(String name, double[] x, double[] y){
        this.dataSetName = name;
        dataX.set(x);
        dataY.set(y);
        dataEX.set(new double[x.length]);
        dataEY.set(new double[y.length]);
        this.initAttributes();
    }
    
    public DataSetXY(String name, double[] x, double[] y,double[] ex, double[] ey){
        this.dataSetName = name;
        dataX.set(x);
        dataY.set(y);
        dataEX.set(ex);
        dataEY.set(ey);
        this.initAttributes();
    }
    
    public DataSetXY(double[] x, double[] y){
        dataX.set(x);
        dataY.set(y);
        dataEX.set(new double[x.length]);
        dataEY.set(new double[y.length]);
        this.initAttributes();
    }
    
    public DataSetXY(double[] x, double[] y, double[] ex, double[] ey){
        dataX.set(x);
        dataY.set(y);
        dataEX.set(ex);
        dataEY.set(ey);
        this.initAttributes();

    }
    
    public void setTiles(String t, String xt, String yt){
        this.dataTitle = t;
        this.dataXtitle = xt;
        this.dataYtitle = yt;
    }
    
    public String getTitle(){ return this.attr.getProperties().getProperty("title");}
    public String getXTitle(){ return this.attr.getProperties().getProperty("xtitle");}
    public String getYTitle(){ return this.attr.getProperties().getProperty("ytitle");}
    public void setTitle(String t){ this.attr.getProperties().put("title", t);}
    public void setXTitle(String t){ this.attr.getProperties().put("xtitle", t);}
    public void setYTitle(String t){ this.attr.getProperties().put("ytitle", t);}
    
    public void add(double x, double y){
        this.add(x, y,0.0,0.0);
    }
    
    public void add(double x, double y, double ex, double ey){
        if(Double.isNaN(x)!=true&&Double.isNaN(y)!=true&&
                x!=Double.NEGATIVE_INFINITY&&y!=Double.NEGATIVE_INFINITY
                &&x!=Double.POSITIVE_INFINITY&&y!=Double.POSITIVE_INFINITY){
            dataX.add(x);
            dataY.add(y);
            dataEX.add(ex);
            dataEY.add(ey);
        } else {
            System.err.println("[DataSetXY::add] ERROR : can not add point "
            + x + " " + y);
        }
    }
    
    public DataVector getDataX(){
        return dataX;
    }
    
    public DataVector getDataY(){
        return dataY;
    }    
    
    public DataSetXY  getDataSetRieman(){
        DataVector vecY = dataY.getCumulative();
        vecY.divide(vecY.getValue(vecY.getSize()-1));
        DataSetXY data = new DataSetXY(dataX.getArray(),vecY.getArray());
        return data;
    }
    
    public DataSetXY  getDataSetRieman(int nsamples){        
        
        DataSetXY rieman = this.getDataSetRieman();
        DataSetXY dataSet = new DataSetXY();
        
        double step = 1.0/nsamples;
        for(int loop = 0; loop < nsamples; loop++){
            double  yn    = loop * step;
            int     bin   = rieman.getDataY().findBin(yn);
            
            //if(bin<0) continue;
            double  ydiff = yn - rieman.getDataY().getValue(bin);
            double  slope = rieman.getBinSlope(bin);
            double  xoffset = ydiff/slope;
            double  xv      = rieman.getDataX().getValue(bin)+xoffset;
            double  yv      = this.evaluate(xv);
            //dataSet.getDataX().add(xv);
            //dataSet.getDataY().add(yv);
            dataSet.add(xv, yv);
            //System.err.println("LOOP " + loop + " " + yn + " " + bin 
            //        + "  " + xv + " " + yv);
        }
        return dataSet;
    }
    
    public double getBinSlope(int bin){
        if(bin>=dataX.getSize()||bin<0) return 0.0;
        double xdiff = dataX.getValue(bin+1) - dataX.getValue(bin);
        double ydiff = dataY.getValue(bin+1) - dataY.getValue(bin);
        if(xdiff!=0) return ydiff/xdiff;
        return 0.0;
    }
    
    public double getIntersectY(double yvalue){
        int bin = dataY.findBin(yvalue);
        //System.out.println(" y value = " + yvalue + "  bin = " + bin);
        if(bin==dataX.getSize()-1) return dataX.getValue(dataX.getSize()-1);
        double y0   = dataY.getValue(bin);
        double y1   = dataY.getValue(bin+1);
        double x0   = dataX.getValue(bin);
        double x1   = dataX.getValue(bin+1);
        double ydiff = (y1-y0);
        if(ydiff==0) return x0;
        double xvalue = (x1-x0)*(yvalue-y0)/(ydiff)+x0;
        //System.out.println("\t\t -> x value = " + xvalue
        // + "  x0 = " + x0);
        return xvalue;
    }
    
    public double evaluate(double x){
        int      bin  = dataX.findBin(x);
        double slope  = this.getBinSlope(bin);
        double xdiff  = x - dataX.getValue(bin);
        double y0     = dataY.getValue(bin);
        double yoffset = xdiff*slope;
        return (y0+yoffset);
    }
    
    public double getMean(){
        double mean  = 0.0;
        double count = 0;
        for(int loop = 0; loop < dataX.getSize(); loop++){
            mean += dataY.getValue(loop)*dataX.getValue(loop);
            if(dataY.getValue(loop)!=0) count += dataY.getValue(loop);
        }
        if(count!=0) mean = mean/count;
        return mean;
    }
    
    public double getRMS(){
        double mean  = this.getMean();
        double rms   = 0.0;
        double count = 0;
        for(int loop = 0; loop < dataX.getSize(); loop++){
            rms += dataY.getValue(loop)*(dataX.getValue(loop)-mean)
                    *(dataX.getValue(loop)-mean);
            if(dataY.getValue(loop)!=0) count += dataY.getValue(loop);
        }
        if(count!=0) rms = Math.sqrt(rms/count);
        
        return rms;
    }
    
    public void show(){
        System.err.println("****** DATA SET *****  LENGTH = " 
                + dataX.getSize() + "  ******");
        for(int loop = 0; loop < dataX.getSize(); loop++){
            System.err.printf("\t%12.5f \t%12.5f \t%12.5f \t%12.5f\n",dataX.getValue(loop),dataY.getValue(loop),
                   dataEX.getValue(loop),dataEY.getValue(loop));
        }
    }

    @Override
    public Map<Integer, Object> toTreeMap() {
        TreeMap<Integer, Object> hcontainer = new TreeMap<Integer, Object>();
        hcontainer.put(1, new int[]{6});
        byte[] nameBytes = this.dataSetName.getBytes();
        hcontainer.put(2, nameBytes);
        //hcontainer.put(2, new int[]{this.getxAxis().getNBins()});
        //hcontainer.put(3, new double[]{this.getxAxis().min(),this.getxAxis().max()});
        hcontainer.put(4, this.dataX.getArray());
        hcontainer.put(5, this.dataY.getArray());
        hcontainer.put(6, this.dataEX.getArray());
        hcontainer.put(7, this.dataEY.getArray());
        //byte[] nameBytes = this.dataSetName.getBytes();
        //hcontainer.put(6, nameBytes);
        return hcontainer;
    }

    public void fromTreeMap(Map<Integer, Object> map) {
        if(map.get(1) instanceof int[]){
            if(  ((int[]) map.get(1))[0]==6){
                double[]  xdata    = ((double[]) map.get(4));
                double[]  ydata    = ((double[]) map.get(5));
                double[]  exdata    = ((double[]) map.get(6));
                double[]  eydata    = ((double[]) map.get(7));
                byte[] name     = (byte[]) map.get(2);
                this.dataSetName = new String(name);                
                this.dataX.set(xdata);
                this.dataY.set(ydata);
                this.dataEX.set(exdata);
                this.dataEY.set(eydata);
            }
        }
    }
    
    
    public void setLineWidth(Integer width){

        this.attr.getProperties().setProperty("line-width", width.toString());
        //System.out.println("SET LINE WIDTH = " + this.attr.getProperties().getProperty("line-width"));
    }
    
    public void setLineColor(Integer color){
        this.attr.getProperties().setProperty("line-color", color.toString());
    }
    
    public void setLineStyle(Integer style){
        this.attr.getProperties().setProperty("line-style", style.toString());
    }
    
    public int getLineWidth(){
        //System.out.println("LINE WIDTH = " + this.attr.getProperties().getProperty("line-width"));
        return Integer.parseInt(this.attr.getProperties().getProperty("line-width"));
    }
    
    public int getLineColor(){
        return Integer.parseInt(this.attr.getProperties().getProperty("line-color"));
    }
    
    public int getLineStyle(){
        return Integer.parseInt(this.attr.getProperties().getProperty("line-style"));
    }
    
    public void setMarkerStyle(Integer style){
        this.attr.getProperties().setProperty("marker-style", style.toString());
    }
    
    public void setMarkerColor(Integer color){
        this.attr.getProperties().setProperty("marker-color", color.toString());
    }
    
    public void setMarkerSize(Integer size){
        this.attr.getProperties().setProperty("marker-size", size.toString());
    }
    
    public void setMarkerWidth(Integer width){
        this.attr.getProperties().setProperty("marker-width", width.toString());
    }
    
    public void setFillColor(Integer color){
        this.attr.getProperties().setProperty("fill-color", color.toString());
    }
    
    public int getMarkerWidth(){
        return Integer.parseInt(this.attr.getProperties().getProperty("marker-width"));
    }
    
    public int getFillColor(){
        return Integer.parseInt(this.attr.getProperties().getProperty("fill-color"));
    }
    
    public int getMarkerColor(){
        return Integer.parseInt(this.attr.getProperties().getProperty("marker-color"));
    }
    
    public int getMarkerStyle(){
        return Integer.parseInt(this.attr.getProperties().getProperty("marker-style"));
    }
    
    public int getMarkerSize(){
        return Integer.parseInt(this.attr.getProperties().getProperty("marker-size"));
    }

    public DataRegion getDataRegion() {
        
        DataRegion  region = new DataRegion();
        region.MINIMUM_X = this.dataX.getMin();
        region.MAXIMUM_X = this.dataX.getMax();
        region.MINIMUM_Y = this.dataY.getMin();
        region.MAXIMUM_Y = this.dataY.getMax();
        
        for(int loop = 0; loop < this.getDataSize(); loop++){
            double xl = this.getDataX(loop) - this.getErrorX(loop);
            double xh = this.getDataX(loop) + this.getErrorX(loop);
            if(xl < region.MINIMUM_X) region.MINIMUM_X = xl;
            if(xh > region.MAXIMUM_X) region.MAXIMUM_X = xh;
            
            double yl = this.getDataY(loop) - this.getErrorY(loop);
            double yh = this.getDataY(loop) + this.getErrorY(loop);
            
            if(yl < region.MINIMUM_Y) region.MINIMUM_Y = yl;
            if(yh > region.MAXIMUM_Y) region.MAXIMUM_Y = yh;
                
        }
        
        if(region.MINIMUM_Y==region.MAXIMUM_Y){
            if(region.MINIMUM_Y!=0){
                double range = region.MINIMUM_Y * 0.05;
                region.MINIMUM_Y = region.MINIMUM_Y - range;
                region.MAXIMUM_Y = region.MAXIMUM_Y + range;
            } else {
                region.MINIMUM_Y = region.MINIMUM_Y - 1;
                region.MAXIMUM_Y = region.MAXIMUM_Y + 1;
            }
        }
        region.growX(0.2, 0.2);
        region.growY(0.2, 0.2);
        return region;
    }

    public Integer getDataSize() {
        return this.dataX.size();
    }

    public Double getDataX(int index) {
        return this.dataX.getValue(index);
    }

    public Double getDataY(int index) {
        return this.dataY.getValue(index);
    }

    public Attributes getAttributes() {
        return this.attr;
    }

    public Double getErrorX(int index) {
        return this.dataEX.getValue(index);
    }

    public Double getErrorY(int index) {
        return this.dataEY.getValue(index);
    }

    public Double getData(int x, int y) {
        return 0.0;
    }

    public Integer getDataSize(int axis) {
        return this.getDataSize();
    }
}
