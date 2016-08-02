/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clasrec.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jlab.clas.tools.utils.JarFileScanner;
import org.jlab.clas12.basic.IDetectorModule;

/**
 *
 * @author gavalian
 */
public class DetectorModulePlugin extends JDialog implements ActionListener{
    JarFileScanner  scanner = new JarFileScanner();
    JTable          table   = new JTable();
    List<IDetectorModule>   modules = new ArrayList<IDetectorModule>();    
    private Object[]        tableColumns = new String[]{"Class","Name","Author","Description"};
    IDetectorModule         selectedModule = null;
    Boolean                 successStatus  = false;
    
    public DetectorModulePlugin(){
        super();
        this.setSize(800,500);
        this.setLayout(new BorderLayout());
        JScrollPane  pane = new JScrollPane(table);
        this.add(pane,BorderLayout.CENTER);
        
        JPanel  buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout());
        
        JButton  cancel = new JButton("Cancel");
        JButton  load   = new JButton("Load");
        cancel.addActionListener(this);
        load.addActionListener(this);
        
        buttonPane.add(load);
        buttonPane.add(cancel);
        this.update();
        this.add(buttonPane,BorderLayout.PAGE_END);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setVisible(true);
    }
    
    
    public void update(){
        List<String>  classList = scanner.scanDir("lib/user", "org.jlab.clas12.basic.IDetectorModule");
        System.out.println("[UPDATE] -----> LOADED files # " + classList.size());
        this.modules.clear();
        for(String classname : classList){
            try {
                Class  c = Class.forName(classname);
                IDetectorModule module = (IDetectorModule) c.newInstance();
                this.modules.add(module);
            } catch (ClassNotFoundException ex) {
                System.out.println("Error:  class not found : " + classname);
            } catch (InstantiationException ex) {
                System.out.println("Error: error instansiating class : " + classname);
            } catch (IllegalAccessException ex) {
                System.out.println("Error: illegal access to the class : " + classname);
            }
        }
        
        DefaultTableModel  model = new DefaultTableModel();
        model.setColumnIdentifiers(tableColumns);
        
        for(IDetectorModule module : this.modules){
            String[]  row = new String[4];
            row[0] = module.getClass().getName();
            row[1] = module.getName();
            row[2] = module.getAuthor();
            row[3] = module.getDescription();
            model.addRow(row);
        }
        this.table.setModel(model);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("Cancel")==0){
            this.setVisible(false);
            //this.dispose();
        }
        if(e.getActionCommand().compareTo("Load")==0){
            int row = table.getSelectedRow();
            System.out.println("Selected Row = " + row);
            if(row>=0){
                this.selectedModule = this.modules.get(row);
                this.successStatus  = true;
                this.setVisible(false);
            }
        }
        
    }
    
    public IDetectorModule  getSelectedModule(){
        return this.selectedModule;
    }
    public static void main(String[] args){
        
        DetectorModulePlugin  plugin = new DetectorModulePlugin();
        if(plugin.getSelectedModule()!=null){
            IDetectorModule  module = plugin.getSelectedModule();
            JFrame frame = new JFrame();
            frame.add(module.getDetectorPanel());
            frame.pack();
            frame.setVisible(true);
        }
        System.out.println("Reached here");
    }
}
