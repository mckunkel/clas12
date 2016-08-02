/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.func;

import java.util.ArrayList;
import java.util.List;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.function.Function;
import org.root.attr.Attributes;
import org.root.base.DataRegion;
import org.root.base.IDataSet;

/**
 *
 * @author gavalian
 */
public class Func1D implements IDataSet {
    
    private  String  funcName = "f1d";
    private  String  functionString = "p0";
    
    private  int     funcDrawResolution = 200;
    private  double  functionRangeMin   = 0.0;
    private  double  functionRangeMax   = 1.0;
    private final    List<RealParameter>  funcParams = new ArrayList<RealParameter>();
    Expression       expr = null;
    
    Function gausFunc = new Function("gaus", 3) {
        @Override
        public double apply(double... args) {
            return FunctionFactory.gauss(args[0],args[1],args[2]);
        }
    };
    
    Function landauFunc = new Function("landau", 3) {
        @Override
        public double apply(double... args) {
            return FunctionFactory.landau(args[0],args[1],args[2]);
        }
    };
    
    Function erfFunc = new Function("erf", 1) {
        @Override
        public double apply(double... args) {
            return ErrorFunction.erf(args[0]);
        }
    };    
    
    private Attributes attr = new Attributes();
    
    public Func1D(String name, double xmin, double xmax, int nparam){
        this.setName(name);
        this.setRange(xmin, xmax);
        this.setNPars(nparam);
        this.setLineColor(4);
        this.setLineWidth(2);
        this.setLineStyle(1);
    }
    
    public Func1D(String name, String function, double xmin, double xmax, int nparam){
        this.setName(name);
        this.setRange(xmin, xmax);
        this.setNPars(nparam);
        this.functionString = function;
        this.setLineColor(4);
        this.setLineWidth(2);
        this.setLineStyle(1);
    }
    
    public void setFunctionString(String func){
        this.functionString = func;
    }
    
    public String getFunctionString(){
        return this.functionString;
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
    
    public void setNPars(int npars){
        this.funcParams.clear();
        for(int i = 0; i < npars; i++){
            this.funcParams.add(new RealParameter("p"+i,0.0));
        }
    }
    
    public RealParameter  getRealParameter(int index){
        return this.funcParams.get(index);
    }
    
    public double getParameter(int index){
        return this.funcParams.get(index).value();
    }
    
    public int  getNPars(){
        return this.funcParams.size();
    }
    
    public double[] getParameters(){
        double[] pars = new double[this.funcParams.size()];
        for(int i =0; i < pars.length;i++){
            pars[i] = this.funcParams.get(i).value();
        }
        return pars;
    }
    
    public void setParameter(int index, double value){
        this.funcParams.get(index).setValue(value);
    }
    
    public void setParameters(double... pars){
        for(int i = 0; i < pars.length; i++){
            this.funcParams.get(i).setValue(pars[i]);
        }
    }
    
    public void setRange(double min, double max){
        this.functionRangeMin = min;
        this.functionRangeMax = max;
    }
    
    public final void initFunctionWithLine(String function){
        
    }
    
    public void setName(String name) {
        this.funcName = name;
    }

    public String getName() {
        return this.funcName;
    }

    public double getMin(){
        return this.functionRangeMin;
    }
    
    public double getMax(){
        return this.functionRangeMin;
    }
    
    public DataRegion getDataRegion() {
        DataRegion region = new DataRegion();
        region.MINIMUM_X = this.getMin();
        region.MAXIMUM_X = this.getMax();
        double ymin = this.getDataY(0);
        double ymax = this.getDataY(0);
        for(int loop = 0; loop < this.funcDrawResolution; loop++){
            double y = this.getDataY(loop);
            if(y>ymax) ymax = y;
            if(y<ymin) ymin = y;
        }
        region.MINIMUM_Y = ymin;
        region.MAXIMUM_Y = ymax;
        return region;
    }

    @Override
    public Integer getDataSize() {
        return this.funcDrawResolution;
    }

    @Override
    public Integer getDataSize(int axis) {
        return this.funcDrawResolution;
    }

    @Override
    public Double getData(int x, int y) {
        return 0.0;
    }

    @Override
    public Double getDataX(int index) {
        double step = (this.getMax()-this.getMin())/this.funcDrawResolution;
        return this.getMin() + index*step;
    }

    public double eval(double x){
        return 1.0;
    }
    
    @Override
    public Double getDataY(int index) {
        double x = this.getDataX(index);
        return this.eval(x);
    }

    @Override
    public Double getErrorX(int index) {
        return 0.0;
    }

    @Override
    public Double getErrorY(int index) {
        return 0.0;
    }

    @Override
    public Attributes getAttributes() {
        return this.attr;
    }
    
}
