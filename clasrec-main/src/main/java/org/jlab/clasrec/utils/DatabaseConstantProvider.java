/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.clasrec.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.JFrame;
import org.jlab.ccdb.Assignment;
import org.jlab.ccdb.CcdbPackage;
import org.jlab.ccdb.JDBCProvider;
import org.jlab.ccdb.TypeTable;
import org.jlab.ccdb.TypeTableColumn;
import org.jlab.clas.tools.utils.StringTable;
import org.jlab.containers.HashTable;
import org.jlab.containers.HashTableViewer;
import org.jlab.geom.base.ConstantProvider;
import org.root.histogram.GraphErrors;


/**
 *
 * @author gavalian
 */
public class DatabaseConstantProvider implements ConstantProvider {
    
    private HashMap<String,String[]> constantContainer = new HashMap<String,String[]>();
    private boolean PRINT_ALL = true;
    private String variation  = "default";
    private Integer runNumber = 10;
    private Integer loadTimeErrors = 0;
    private Boolean PRINTOUT_FLAG  = false;
    
    private JDBCProvider provider;
    
    
    public DatabaseConstantProvider(){
        this.loadTimeErrors = 0;
        this.runNumber = 10;
        this.variation = "default";
        
        String address = "mysql://clas12reader@clasdb.jlab.org/clas12";
        
        String envAddress = this.getEnvironment();        
        if(envAddress!=null) address = envAddress;
        this.initialize(address);
    }
    
    public DatabaseConstantProvider(int run, String var){
        
        this.loadTimeErrors = 0;
        this.runNumber = run;
        this.variation = var;
        
        String address = "mysql://clas12reader@clasdb.jlab.org/clas12";
        
        String envAddress = this.getEnvironment();        
        if(envAddress!=null) address = envAddress;
        this.initialize(address);
    }
    
    public DatabaseConstantProvider(String address){
        this.initialize(address);
    }
    
    public DatabaseConstantProvider(String address, String var){
        this.variation = var;
        this.initialize(address);
    }
    
    public void setPrintout(Boolean flag){ this.PRINTOUT_FLAG = flag;}
    
    public Set<String> getEntrySet(){
        Set<String> entries = new HashSet<String>();
        for(Map.Entry<String,String[]> entry: this.constantContainer.entrySet()){
            entries.add(entry.getKey());
        }
        return entries;
    }
    
    private String getEnvironment(){
        
        String envCCDB   = System.getenv("CCDB_DATABASE");
        String envCLAS12 = System.getenv("CLAS12DIR");
        
        String propCLAS12 = System.getProperty("CLAS12DIR");
        String propCCDB   = System.getProperty("CCDB_DATABASE");
        
        //System.out.println("ENVIRONMENT : " + envCLAS12 + " " + envCCDB + " " + propCLAS12 + " " + propCCDB);
        
        if(envCCDB!=null&&envCLAS12!=null){
            StringBuilder str = new StringBuilder();
            str.append("sqlite:///");
            if(envCLAS12.charAt(0)!='/') str.append("/");
            str.append(envCLAS12);
            if(envCCDB.charAt(0)!='/' && envCLAS12.charAt(envCLAS12.length()-1)!='/'){
                str.append("/");
            }
            str.append(envCCDB);
            return str.toString();
        }
        
        if(propCCDB!=null&&propCLAS12!=null){
            StringBuilder str = new StringBuilder();
            str.append("sqlite:///");
            if(propCLAS12.charAt(0)!='/') str.append("/");
            str.append(propCLAS12);
            if(propCCDB.charAt(0)!='/' && propCLAS12.charAt(propCLAS12.length()-1)!='/'){
                str.append("/");
            }
            str.append(propCCDB);
            return str.toString();
        }
        
        return null;
    }
    
    private void initialize(String address){
        provider = CcdbPackage.createProvider(address);
        System.out.println("[DB] --->  open connection with : " + address);
        System.out.println("[DB] --->  database variation   : " + this.variation);
        System.out.println("[DB] --->  database run number  : " + this.runNumber);
        
        provider.connect();
        if(provider.getIsConnected()==true){
            System.out.println("[DB] --->  database connection  : success");
        } else {
            System.out.println("[DB] --->  database connection  : failed");
        }
        
        provider.setDefaultVariation(variation);
        provider.setDefaultRun(this.runNumber);

        //Assignment asgmt = provider.getData("/test/test_vars/test_table");
    }
    
    
    public HashTable  readTable(String table_name){

        Assignment asgmt = provider.getData(table_name);
        int ncolumns = asgmt.getColumnCount();
        Vector<TypeTableColumn> typecolumn = asgmt.getTypeTable().getColumns();
        String[] format = new String[ncolumns-3];
        
        for(int loop = 3; loop < ncolumns; loop++){
            //System.out.println("COLUMN " + typecolumn.get(loop).getName() 
            //        + "  " + typecolumn.get(loop).getCellType().name());
            if(typecolumn.get(loop).getCellType().name().compareTo("DOUBLE")==0){
                format[loop-3] = typecolumn.get(loop).getName() + ":d";
            } else {
                format[loop-3] = typecolumn.get(loop).getName() + ":i";
            }
            //format[loop-3] = 
        }
        
        HashTable  table = new HashTable(3,format);
        List< Vector<String> >  tableRows = new ArrayList< Vector<String> >();
        
        
        for(int loop = 0; loop < ncolumns; loop++){
                Vector<String> column = asgmt.getColumnValuesString(loop);
                tableRows.add(column);
        }
        
        int nrows = tableRows.get(0).size();
        
        for(int nr = 0 ; nr < nrows; nr++){
            String[] values = new String[ncolumns];
            for(int nc = 0; nc < ncolumns; nc++){
                values[nc] = tableRows.get(nc).get(nr);
            }
            //System.out.println(" LENGTH = " + values.length);
            table.addRow(values);
        }
        /*
                String[] values = new String[row.size()];
                System.out.println("VALUES SIZE = " + row.size());
                for(int el = 0; el < row.size(); el++){
                    values[el] = row.elementAt(el);                    
                    //for(String cell: row){
                    //System.out.print(cell + " ");
                }
                table.addRow(values);
        }*/
        return table;
    }
    
    public void loadTable(String table_name){
        try {
            Assignment asgmt = provider.getData(table_name);
            
            int ncolumns = asgmt.getColumnCount();
            TypeTable  table = asgmt.getTypeTable();
            Vector<TypeTableColumn> typecolumn = asgmt.getTypeTable().getColumns();
            System.out.println("[DB LOAD] ---> loading data table : " + table_name);
            System.out.println("[DB LOAD] ---> number of columns  : " + typecolumn.size());
            System.out.println();
            for(int loop = 0; loop < ncolumns; loop++){
                //System.out.println("Reading column number " + loop 
                //+ "  " + typecolumn.elementAt(loop).getCellType()
                //+ "  " + typecolumn.elementAt(loop).getName());
                Vector<String> row = asgmt.getColumnValuesString(loop);
                String[] values = new String[row.size()];
                for(int el = 0; el < row.size(); el++){
                    values[el] = row.elementAt(el);
                    //for(String cell: row){
                    //System.out.print(cell + " ");
                }
                StringBuilder str = new StringBuilder();
                str.append(table_name);
                str.append("/");
                str.append(typecolumn.elementAt(loop).getName());
                constantContainer.put(str.toString(), values);
                //System.out.println(); //next line after a row
            }
            //provider.close();
        } catch (Exception e){
            System.out.println("[DB LOAD] --->  error loading table : " + table_name);
            this.loadTimeErrors++;
        }
    }
    
    public void loadTables(String... tbl){
        for(String table : tbl){
            
        }
    }
    
    @Override
    public boolean hasConstant(String string) {
        return constantContainer.containsKey(string);
    }

    @Override
    public int length(String string) {
        if(this.hasConstant(string)) return constantContainer.get(string).length;
        return 0;
    }

    @Override
    public double getDouble(String string, int i) {
        if(this.hasConstant(string)==true && i < this.length(string)){
            return Double.parseDouble(constantContainer.get(string)[i]);
        } else {
            
        }
        return 0.0;
    }

    @Override
    public int getInteger(String string, int i) {
        if(this.hasConstant(string)==true && i < this.length(string)){
            return Integer.parseInt(constantContainer.get(string)[i]);
        } else {
            
        }
        return 0;
    }
    
    public void disconnect(){
        System.out.println("[DB] --->  database disconnect  : success");
        this.provider.close();
    }
    /**
     * prints out table with loaded values.
     */
    public void show(){
        System.out.println("\n\n");
        StringTable table = new StringTable();
        System.out.println("\t" + StringTable.getCharacterString("*", 70));
        System.out.println(String.format("\t*  %-52s : %8s   *", "Item Name","Length"));
        System.out.println("\t" + StringTable.getCharacterString("*", 70));
        System.out.print(this.showString());
        System.out.println("\t" + StringTable.getCharacterString("*", 70));
    }
    /**
     * returns a string representing a table printout of the constants
     * 
     * @return 
     */
    public String showString(){
        StringBuilder str = new StringBuilder();
        for(Map.Entry<String,String[]> item : this.constantContainer.entrySet()){
            str.append(String.format("\t*  %-52s : %8d   *\n", item.getKey(),item.getValue().length));
        }
        return str.toString();
    }
    
    @Override
    public String toString(){
        System.err.println("Database Constat Provider: ");
        StringBuilder str = new StringBuilder();
        if(PRINT_ALL==true){
            for(Map.Entry<String,String[]> entry : constantContainer.entrySet()){
                str.append(String.format("%24s : %d\n", entry.getKey(),entry.getValue().length));
                for(int loop = 0; loop < entry.getValue().length; loop++){
                    //str.append("\n");
                    str.append(String.format("%18s ", entry.getValue()[loop]));
                }
                str.append("\n");
            }
        } else {
            for(Map.Entry<String,String[]> entry : constantContainer.entrySet()){
                str.append(String.format("%24s : %d\n", entry.getKey(),entry.getValue().length));
            }
        }
        return str.toString();
    }
    
    
    public void clear(){
        this.constantContainer.clear();
    }
    
    public int getSize(){
        return this.constantContainer.size();
    }
    
    public int getSize(String name){
        if(this.hasConstant(name)==true){
            String[] array = this.constantContainer.get(name);
            return array.length;
        }
        return 0;
    }
    
    public GraphErrors getGraph(String constant){
        GraphErrors graph = new GraphErrors();
        if(this.constantContainer.containsKey(constant)==true){
            String[]  values = this.constantContainer.get(constant);
            int counter = 0;
            for(String item : values){
                counter++;
                graph.add((double) counter, Double.parseDouble(item) );
            }
        }
        return graph;
    }

    public static void main(String[] args){
        DatabaseConstantProvider provider = new DatabaseConstantProvider(10,"default");
        HashTable table = provider.readTable("/test/fc/fadc");
        table.addConstrain(3, 0.0, 90.0);
        provider.disconnect();
        JFrame frame = new JFrame();
        frame.setSize(600, 600);
        
        /*
        table.addRowAsDouble(new String[]{"21","7","1","0.5","0.1","0.6"});
        table.addRowAsDouble(new String[]{"22","8","2","0.6","0.2","0.7"});
        table.addRowAsDouble(new String[]{"23","9","3","0.7","0.3","0.8"});
        table.addRowAsDouble(new String[]{"24","10","4","0.8","0.4","0.9"});
        */
        //table.readFile("/Users/gavalian/Work/Software/Release-8.0/COATJAVA/coatjava/EC.table");
        //table.show();
        HashTableViewer canvas = new HashTableViewer(table);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
        table.show();
    }

}
