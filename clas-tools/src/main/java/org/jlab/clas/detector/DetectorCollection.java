/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas.detector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 *
 * @author gavalian
 */
public class DetectorCollection<T> {
    
    Map<Integer,T>  collection = new HashMap<Integer,T>();
    String          collectionName = "undefined";
    
    
    public void setName(String name){
        this.collectionName = name;
    }
    
    public String getName(){
        return this.collectionName;
    }
    
    public void add(int sector, int layer, int comp, T value){
        collection.put(DetectorDescriptor.generateHashCode(sector, layer, comp), value);
    }
    
    public void add(DetectorDescriptor desc, T value){
        int hash = DetectorDescriptor.generateHashCode(desc.getSector(), desc.getLayer(), 
                desc.getComponent());
        collection.put(hash, value);
    }
    
    public void clear(){
        this.collection.clear();
    }
    
    public boolean hasEntry(int sector, int layer, int comp){
        return this.collection.containsKey(DetectorDescriptor.generateHashCode(sector, layer, comp));
    }
    public List<T> getList(){
        Collection<T> vc = this.collection.values();
        List<T>  list = new ArrayList<T>();
        for(T c : vc){
            list.add(c);
        }
        return list;
    }
    public T  get(int sector, int layer, int comp){
        return this.collection.get(DetectorDescriptor.generateHashCode(sector, layer, comp));
    }
    /**
     * Returns Set of sectors defined in the map
     * @return 
     */
    public Set<Integer> getSectors(){
        Set<Integer>  list = this.collection.keySet();
        Set<Integer>  sectors = new HashSet<Integer>();
        
        for(Integer item : list){
            int sect = DetectorDescriptor.getSectorFromHash(item);
            sectors.add(sect);
        }
        return sectors;
    }
    /**
     * returns Set of layers for given sector.
     * @param sector
     * @return 
     */
    public Set<Integer> getLayers(int sector){
        Set<Integer>  list = this.collection.keySet();
        Set<Integer>  layers = new HashSet<Integer>();
        for(Integer item : list){
            int sect = DetectorDescriptor.getSectorFromHash(item);
            if(sect==sector){
                int lay = DetectorDescriptor.getLayerFromHash(item);
                layers.add(lay);
            }
        }
        return layers;
    }
    /**
     * returns component set for given sector and layer
     * @param sector
     * @param layer
     * @return 
     */
    public Set<Integer>  getComponents(int sector, int layer){
        Set<Integer>  list = this.collection.keySet();
        Set<Integer>  components = new HashSet<Integer>();
        for(Integer item : list){
            int sect = DetectorDescriptor.getSectorFromHash(item);
            int lay = DetectorDescriptor.getLayerFromHash(item);
            if(sect==sector&&lay==layer){
                int comp = DetectorDescriptor.getComponentFromHash(item);
                components.add(comp);
            }
        }
        return components;        
    }
    /**
     * Returns a Tree Model to display in tree selectors
     * @return 
     */
    public TreeModel getTreeModel(){
        DefaultMutableTreeNode  root = new DefaultMutableTreeNode(this.getName());
        Set<Integer>  Sectors   = this.getSectors();
        for(Integer sector : Sectors){
            DefaultMutableTreeNode  node_sector = new DefaultMutableTreeNode(sector.toString());
            Set<Integer>  Layers = this.getLayers(sector);
            for(Integer layer : Layers){
                DefaultMutableTreeNode  node_layer = new DefaultMutableTreeNode(layer.toString());
                Set<Integer> Components = this.getComponents(sector, layer);
                for(Integer component : Components){
                    DefaultMutableTreeNode  node_component = new DefaultMutableTreeNode(component.toString());
                    node_layer.add(node_component);
                }
                node_sector.add(node_layer);
            }
            root.add(node_sector);
        }
        return new DefaultTreeModel(root);
    }
    /**
     * Prints out the content of the 
     */
    public void show(){
        System.out.println(" DETECTOR COLLECTION : SIZE = " + this.collection.size());
        Set<Integer>  list = this.collection.keySet();
        for(Integer item : list){
            System.out.println(String.format("\t SECTOR/LAYAR/COMPONENT %4d %4d %4d", 
                    DetectorDescriptor.getSectorFromHash(item),
                    DetectorDescriptor.getLayerFromHash(item),
                    DetectorDescriptor.getComponentFromHash(item)
                    ));
        }
    }
}
