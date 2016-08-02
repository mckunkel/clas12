/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.evio.clas12;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DetectorBankIndex {
    private List<Integer>   indexLength = new ArrayList<Integer>();
    private List<Integer>   indexList   = new ArrayList<Integer>();
    
    public DetectorBankIndex(){
        
    }
    
    public void clear(){
        this.indexLength.clear();
        this.indexList.clear();
    }
    
    public void add(int... index){
        this.indexLength.add(index.length);
        for(int idx : index){
            indexList.add(idx);
        }
    }
    
    public int getOffset(int nidx){
        if(nidx==0) return 0;
        int offset = 0;
        for(int i = 0; i < nidx; i++){
            offset += this.indexLength.get(i);
        }
        return offset;
    }
    
    public int  getIndexSize(){
        return this.indexLength.size();
    }
    
    public int[]  getOffsetData(){
        int[] array = new int[this.indexLength.size()];
        for(int i = 0; i < array.length; i++) array[i] = indexLength.get(i);
        return array;
    }
    
    public int[] getListData(){
        int[] array = new int[this.indexList.size()];
        for(int i = 0; i < array.length; i++) array[i] = indexList.get(i);
        return array;
    }
    
    public void init(int[] indx, int[] list){
        this.clear();
        for(int v : indx) this.indexLength.add(v);
        for(int l : list) this.indexList.add(l);
    }
    
    public List<Integer>  getList(int id){
        int offset = this.getOffset(id);
        List<Integer>  array = new ArrayList<Integer>();
        for(int i = 0; i < this.indexLength.get(id); i++){
            array.add(this.indexList.get(i+offset));
        }
        return array;
    }
    
    public static void main(String[] args){
        
        DetectorBankIndex index = new DetectorBankIndex();
        
        index.add(1);
        index.add(2,3);
        index.add(4,5,6);
        index.add(7,8,9,10);
        index.add(4,6,7);
        
        int size = index.getIndexSize();
        System.out.println(" size = " + size);
        for(int loop = 0; loop < size; loop++){
            System.out.println("--> " + loop  + "  =  " + index.getOffset(loop));
            List<Integer>  ind = index.getList(loop);
            System.out.println(ind);
        }
        
    }
}
