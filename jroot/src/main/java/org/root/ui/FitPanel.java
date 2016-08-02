package org.root.ui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI.TabbedPaneLayout;

import org.root.base.IDataSet;
import org.root.basic.EmbeddedCanvas;
import org.root.basic.EmbeddedPad;
import org.root.fitter.DataFitter;
import org.root.func.F1D;
import org.root.histogram.H1D;
import org.root.pad.TCanvas;
import org.root.pad.TGCanvas;

@SuppressWarnings("unused")
public class FitPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	EmbeddedCanvas canvas;
	int index;
	int selectedTab = 0;
	JPanel fitSettings,fitSwapSettings, fitFunctionPanel, lowerWindow;
	F1D fitFunction;
	ArrayList<F1D> fitAllFitFunctions = new ArrayList<F1D>();
	H1D histogram;
	IDataSet thisDataset;
	ArrayList<IDataSet> datasets = new ArrayList<IDataSet>();
	ArrayList<String> dataSetNames = new ArrayList<String>();
	DataFitter fitter = new DataFitter();
	JComboBox paramEstimationMethods;
	ArrayList<JCheckBox> optionCheckBoxes;
	
	ParameterPanel parameterPanel;
	boolean parameterPanelSwapped = false;
	boolean fitSettingsSwapped = false;
	ParameterPanel parameterSwapPanel;
    JPanel blankPanel = new JPanel();
    boolean predef = true;
   // boolean hasDrawnStats = false;

	//Actual low and high of the x axis

	double xMin = 0.0;
	double xMax = 100.0;
	//This mess is due to the slider only working with integer values
	int xSliderMin = 0;
	int xSliderMax = 1000000;
	double currentRangeMin = 0.0;
	double currentRangeMax = 100.0;
	JComboBox predefinedFunctionsSelector;
	//Fit options
	String options = "";
	String predefFunctions[] = {"gaus", "gaus+p0", "gaus+p1", "gaus+p2", "gaus+p3","landau","landau+p0","landau+p1","landau+p2","landau+p3","p0","p1","p2","p3","erf","exp"};
	String functions[];
	ArrayList<F1D> userFunctions = new ArrayList<F1D>();
	FitPanel() {
		init();
	}
	
	public FitPanel(EmbeddedCanvas canvas, int indx) {
		this.canvas = canvas;
		this.index = indx;		
		//System.out.println("Inializing Fit Panel index:["+index+"]");
		xMin = canvas.getPad(index).getAxisX().getMin();
		xMax = canvas.getPad(index).getAxisX().getMax();
		int ndataset = canvas.getPad(index).getDataSetCount();
		for(int i = 0; i < ndataset; i++){
			IDataSet ds = canvas.getPad(index).getDataSet(i);
			String name = ds.getName();
			dataSetNames.add(name);
			datasets.add(ds);
		}
		this.thisDataset = datasets.get(0);
		userFunctions = canvas.getFunctions();
		init();
	}
	
	final void init(){
		this.setLayout(new BorderLayout());
		this.fitFunction = new F1D(predefFunctions[0],xMin, xMax);
		initFitFunction();
		initFitSettings();
		initRangeSelector();
	}

	void setFunction(F1D fitFunction) {
		//this.fitFunction = fitFunction;
		this.userFunctions.add(fitFunction);
	}


	void initFitFunction() {
		fitFunctionPanel = new JPanel(new GridLayout(2,1));
		fitFunctionPanel.setBorder(new TitledBorder("Function Settings"));

		JComboBox dataSetBox = new JComboBox(dataSetNames.toArray());
		dataSetBox.setSelectedIndex(0);
		dataSetBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				thisDataset = datasets.get(dataSetBox.getSelectedIndex());
			}
		});
		if(userFunctions.size()==0){
			functions = predefFunctions;
		}else{
			functions = new String[userFunctions.size()+predefFunctions.length];
			for(int i=0; i< userFunctions.size();i++){
				functions[i] = userFunctions.get(i).getName();
			}
			for(int i=0; i< predefFunctions.length; i++){
				functions[i+userFunctions.size()] = predefFunctions[i];
			}
		}
		// Labels from F1D class
		predefinedFunctionsSelector = new JComboBox(functions);
		predefinedFunctionsSelector.setSelectedIndex(0);
		fitFunction.initFunction(predefFunctions[predefinedFunctionsSelector.getSelectedIndex()], currentRangeMin, currentRangeMax);
		parameterPanel = new ParameterPanel(this.canvas,this.index,this.fitFunction);
		predefinedFunctionsSelector.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//System.out.println(predefinedFunctionsSelector.getSelectedIndex());
					//fitFunction = new F1D(predefFunctions[predefinedFunctionsSelector.getSelectedIndex()],currentRangeMin,currentRangeMax);
					//fitFunction.setFunction(predefFunctions[predefinedFunctionsSelector.getSelectedIndex()]);
					boolean functionSetup = false;
					for(int i=0; i<predefFunctions.length;i++){
						if(functions[predefinedFunctionsSelector.getSelectedIndex()].equals(predefFunctions[i])){
							fitFunction.initFunction(predefFunctions[predefinedFunctionsSelector.getSelectedIndex()], currentRangeMin, currentRangeMax);
							functionSetup = true;
							predef=true;
						}
					}
					if(!functionSetup){
						for(int i=0; i<userFunctions.size();i++){
							if(functions[predefinedFunctionsSelector.getSelectedIndex()].equals(userFunctions.get(i).getName())){
								fitFunction = userFunctions.get(i);
								fitFunction.setRange(currentRangeMin, currentRangeMax);
								functionSetup = true;
								predef=false;
							}
						}
					}
					
					//if(parameterPanelSwapped){
				//		parameterSwapPanel.updateNewFunction(fitFunction);
				//	}else{
					//}
					/*
					if(predefinedFunctionsSelector.getSelectedIndex()==3){
						System.out.println(predefFunctions[predefinedFunctionsSelector.getSelectedIndex()]);
						fitFunction.setParameter(0, 120.0);
						fitFunction.setParameter(1, 8.2);
						fitFunction.setParameter(2, 1.2);
						fitFunction.setParameter(3, 24.0);
						fitFunction.setParameter(4, 7.0);
					}else{
						for(int i=0; i<fitFunction.getNParams(); i++){
							fitFunction.setParameter(i, 5.0);
						}
					}*/
					if(predef){
					for(int i=0; i<fitFunction.getNParams(); i++){
						if(i==0){
							fitFunction.setParameter(0,getMaxYIDataSet(thisDataset,currentRangeMin, currentRangeMax));
						}else if(i==1){
							fitFunction.setParameter(1,getMeanIDataSet(thisDataset,currentRangeMin, currentRangeMax));
						}else if(i==2){
							fitFunction.setParameter(2,getRMSIDataSet(thisDataset,currentRangeMin, currentRangeMax));
						}else if(i==3){
							fitFunction.setParameter(3,getAverageHeightIDataSet(thisDataset,currentRangeMin, currentRangeMax));
						}else if(i>3){
							fitFunction.setParameter(i, 1.0);
						}
						System.out.println("Paramter "+i+" ="+fitFunction.getParameter(i));
					}
					}
					parameterPanel.updateNewFunction(fitFunction);
					JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(predefinedFunctionsSelector);
					topFrame.pack();
				}
		});
		
		JLabel labelForFunction = new JLabel("Function:");
		JLabel dataSetLabel = new JLabel("Select Dataset:");
		fitFunctionPanel.add(dataSetLabel);
		fitFunctionPanel.add(dataSetBox);
		fitFunctionPanel.add(labelForFunction);
		fitFunctionPanel.add(predefinedFunctionsSelector);
		this.add(fitFunctionPanel,BorderLayout.PAGE_START);
	}
	
	void initFitSettings() {

		JPanel fitMethod = new JPanel(new FlowLayout());
		String labs[] = {"Chi-square","Chi-square (Neyman)","Chi-square (Pearson)","Chi-square (All weights=1)", "Binned Extended-MLE"};
		paramEstimationMethods = new JComboBox(labs);
		/*
		paramEstimationMethods.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(paramEstimationMethods.getSelectedIndex()==0){
					options = "NRQ";
					System.out.print("Option:"+options);
				}else{
					options = "LRQ";
					System.out.print("Option:"+options);
				}
				
			}
		});*/
		
		
		paramEstimationMethods.setSelectedIndex(0);
		fitMethod.add(new JLabel("Method:"));
		fitMethod.add(paramEstimationMethods);

		JPanel fitOptions = new JPanel(new GridLayout(1, 2));
		String[] options = {"Draw Stats","Quiet"};
		optionCheckBoxes = new ArrayList<JCheckBox>();
		for (int i = 0; i < options.length; i++) {
			optionCheckBoxes.add(new JCheckBox(options[i]));
			fitOptions.add(optionCheckBoxes.get(i));
		}
		
		JTabbedPane tabbedPane = new JTabbedPane();
		fitSettings = new JPanel(new GridLayout(2, 1));
		fitSettings.add(fitMethod);
		fitSettings.add(fitOptions);
		//parameterPanel = new ParameterPanel(this.canvas,this.index,this.fitFunction);
		tabbedPane.add("Minimizer Settings", fitSettings);
		tabbedPane.add("Parameter Settings", blankPanel);

		tabbedPane.setBorder(new TitledBorder("Minimizer Settings"));
		tabbedPane.addChangeListener(new ChangeListener(){

		    @Override
		    public void stateChanged(ChangeEvent arg0) {
		        if(tabbedPane.getSelectedIndex()==0){	
		        	tabbedPane.setComponentAt(1, blankPanel);
		        	
		        }
		        if(tabbedPane.getSelectedIndex()==1){
		        	tabbedPane.setComponentAt(1, parameterPanel);
		        }
		        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(tabbedPane);
				topFrame.pack();  
		    }   
		});
		
		
		/*  @Override
	    public void stateChanged(ChangeEvent arg0) {
	        Component mCompo=tabbedPane.getSelectedComponent();
	        System.out.println(tabbedPane.getSelectedComponent().equals(parameterPanel)+" is Selected");
	        
	        if(tabbedPane.getSelectedComponent().equals(fitSettings)){
	        	
	        	
	        	tabbedPane.remove(parameterPanel);
	        	tabbedPane.add("Parameter Settings",blankPanel);
	        	if(fitSettingsSwapped){
	        		fitSettings = fitSwapSettings;
	        		fitSettingsSwapped = false;
	        	}
	        	parameterSwapPanel = parameterPanel;
	        	parameterPanel = (ParameterPanel) blankPanel;
	        	parameterPanelSwapped = true;
	        }else{
	        	if(parameterPanelSwapped){
	        		parameterPanel = parameterSwapPanel;
	        		parameterPanelSwapped = false;
	        	}
	        	fitSwapSettings = fitSettings;
	        	fitSettings = blankPanel;
        		fitSettingsSwapped = true;

	        }
	        
	        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(tabbedPane.getSelectedComponent());
			topFrame.pack();
	    }   
	});*/
		this.add(tabbedPane, BorderLayout.CENTER);
		//System.out.println("YES THIS IS THE CORRECT FILE");
	}
	
	private double getMeanIDataSet(IDataSet data, double min, double max){
		int nsamples = 0;
		double sum  = 0;
		double nEntries = 0;
		for(int i=0; i<data.getDataSize(); i++){
			double x = data.getDataX(i);
			double y = data.getDataY(i);
			if(x>min&&x<max&&y!=0){
				nsamples++;
				sum += x*y;
				nEntries+=y;
			}
		}
		return sum/(double)nEntries;
	}
	
	private double getRMSIDataSet(IDataSet data, double min, double max){
		int nsamples = 0;
		double mean  = getMeanIDataSet(data,min,max);
		double sum   = 0;
		double nEntries = 0;

		for(int i=0; i<data.getDataSize(); i++){
			double x = data.getDataX(i);
			double y = data.getDataY(i);
			if(x>min&&x<max&&y!=0){
				nsamples++;
				sum += Math.pow(x-mean,2)*y;
				nEntries+=y;
			}
		}
		return Math.sqrt(sum/(double)nEntries);
	}
	
	private double getAverageHeightIDataSet(IDataSet data, double min, double max){
		int nsamples = 0;
		double sum   = 0;
		for(int i=0; i<data.getDataSize(); i++){
			double x = data.getDataX(i);
			double y = data.getDataY(i);
			if(x>min&&x<max&&y!=0){
				nsamples++;
				sum += y;
			}
		}
		return sum/(double)nsamples;
	}

	
	private double getMaxXIDataSet(IDataSet data, double min, double max){
		double max1   = 0;
		double xMax   = 0;
		for(int i=0; i<data.getDataSize(); i++){
			double x = data.getDataX(i);
			double y = data.getDataY(i);
			if(x>min&&x<max&&y!=0){
				if(y>max1){
					max1 = y;
					xMax = x;
				}
			}
		}
		return xMax;
	}
	private double getMaxYIDataSet(IDataSet data, double min, double max){
		double max1   = 0;
		double xMax   = 0;
		for(int i=0; i<data.getDataSize(); i++){
			double x = data.getDataX(i);
			double y = data.getDataY(i);
			if(x>min&&x<max&&y!=0){
				if(y>max1){
					max1 = y;
					xMax = x;
				}
			}
		}
		return max1;
	}

	private void initRangeSelector() {
		lowerWindow = new JPanel(new GridLayout(2, 1));
		JPanel rangeSelector = new JPanel();
		JLabel xLabel = new JLabel("X:");
		RangeSlider slider = new RangeSlider();
		slider.setMinimum((int) xSliderMin);
		slider.setMaximum((int) xSliderMax);
		slider.setValue((int) xSliderMin);
		slider.setUpperValue((int) xSliderMax);

		currentRangeMin = slider.getValue() * (xMax-xMin)/(double)(xSliderMax-xSliderMin) + xMin;
		currentRangeMax = slider.getUpperValue() * (xMax-xMin)/(double)(xSliderMax-xSliderMin) + xMin;
	    JLabel rangeSliderValue1 = new JLabel(""+String.format("%4.2f",currentRangeMin));
	    JLabel rangeSliderValue2 = new JLabel(""+String.format("%4.2f",currentRangeMax));
		fitFunction.setRange(currentRangeMin, currentRangeMax);
		
		rangeSelector.add(xLabel);
	    rangeSelector.add(rangeSliderValue1);
		rangeSelector.add(slider);
	    rangeSelector.add(rangeSliderValue2);
		lowerWindow.add(rangeSelector);
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				RangeSlider slider = (RangeSlider) e.getSource();
				currentRangeMin = slider.getValue() * (xMax-xMin)/(double)(xSliderMax-xSliderMin) + xMin;
				currentRangeMax = slider.getUpperValue() * (xMax-xMin)/(double)(xSliderMax-xSliderMin) + xMin;
				rangeSliderValue1.setText(String.valueOf(""+String.format("%4.2f",currentRangeMin)));
				rangeSliderValue2.setText(String.valueOf(""+String.format("%4.2f",currentRangeMax)));
				//System.out.println("currentRangeMin:"+currentRangeMin+" xOffset:"+xOffset);
				//fitFunction.setRange(currentRangeMin, currentRangeMax);
			}
		});

		JButton fit = new JButton("Fit");
		fit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Construct options
				int method = paramEstimationMethods.getSelectedIndex();
				if(method==0){
					options = "ER";
				}else if(method==1){
					options = "NR";
				}else if(method==2){
					options = "PR";
				}else if(method==3){
					options ="R";
				}else if(method==4){
					options ="LR";
				}
				String drawOption = "";
				//System.out.println("******************BLAH "+optionCheckBoxes.size());
				for(int i=0; i<optionCheckBoxes.size(); i++){
					if(optionCheckBoxes.get(0).isSelected()){
						//if(optionCheckBoxes.get(0).isSelected()&&hasDrawnStats==false){

						drawOption = "S";
						//hasDrawnStats = true;
					//	System.out.println("Draw stats!");
					}
					if(optionCheckBoxes.get(1).isSelected()){
						options = options+"Q";
						//System.out.println("Draw quietly!");
					}
					//System.out.println("Options: "+optionCheckBoxes.get(i).getName()+ " is "+optionCheckBoxes.get(i).isSelected());
				}
				//System.out.println("******************BLAH2");
				//System.out.println("Fit Options:["+options+"]");

				fitFunction.setRange(currentRangeMin, currentRangeMax);
				//histogram.fit(fitFunction,options);
				if(predef&&!parameterPanel.modified()){
				for(int i=0; i<fitFunction.getNParams(); i++){
					if(i==0){
						fitFunction.setParameter(0,getMaxYIDataSet(thisDataset,currentRangeMin, currentRangeMax));
					}else if(i==1){
						fitFunction.setParameter(1,getMeanIDataSet(thisDataset,currentRangeMin, currentRangeMax));
					}else if(i==2){
						fitFunction.setParameter(2,getRMSIDataSet(thisDataset,currentRangeMin, currentRangeMax));
					}else if(i==3){
						fitFunction.setParameter(3,getAverageHeightIDataSet(thisDataset,currentRangeMin, currentRangeMax));
					}else if(i>3){
						fitFunction.setParameter(i, 1.0);
					}
					System.out.println("Paramter "+i+" ="+fitFunction.getParameter(i));

				}
				}
				fitter.fit(thisDataset, fitFunction,options);
				//fitFunction.show(); // print on the screen fit results
				fitFunction.setLineColor(2);
				fitFunction.setLineWidth(5);
				fitFunction.setLineStyle(1);
				canvas.cd(index);                                
				canvas.draw(fitFunction,"same"+drawOption);
				/*
				for(int i=0; i<canvas.getPad(index).getDataSetCount(); i++){
					System.out.println("Dataset#:"+i);
					Enumeration<?> blah = canvas.getPad(index).getDataSet(i).getAttributes().getProperties().propertyNames();
					while(blah.hasMoreElements()){
						System.out.println(blah.nextElement());
					}
				}*/
				
				/*
				ArrayList<IDataSet> nonDuplicateDataset = new ArrayList<IDataSet>();
				ArrayList<IDataSet> datasets1 = new ArrayList<IDataSet>();
				for(int i=0; i<canvas.getPad(index).getDataSetCount(); i++){
					datasets1.add(canvas.getPad(index).getDataSet(i));
					if(!nonDuplicateDataset.contains(datasets1.get(i))){
						nonDuplicateDataset.add(datasets1.get(i));
					}
				}
				canvas.getPad(index).
				
				*/
				parameterPanel.updateNewFunction(fitFunction);
			}
		});
		ArrayList<EmbeddedPad> canvasPads = canvas.canvasPads;
		JPanel fitButtons = new JPanel(new GridLayout(1,2));
        
		if(canvasPads.size()>1){
			
			JButton fitAll = new JButton("Fit All");
			fitAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Construct options
				int method = paramEstimationMethods.getSelectedIndex();
				if(method==0){
					options = "ER";
				}else if(method==1){
					options = "NR";
				}else if(method==2){
					options = "PR";
				}else if(method==3){
					options ="R";
				}else if(method==4){
					options ="LR";
				}
				String drawOption = "";
				//System.out.println("******************BLAH "+optionCheckBoxes.size());
				for(int i=0; i<optionCheckBoxes.size(); i++){
					//if(optionCheckBoxes.get(0).isSelected()&&hasDrawnStats==false){
					if(optionCheckBoxes.get(0).isSelected()){
						drawOption = "S";
						//hasDrawnStats = true;
					//	System.out.println("Draw stats!");
					}
					if(optionCheckBoxes.get(1).isSelected()){
						options = options+"Q";
						//System.out.println("Draw quietly!");
					}
					//System.out.println("Options: "+optionCheckBoxes.get(i).getName()+ " is "+optionCheckBoxes.get(i).isSelected());
				}
				//System.out.println("******************BLAH2");
				//System.out.println("Fit Options:["+options+"]");
				for(int padCounter=0; padCounter<canvasPads.size();padCounter++){
					double min  = canvas.getPad(padCounter).getAxisX().getMin();
					double max = canvas.getPad(padCounter).getAxisX().getMax();
					if(fitAllFitFunctions.size()!=canvasPads.size()){
						fitAllFitFunctions.add(new F1D(predefFunctions[predefinedFunctionsSelector.getSelectedIndex()], min, max));
					}else{
						fitAllFitFunctions.get(padCounter).initFunction(predefFunctions[predefinedFunctionsSelector.getSelectedIndex()], min, max);
					}
					ArrayList<IDataSet> tempDataset = new ArrayList<IDataSet>();
					int ndataset = canvas.getPad(padCounter).getDataSetCount();
					for(int i = 0; i < ndataset; i++){
						IDataSet ds = canvas.getPad(padCounter).getDataSet(i);
						String name = ds.getName();
						dataSetNames.add(name);
						tempDataset.add(ds);
					}
					IDataSet currentDataset = tempDataset.get(0);
					fitAllFitFunctions.get(padCounter).setRange(min, max);
				
				//histogram.fit(fitFunction,options);
				if(predef){
				for(int i=0; i<fitAllFitFunctions.get(padCounter).getNParams(); i++){
					if(i==0){
						fitAllFitFunctions.get(padCounter).setParameter(0,getMaxYIDataSet(currentDataset,min, max));
					}else if(i==1){
						fitAllFitFunctions.get(padCounter).setParameter(1,getMeanIDataSet(currentDataset,min, max));
					}else if(i==2){
						fitAllFitFunctions.get(padCounter).setParameter(2,getRMSIDataSet(currentDataset,min, max));
					}else if(i==3){
						fitAllFitFunctions.get(padCounter).setParameter(3,getAverageHeightIDataSet(currentDataset,min, max));
					}else if(i>3){
						fitAllFitFunctions.get(padCounter).setParameter(i, 1.0);
					}
					//System.out.println("Paramter "+i+" ="+fitFunction.getParameter(i));

				}}
				fitter.fit(currentDataset, fitAllFitFunctions.get(padCounter),options);
				//fitFunction.show(); // print on the screen fit results
				fitAllFitFunctions.get(padCounter).setLineColor(2);
				fitAllFitFunctions.get(padCounter).setLineWidth(5);
				fitAllFitFunctions.get(padCounter).setLineStyle(1);
				canvas.cd(padCounter);                                
				canvas.draw(fitAllFitFunctions.get(padCounter),"same"+drawOption);
				//parameterPanel.updateNewFunction(fitFunction);
				}
			}
		});
		fitButtons.add(fit);
		fitButtons.add(fitAll);
		lowerWindow.add(fitButtons);
		//lowerWindow.add(button);
		this.add(lowerWindow,BorderLayout.PAGE_END);
		}else{
			lowerWindow.add(fit);
			this.add(lowerWindow,BorderLayout.PAGE_END);
		}
		
	}
}
