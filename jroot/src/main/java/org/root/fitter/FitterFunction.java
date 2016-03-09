/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.root.fitter;

import org.freehep.math.minuit.FCNBase;
import org.root.base.IDataSet;
import org.root.data.DataVector;
import org.root.func.Function1D;


/**
 *
 * @author gavalian
 */
public class FitterFunction implements FCNBase {
    
    private IDataSet    dataset;
    private Function1D  function;
    private String      options = "*";
    
    public FitterFunction(IDataSet ds, Function1D func){
        dataset  = ds;
        function = func;
    }
    
    public FitterFunction(IDataSet ds, Function1D func, String opt){
        dataset  = ds;
        function = func;
        options  = opt;
    }
        
    public Function1D getFunction(){return function;}
    public IDataSet   getDataSet() { return this.dataset;}
    
    @Override
    public double valueOf(double[] pars) {
        this.function.setParameters(pars);
        double chi2 = this.function.getChiSquare(dataset,this.options);
        return chi2;
        
    }
    
}
