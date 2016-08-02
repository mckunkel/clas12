
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.root.func;
import java.util.ArrayList;
import org.root.attr.Attributes;
import org.root.base.IDataSet;
import org.root.data.DataSetXY;
import org.root.histogram.H1D;


/**
 *
 * @author gavalian
 */
public class Function1D  {
    
    private double     functionMin = 0;
    private double     functionMax = 0;
    private String  functionString = "g";
    private int     calculatedNDF  = 0;
    
    private final ArrayList<RealParameter>  funcParams = new ArrayList<RealParameter>();
    
    private final Attributes attr = new Attributes();
    
    public Function1D(int npar){
        this.setLineColor(4);
        this.setLineWidth(2);
        this.setLineStyle(1);
        for(int i = 0; i < npar;i++){
            this.parameters().add(new RealParameter("p"+i,0.0));
        }
        this.setRange(0.0, 1.0);
    }
    
    public Function1D(double min, double max, int npar){
        this.setLineColor(4);
        this.setLineWidth(2);
        this.setLineStyle(1);
        for(int i = 0; i < npar;i++){
            this.parameters().add(new RealParameter("p"+i,0.0));
        }
        this.setRange(min,max);
    }
    
    public Function1D(){
        this.setLineColor(4);
        this.setLineWidth(2);
        this.setLineStyle(1);
    }
    
    public Attributes getAttributes() {
        return this.attr;
    }
    
    public int  getLineColor(){
        return Integer.parseInt(this.attr.getProperties().getProperty("line-color"));
    }
    
    public int  getLineWidth(){
        return Integer.parseInt(this.attr.getProperties().getProperty("line-width"));
    }
    
    public int  getLineStyle(){
        return Integer.parseInt(this.attr.getProperties().getProperty("line-style"));
    }
    
    public final void setLineColor(Integer color){
        this.attr.getProperties().put("line-color", color.toString());
    }
    
    public final void setLineWidth(Integer width){
        this.attr.getProperties().put("line-width", width.toString());
    }
    
    public final void setLineStyle(Integer style){
        this.attr.getProperties().put("line-style", style.toString());
    }
    
    public void setRange(double min, double max){
        functionMin = min;
        functionMax = max;
    }
    
    public double getMin(){ return functionMin; }
    public double getMax(){ return functionMax; }
    
    public void setNParams(int n){
        funcParams.clear();
        for(int loop = 0; loop < n; loop++){
            String parname = "p"+loop;
            RealParameter par = new RealParameter();
            par.setValue(0.0);
            par.setName(parname);
            funcParams.add(par);
        }
    }
    
    public void setFunction(String funcstring){
        functionString = funcstring;
    }
    
    public int getNParams(){
        return this.funcParams.size();
    }
    
    public ArrayList<RealParameter>  parameters(){ return this.funcParams;}
    
    public double[] getParameters(){
        double[] pars = new double[this.funcParams.size()];
        for(int loop = 0; loop < this.funcParams.size();loop++){
            pars[loop] = funcParams.get(loop).value();
        }
        return pars;
    }
    
    public void setParameters(double[] par){
        for(int loop = 0; loop < par.length;loop++){
            funcParams.get(loop).setValue(par[loop]);
        }
    }
    
    public RealParameter parameter(int index){
        return funcParams.get(index);
    }
    
    public double eval(double x){
        return 1;
    }
    
    public DataSetXY getDataSet(){
        return getDataSet(100);
    }
    
    public DataSetXY getDataSet(int npoints){
        double step = (functionMax - functionMin)/npoints;
        DataSetXY data = new DataSetXY();
        for(int loop = 0; loop < npoints; loop++){
            double xv = functionMin+loop*step;
            double yv = this.eval(xv);
            data.getDataX().add(xv);
            data.getDataY().add(yv);
        }
        return data;
    }
    
    public int getNDF(){
        return this.calculatedNDF;
    }
    
    public int    getNDF(DataSetXY data){
        int npoints = 0;
        for(int loop = 0; loop < data.getDataY().getSize();loop++){
            double xv = data.getDataX().getValue(loop);
            if(xv>=this.getMin()&&xv<=this.getMax()){
                npoints++;
            }
        }
        return npoints-this.getNParams();
    }
    
    
    public double getChiSquare(IDataSet ds){
        return this.getChiSquare(ds,"*");
    }
    
    private double getChiSquareH1D(IDataSet ds, String options){
        double   errorSumm = 0.0;
        double   chiSquare = 0.0;
        int      ndfPoints = 0;
        
        boolean  funcRangeCheck   = false;
        int      mode             = 4; // mode 1 is Neyman, 2 - Pearson, 3 - include Error
        // 4 - all bins equal
        
        if(options.contains("E")==true) mode = 3;
        if(options.contains("N")==true) mode = 1;
        if(options.contains("P")==true) mode = 2;
        
        
        if(options.contains("L")==true){
            double lnl = 0.0;
            double A = 0.0;
            for(int bin = 0; bin < ds.getDataSize(); bin++){
         	   double xv = ds.getDataX(bin);
         	   double yv = ds.getDataY(bin);
         	   double fv = this.eval(xv);
         	   if(yv!=0&&xv>=this.getMin()&&xv<this.getMax()&&fv>0.00000000000000000000000001){
         		  lnl += yv*Math.log(fv);
         		  A+=fv;
         	   }
            }
            return -lnl+A;
        }
        
        if(options.contains("R")==true) funcRangeCheck = true;
        //System.out.println(" FITTING MODE = " + mode);
        for(int b = 0; b < ds.getDataSize();b++){
            
            double xv = ds.getDataX(b);
            double yv = ds.getDataY(b);
            double ye = ds.getErrorY(b);
            double fv = this.eval(xv);
            double denom = 1.0;
            if(mode==4) {
             

            } else {
                switch(mode){
                    case 1:  denom = yv; break;
                    case 2:  denom = fv; break;
                    case 3:  denom = ye*ye; break;
                    default: denom = 1.0; break;                    
                }
                
                if(yv!=0.0&&denom!=0.0){
                    if(funcRangeCheck==true){
                        if(xv>=this.getMin()&&xv<=this.getMax()){
                            chiSquare += (yv-fv)*(yv-fv)/denom;
                            //System.out.println("Bin = " + b +  " COUNTING X = " + xv +  "  Y = " + yv + "  DENOM = " + denom);
                            ndfPoints++;
                        }
                    } else {
                        chiSquare += (yv-fv)*(yv-fv)/denom;
                        ndfPoints++;
                    }
                }
            }            
            
        } 
        //System.out.println("CHI2/ NDF = " +  chiSquare +" / " +ndfPoints);
        return chiSquare;        
    }
    
    public double getChiSquare(IDataSet ds, String options){
        
        
        if(ds instanceof H1D){
            return this.getChiSquareH1D(ds, options);
        }
        
        double   errorSumm = 0.0;
        boolean  funcRangeCheck   = false;
        boolean  useDatasetErrors = false;
        //System.out.println("===========>  OPTIONS FOR FITTING = " + options);
        if(options.contains("E")==true) useDatasetErrors = true;        
        if(options.contains("R")==true) funcRangeCheck = true;
        
        for(int b = 0; b < ds.getDataSize();b++){
            if(funcRangeCheck==true){
                if(ds.getDataX(b)>=this.getMin()&&ds.getDataX(b)<=this.getMax()){
                    errorSumm += ds.getErrorY(b);
                }
            } else {
                errorSumm += ds.getErrorY(b);
            }
        }
        
        boolean ignoreErrors = false;
        
        if(errorSumm<0.0000001){
            ignoreErrors = true;
        }
        
        if(useDatasetErrors==false){
            ignoreErrors = true;
        }
        
        double chiSquare = 0.0;
        int    ndfPoints = 0;
        
        for(int bin = 0; bin < ds.getDataSize(); bin++){
            double yerror = ds.getErrorY(bin);
            if(ignoreErrors==true){
                double xv = ds.getDataX(bin);
                double yv = ds.getDataY(bin);
                double fv = this.eval(xv);                
                if(funcRangeCheck==true){
                    if(xv>=this.getMin()&&xv<this.getMax()){
                        chiSquare += (yv-fv)*(yv-fv);
                        ndfPoints++;
                    }
                } else {
                    chiSquare += (yv-fv)*(yv-fv);
                    ndfPoints++;
                }

            } else {
                double xv = ds.getDataX(bin);
                double yv = ds.getDataY(bin);
                double fv = this.eval(xv);
                if(yerror!=0){
                    if(funcRangeCheck==true){
                        if(xv>=this.getMin()&&xv<this.getMax()){
                            chiSquare += (yv-fv)*(yv-fv)/(yerror*yerror);                        
                            ndfPoints++;
                        }
                    } else {
                        chiSquare += (yv-fv)*(yv-fv)/(yerror*yerror);                        
                        ndfPoints++;
                    }
                }
            }
        }
        this.calculatedNDF = ndfPoints - this.getNParams();
        return chiSquare;
    }
    /*
    public double getChiSquare(DataSetXY data){
        double chi2 = 0.0;
        for(int loop = 0; loop < data.getDataY().getSize();loop++){
            double xv = data.getDataX().getValue(loop);
            double yv = data.getDataY().getValue(loop);
            double fv = this.eval(xv);
            if(yv!=0&&xv>=this.getMin()&&xv<=this.getMax()){
                chi2 += (yv-fv)*(yv-fv)/(yv);
                //System.err.println("adding " + xv + " " + chi2);
            }
        }
        return chi2;
    }*/
    
    public void show(){
        StringBuilder str = new StringBuilder();
        str.append("FUNCTION : "); 
        str.append(functionString);
        str.append("\n");
        str.append("LIMITS   : "); 
        str.append(String.format("%e %e\n\n", this.getMin(),this.getMax())); 
        for(RealParameter par : funcParams){
            str.append(par.toString());
            str.append("\n");
        }
        System.err.println(str.toString());
    }
    
}
