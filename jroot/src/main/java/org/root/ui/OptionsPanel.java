package org.root.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.root.attr.TStyle;
import org.root.base.IDataSet;
import org.root.basic.EmbeddedCanvas;
import org.root.basic.EmbeddedPad;
import org.root.func.Function1D;
import org.root.histogram.H1D;

public class OptionsPanel extends JPanel  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel axisOptions = new JPanel();
	JPanel frameOptions = new JPanel();
	ArrayList<JPanel> dataSetPanels = new ArrayList<JPanel>();
	ArrayList<String> dataSetNames = new ArrayList<String>();
	ArrayList<IDataSet> datasets = new ArrayList<IDataSet>();
	JPanel blankPanel = new JPanel();
	JCheckBox[] applyToAllCheckBoxes;
	double xMin, xMax, yMin, yMax;
	int ySliderMin = 0;
	int ySliderMax = 10000000;
	int xSliderMin = 0;
	int xSliderMax = 10000000;
	RangeSlider xSlider, ySlider;
	String xAxisLabel, yAxisLabel, title;
	List<String>   systemFonts = TStyle.systemFonts();
	int axisFontSize,titleFontSize,axisTitleFontSize,currentFont;
	EmbeddedCanvas canvas;
	int index;
	JTabbedPane tabbedPane;
	public OptionsPanel(EmbeddedCanvas canvas, int indx){
		this.canvas = canvas;
		this.index = indx;
		//System.out.println("Inializing Options Panel index:["+index+"]");
		xMin = canvas.getPad(index).getAxisX().getMin();
		xMax = canvas.getPad(index).getAxisX().getMax();
		yMin = canvas.getPad(index).getAxisY().getMin();
		yMax = canvas.getPad(index).getAxisY().getMax();
		//canvas.getPad(index).getAxisY().getAxisFont().getSize()
		//canvas.getPad(5).setAxisRange("X", 0.0, 5.0);
		int ndataset = canvas.getPad(index).getDataSetCount();
		for(int i = 0; i < ndataset; i++){
			IDataSet ds = canvas.getPad(index).getDataSet(i);
			datasets.add(canvas.getPad(index).getDataSet(i));
			String name = ds.getName();
			dataSetPanels.add(new JPanel());
			dataSetNames.add(name);
		}
		axisFontSize = canvas.getPad(index).getAxisX().getAxisFontSize();
		axisTitleFontSize = canvas.getPad(index).getAxisX().getAxisTitleFontSize();
		//currentFont = systemFonts.indexOf(canvas.getPad(index).getAxisX().getAxisFont());
		//There's probably a better way
		for(int i=0; i<systemFonts.size(); i++){
			if(canvas.getPad(index).getAxisX().getAxisFont().getName().compareTo(systemFonts.get(i)) ==0){
				currentFont = i;
			}
		}
		
		initDatasetOptions();
		initAxisOptions();
		initTabbedPanes();
		initFrameOptions();
	}

	private void initFrameOptions() {
		frameOptions.setBorder(new TitledBorder("Frame Options"));
		frameOptions.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
      
        JTextField xPixels = new JTextField();
        JTextField yPixels = new JTextField();
        JTextField ratio = new JTextField();
        JCheckBox lockRatio = new JCheckBox("Lock Ratio");
        double width = canvas.getSize().getWidth();
        double height = canvas.getSize().getHeight();
        xPixels.setText(String.format("%d",(int) width));
        yPixels.setText(String.format("%d",(int) height));
        ratio.setText(String.format("%.02f",width/height));
        c.gridy = 0;
        frameOptions.add(new JLabel("Width:"), c);
        frameOptions.add(xPixels, c);        
        frameOptions.add(new JLabel("Height:"), c);
        frameOptions.add(yPixels, c);
        xPixels.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		canvas.setPreferredSize(new Dimension(Integer.parseInt(xPixels.getText()),Integer.parseInt(yPixels.getText())));
        		canvas.setSize(Integer.parseInt(xPixels.getText()),Integer.parseInt(yPixels.getText()));
        		  double width = canvas.getSize().getWidth();
        	        double height = canvas.getSize().getHeight();
        	        xPixels.setText(String.format("%d",(int)width));
        	        yPixels.setText(String.format("%d",(int)height));
        	        ratio.setText(String.format("%.02f",width/height));
        	        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(canvas);
        	        topFrame.pack(); 
        	        canvas.update();
        	}
        });
        
        yPixels.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		canvas.setPreferredSize(new Dimension(Integer.parseInt(xPixels.getText()),Integer.parseInt(yPixels.getText())));
        		canvas.setSize(Integer.parseInt(xPixels.getText()),Integer.parseInt(yPixels.getText()));
        		  double width = canvas.getSize().getWidth();
        	        double height = canvas.getSize().getHeight();
        	        xPixels.setText(String.format("%d",(int)width));
        	        yPixels.setText(String.format("%d",(int)height));
        	        ratio.setText(String.format("%.02f",width/height));
        	        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(canvas);
        	        topFrame.pack();  
        	        canvas.update();
        	}
        });
        c.gridy = 2;
        frameOptions.add(new JLabel("Aspect Ratio:"), c);
        frameOptions.add(ratio, c);
        frameOptions.add(lockRatio, c);
        canvas.addComponentListener(new ComponentListener(){
			@Override
			public void componentResized(ComponentEvent e){
        		  	double width = canvas.getSize().getWidth();
        	        double height = canvas.getSize().getHeight();
        	        xPixels.setText(String.format("%d",(int)width));
        	        yPixels.setText(String.format("%d",(int)height));
        	        ratio.setText(String.format("%.02f",width/height));
        	       // canvas.setPreferredSize(new Dimension(Integer.parseInt(xPixels.getText()),Integer.parseInt(yPixels.getText())));
            		//canvas.setSize(Integer.parseInt(xPixels.getText()),Integer.parseInt(yPixels.getText()));
        	        //JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(canvas);
        	        //topFrame.pack(); 
        	        canvas.update();
        	    
        	}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
        });
        
        axisOptions.add(frameOptions,BorderLayout.CENTER);
	}

	private void initTabbedPanes() {
		tabbedPane = new JTabbedPane();
		tabbedPane.add("Axes", axisOptions);
		for(int i=0; i<dataSetPanels.size(); i++){
			tabbedPane.add(dataSetNames.get(i),dataSetPanels.get(i));
		}
		tabbedPane.addChangeListener(new ChangeListener(){

		    @Override
		    public void stateChanged(ChangeEvent arg0) {
		        if(tabbedPane.getSelectedIndex()==0){	
		        	tabbedPane.setComponentAt(0, axisOptions);
		        	
		        }
		        if(tabbedPane.getSelectedIndex()!=0){
		        	tabbedPane.setComponentAt(0, blankPanel);
		        }
			        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(tabbedPane);
					topFrame.pack();  
		    }   
		});
		this.add(tabbedPane);
	}
	private int returnIndex(int[] array, int number){
		for(int i=0; i<array.length; i++){
			if(array[i] == number){
				return i;
			}
		}
		return 0;
	}
	
	private void initAxisOptions(){
		axisOptions.setLayout(new BorderLayout());
		JPanel optionsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
		optionsPanel.setBorder(new TitledBorder("Options"));
		String[] lineThickness 	= {"1","2","3","4","5","6","7","8"};
		String[] fillColor 		= {"34","35","36","37","38"};
		String[] colors 		= {"1","2","3","4","5","6","7","8","9"};
		String[] colorNames 	= {"Black","White","Red","Green","Blue","Yellow","Magenta","Cyan","Dark Green","Purple-ish"};
		String[] fontSize 		= {"6","8","10","12","14","16","18","24","36","48"};
		int[] fontSizeInts 		= {6,8,10,12,14,16,18,24,36,48};
		String[] translucency = {"100%","80%","60%","40%","20%"};
		
		//JComboBox lineWidthBox = new JComboBox(lineThickness);
		JComboBox fontsBox	= new JComboBox(systemFonts.toArray());
		fontsBox.setSelectedIndex(currentFont);
		JComboBox axisFontSizeBox	= new JComboBox(fontSize);
		axisFontSizeBox.setSelectedIndex(returnIndex(fontSizeInts,axisFontSize));
		JComboBox axisTitleFontSizeBox = new JComboBox(fontSize);
		axisTitleFontSizeBox.setSelectedIndex(returnIndex(fontSizeInts,axisTitleFontSize));
		JComboBox titleFontSizeBox 	= new JComboBox(fontSize);
		titleFontSizeBox.setSelectedIndex(returnIndex(fontSizeInts,canvas.getPad(index).getAxisFrame().getTitleFontSize()));
		JComboBox statBoxFontSizeBox 	= new JComboBox(fontSize);
		statBoxFontSizeBox.setSelectedIndex(returnIndex(fontSizeInts,canvas.getPad(index).getPad().getStatBoxFontSize()));

		fontsBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).getAxisX().setAxisFontName(systemFonts.get(fontsBox.getSelectedIndex()));
				canvas.getPad(index).getAxisY().setAxisFontName(systemFonts.get(fontsBox.getSelectedIndex()));
				canvas.getPad(index).getAxisX().setTitleFontName(systemFonts.get(fontsBox.getSelectedIndex()));
				canvas.getPad(index).getAxisY().setTitleFontName(systemFonts.get(fontsBox.getSelectedIndex()));
				canvas.getPad(index).getAxisFrame().setTitleFontName(systemFonts.get(fontsBox.getSelectedIndex()));
				canvas.getPad(index).getPad().setStatBoxFontName(systemFonts.get(fontsBox.getSelectedIndex()));
				canvas.update();
			}
		});
		
		axisFontSizeBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).getAxisX().setAxisFontSize(fontSizeInts[axisFontSizeBox.getSelectedIndex()]);
				canvas.getPad(index).getAxisY().setAxisFontSize(fontSizeInts[axisFontSizeBox.getSelectedIndex()]);
				canvas.update();
			}
		});
		
		
		axisTitleFontSizeBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).getAxisX().setTitleFontSize(fontSizeInts[axisTitleFontSizeBox.getSelectedIndex()]);
				canvas.getPad(index).getAxisY().setTitleFontSize(fontSizeInts[axisTitleFontSizeBox.getSelectedIndex()]);
				canvas.update();
			}
		});
		
		titleFontSizeBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).getAxisFrame().setTitleFontSize(fontSizeInts[titleFontSizeBox.getSelectedIndex()]);
				canvas.update();
			}
		});
		
		statBoxFontSizeBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).getPad().setStatBoxFontSize(fontSizeInts[statBoxFontSizeBox.getSelectedIndex()]);
				canvas.update();
			}
		});
		//JComboBox fillColorBox = new JComboBox(fillColor);
		
		//JLabel lineWidthLabel = new JLabel("Line Width");
		JLabel fontLabel 	            = new JLabel("Font:");
		JLabel axisFontSizeLabel     	= new JLabel("Axis Label Font Size:");
		JLabel axisTitleFontSizeLabel 	= new JLabel("Axis Title Font Size:");
		JLabel titleFontSizeLabel 	    = new JLabel("Title Font Size:");
		JLabel statBoxFontSizeLabel 	= new JLabel("Stat Box Font Size:");

		
		JLabel xAxisTitleLabel = new JLabel("X Axis Title:");
		JLabel yAxisTitleLabel = new JLabel("Y Axis Title:");
		JLabel titleLabel      = new JLabel("Title:");

		JTextField xAxisTextField 	= new JTextField(canvas.getPad(index).getAxisX().getTitleString());
		JTextField yAxisTextField 	= new JTextField(canvas.getPad(index).getAxisY().getTitleString());
		JTextField titleTextField 	= new JTextField(canvas.getPad(index).getAxisFrame().getTitle());
		/*xAxisTextField.addKeyListener(new KeyListener(){
			 public void keyTyped(KeyEvent e) {
				 canvas.getPad(index).getAxisX().setTitle(xAxisTextField.getText());
					canvas.update();			   
				}
		});*/
		
		xAxisTextField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).getAxisX().setTitle(xAxisTextField.getText());
				canvas.update();
			}
		});
		
		yAxisTextField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).getAxisY().setTitle(yAxisTextField.getText());
				canvas.update();
			}
		});
		
		titleTextField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).getAxisFrame().setTitle(titleTextField.getText());
				canvas.update();
			}
		});
		
		JPanel gridPanel = new JPanel(new GridLayout(1,2));
		JCheckBox xGridBox = new JCheckBox("Grid X");
		xGridBox.setSelected(canvas.getPad(index).getAxisFrame().getGridX());
		xGridBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).getAxisFrame().setGridX(xGridBox.isSelected());
				canvas.update();
			}
		});
		JCheckBox yGridBox = new JCheckBox("Grid Y");
		yGridBox.setSelected(canvas.getPad(index).getAxisFrame().getGridY());
		yGridBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).getAxisFrame().setGridY(yGridBox.isSelected());
				canvas.update();
			}
		});
		
		gridPanel.add(xGridBox);
		gridPanel.add(yGridBox);
	
		
		
		/*
		JPanel logPanel = new JPanel(new GridLayout(1,3));
		JCheckBox xLogBox = new JCheckBox("Log X");
		xLogBox.setSelected(canvas.getPad(index).getLogX());
		xLogBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).setLogX(xLogBox.isSelected());
				canvas.update();
			}
		});
		JCheckBox yLogBox = new JCheckBox("Log Y");
		yLogBox.setSelected(canvas.getPad(index).getLogY());
		yLogBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).setLogY(yLogBox.isSelected());
				canvas.update();
			}
		});
		
		JCheckBox zLogBox = new JCheckBox("Log Z");
		zLogBox.setSelected(canvas.getPad(index).getLogZ());
		zLogBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				canvas.getPad(index).setLogZ(xLogBox.isSelected());
				canvas.update();
			}
		});
		
		logPanel.add(xLogBox);
		logPanel.add(yLogBox);
		logPanel.add(zLogBox);

		*/
		
		JPanel applyToAllPanel = new JPanel(new BorderLayout());
		String[] applyToAllOptions = {"Font","Title Font Size","Axis Title Font Size","Axis Label Font Size","Stat Box Font Size","Title","X Axis Title","Y Axis Title","Grid X","Grid Y","Range X","Range Y"};
		boolean[] applyToAllDefaults = {true,true,true,true,true,false,false,false,true,true,false,false};
		applyToAllCheckBoxes = new JCheckBox[applyToAllOptions.length];
		for(int i=0; i<applyToAllOptions.length; i++){
			applyToAllCheckBoxes[i] = new JCheckBox(applyToAllOptions[i]);
			applyToAllCheckBoxes[i].setSelected(applyToAllDefaults[i]);
		}
		JComboCheckBox applyToAllComboCheckBox = new JComboCheckBox(applyToAllCheckBoxes);
		JButton applyToAllButton = new JButton("Apply To All");
		applyToAllButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ArrayList<EmbeddedPad> canvasPads = canvas.canvasPads;
				for(int i=0; i<canvasPads.size();i++){
					canvas.cd(i);
					if(applyToAllCheckBoxes[0].isSelected()){
						canvas.getPad(i).getAxisX().setAxisFontName(systemFonts.get(fontsBox.getSelectedIndex()));
						canvas.getPad(i).getAxisY().setAxisFontName(systemFonts.get(fontsBox.getSelectedIndex()));
						canvas.getPad(i).getAxisX().setTitleFontName(systemFonts.get(fontsBox.getSelectedIndex()));
						canvas.getPad(i).getAxisY().setTitleFontName(systemFonts.get(fontsBox.getSelectedIndex()));
						canvas.getPad(i).getAxisFrame().setTitleFontName(systemFonts.get(fontsBox.getSelectedIndex()));
						canvas.getPad(i).getPad().setStatBoxFontName(systemFonts.get(fontsBox.getSelectedIndex()));

					} 
					if(applyToAllCheckBoxes[1].isSelected()){
						canvas.getPad(i).getAxisFrame().setTitleFontSize(fontSizeInts[titleFontSizeBox.getSelectedIndex()]);
					}
					if(applyToAllCheckBoxes[2].isSelected()){
						canvas.getPad(i).getAxisX().setAxisFontSize(fontSizeInts[axisFontSizeBox.getSelectedIndex()]);
						canvas.getPad(i).getAxisY().setAxisFontSize(fontSizeInts[axisFontSizeBox.getSelectedIndex()]);
					} 
					if(applyToAllCheckBoxes[3].isSelected()){
						canvas.getPad(i).getAxisX().setTitleFontSize(fontSizeInts[axisTitleFontSizeBox.getSelectedIndex()]);
						canvas.getPad(i).getAxisY().setTitleFontSize(fontSizeInts[axisTitleFontSizeBox.getSelectedIndex()]);
					}
					if(applyToAllCheckBoxes[4].isSelected()){
						canvas.getPad(i).getPad().setStatBoxFontSize(fontSizeInts[statBoxFontSizeBox.getSelectedIndex()]);
					}
					if(applyToAllCheckBoxes[5].isSelected()){
						canvas.getPad(i).getAxisFrame().setTitle(titleTextField.getText());
					}
					if(applyToAllCheckBoxes[6].isSelected()){
						canvas.getPad(i).getAxisX().setTitle(xAxisTextField.getText());

					}
					if(applyToAllCheckBoxes[7].isSelected()){
						canvas.getPad(i).getAxisY().setTitle(yAxisTextField.getText());
					}
					if(applyToAllCheckBoxes[8].isSelected()){
						canvas.getPad(i).getAxisFrame().setGridX(xGridBox.isSelected());

					}
					if(applyToAllCheckBoxes[9].isSelected()){
						canvas.getPad(i).getAxisFrame().setGridY(yGridBox.isSelected());
					}
					if(applyToAllCheckBoxes[10].isSelected()){
						canvas.getPad(i).setAxisRange("X",xSlider.getValue() * (xMax-xMin)/(double)(xSliderMax-xSliderMin) +xMin, xSlider.getUpperValue()* (xMax-xMin)/(double)(xSliderMax-xSliderMin)+xMin);
					}
					if(applyToAllCheckBoxes[11].isSelected()){
						canvas.getPad(i).setAxisRange("Y",ySlider.getValue() * (yMax-yMin)/(double)(ySliderMax-ySliderMin) +yMin, ySlider.getUpperValue()* (yMax-yMin)/(double)(ySliderMax-ySliderMin)+yMin);
					}
				}
				canvas.cd(index);
				canvas.update();
			}
		});
		applyToAllPanel.add(applyToAllComboCheckBox,BorderLayout.WEST);
		applyToAllPanel.add(applyToAllButton,BorderLayout.EAST);
		
		int yGrid = 0;
		c.gridy = yGrid++;
		optionsPanel.add(fontLabel,c);
		optionsPanel.add(fontsBox,c);
		c.gridy = yGrid++;
		optionsPanel.add(titleFontSizeLabel,c);
		optionsPanel.add(titleFontSizeBox,c);
		c.gridy = yGrid++;
		optionsPanel.add(axisTitleFontSizeLabel,c);
		optionsPanel.add(axisTitleFontSizeBox,c);
		c.gridy = yGrid++;	
		optionsPanel.add(axisFontSizeLabel,c);
		optionsPanel.add(axisFontSizeBox,c);
		c.gridy = yGrid++;
		optionsPanel.add(statBoxFontSizeLabel,c);
		optionsPanel.add(statBoxFontSizeBox,c);
		c.gridy = yGrid++;
		optionsPanel.add(titleLabel,c);
		optionsPanel.add(titleTextField,c);
		c.gridy = yGrid++;
		optionsPanel.add(titleLabel,c);
		optionsPanel.add(titleTextField,c);
		c.gridy = yGrid++;
		optionsPanel.add(xAxisTitleLabel,c);
		optionsPanel.add(xAxisTextField,c);
		c.gridy = yGrid++;
		optionsPanel.add(yAxisTitleLabel,c);
		optionsPanel.add(yAxisTextField,c);
		c.gridy = yGrid++;
		optionsPanel.add(xGridBox,c);
		optionsPanel.add(yGridBox,c);
		c.gridy = yGrid++;
		optionsPanel.add(applyToAllComboCheckBox,c);
		optionsPanel.add(applyToAllButton,c);
		/*
		JPanel fontsPanel = new JPanel(new BorderLayout());
		fontsPanel.add(fontLabel,BorderLayout.WEST);
		fontsPanel.add(fontsBox,BorderLayout.EAST);
		optionsPanel.add(fontsPanel);
		JPanel titleSizePanel = new JPanel(new BorderLayout());
		titleSizePanel.add(titleFontSizeLabel,BorderLayout.WEST);
		titleSizePanel.add(titleFontSizeBox,BorderLayout.EAST);
		optionsPanel.add(titleSizePanel);
		JPanel axisTitleSizePanel = new JPanel(new BorderLayout());
		axisTitleSizePanel.add(axisTitleFontSizeLabel,BorderLayout.WEST);
		axisTitleSizePanel.add(axisTitleFontSizeBox,BorderLayout.EAST);
		optionsPanel.add(axisTitleSizePanel);
		JPanel axisLabelPanel = new JPanel(new BorderLayout());
		axisLabelPanel.add(axisFontSizeLabel,BorderLayout.WEST);
		axisLabelPanel.add(axisFontSizeBox,BorderLayout.EAST);
		optionsPanel.add(axisLabelPanel);
		JPanel statTitleSizePanel = new JPanel(new BorderLayout());
		statTitleSizePanel.add(statBoxFontSizeLabel,BorderLayout.WEST);
		statTitleSizePanel.add(statBoxFontSizeBox,BorderLayout.EAST);
		optionsPanel.add(statTitleSizePanel);
		JPanel titleTextFieldPanel = new JPanel(new GridLayout(1,2));
		titleTextFieldPanel.add(titleLabel);
		titleTextFieldPanel.add(titleTextField);
		optionsPanel.add(titleTextFieldPanel);
		JPanel xAxisTextFieldPanel = new JPanel(new GridLayout(1,2));
		xAxisTextFieldPanel.add(xAxisTitleLabel);
		xAxisTextFieldPanel.add(xAxisTextField);
		optionsPanel.add(xAxisTextFieldPanel);
		JPanel yAxisTextFieldPanel = new JPanel(new GridLayout(1,2));
		yAxisTextFieldPanel.add(yAxisTitleLabel);
		yAxisTextFieldPanel.add(yAxisTextField);
		optionsPanel.add(yAxisTextFieldPanel);

		optionsPanel.add(gridPanel,BorderLayout.EAST);
		//optionsPanel.add(logPanel,BorderLayout.EAST);
		optionsPanel.add(applyToAllPanel,BorderLayout.EAST);
		*/

		//optionsPanel.add(fillColorLabel);
		//optionsPanel.add(fillColorBox);

		axisOptions.add(optionsPanel, BorderLayout.PAGE_START);

		JPanel rangePanel = new JPanel(new GridBagLayout());
		GridBagConstraints rangeConstraints = new GridBagConstraints();
		rangeConstraints.fill = GridBagConstraints.HORIZONTAL;
		JPanel xRangePanel =new JPanel(new FlowLayout());
		JPanel yRangePanel =new JPanel(new FlowLayout());

		rangePanel.setBorder(new TitledBorder("Range"));
		JLabel xrangeSliderValue1 = new JLabel(""+String.format("%4.2f",xMin));
		JLabel xrangeSliderValue2 = new JLabel(""+String.format("%4.2f",xMax));
		JLabel xAxisLabel = new JLabel("X:");
		
		//x axis
		xSlider = new RangeSlider();
		xSlider.setMinimum(xSliderMin);
		xSlider.setMaximum(xSliderMax);
		xSlider.setValue(xSliderMin);
		xSlider.setUpperValue(xSliderMax);
		rangeConstraints.gridy=0;
		//rangeConstraints.weightx = 0.5;
		rangePanel.add(xAxisLabel,rangeConstraints);
		rangePanel.add(xrangeSliderValue1,rangeConstraints);
		//rangeConstraints.weightx = 0.0;
		rangePanel.add(xSlider,rangeConstraints);
		//rangeConstraints.weightx = 0.5;
		rangePanel.add(xrangeSliderValue2,rangeConstraints);
		
		xSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				RangeSlider slider = (RangeSlider) e.getSource();
				xrangeSliderValue1.setText(String.valueOf(""+String.format("%4.2f",slider.getValue() * (xMax-xMin)/(double)(xSliderMax-xSliderMin)+xMin)));
				xrangeSliderValue2.setText(String.valueOf(""+String.format("%4.2f",slider.getUpperValue() *(xMax-xMin)/(double)(xSliderMax-xSliderMin)+xMin)));
				canvas.getPad(index).setAxisRange("X",slider.getValue() * (xMax-xMin)/(double)(xSliderMax-xSliderMin)+xMin , slider.getUpperValue()* (xMax-xMin)/(double)(xSliderMax-xSliderMin)+xMin);
				//canvas.repaint();
				canvas.update();
			}
		});
		
		//y axis
		JLabel yAxisLabel = new JLabel("Y:");
		JLabel yrangeSliderValue1 = new JLabel(""+String.format("%4.2f",yMin));
		JLabel yrangeSliderValue2 = new JLabel(""+String.format("%4.2f",yMax));
		ySlider = new RangeSlider();
		ySlider.setMinimum(ySliderMin);
		ySlider.setMaximum(ySliderMax);
		ySlider.setValue(ySliderMin);
		ySlider.setUpperValue(ySliderMax);
		rangeConstraints.gridy=1;
		//rangeConstraints.weightx = 0.5;
		rangePanel.add(yAxisLabel,rangeConstraints);
		rangePanel.add(yrangeSliderValue1,rangeConstraints);
		//rangeConstraints.weightx = 0.0;
		rangePanel.add(ySlider,rangeConstraints);
		rangePanel.add(yrangeSliderValue2,rangeConstraints);
		
		ySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				RangeSlider slider = (RangeSlider) e.getSource();
				yrangeSliderValue1.setText(String.valueOf(""+String.format("%4.2f",slider.getValue() * (yMax-yMin)/(double)(ySliderMax-ySliderMin)+yMin)));
				yrangeSliderValue2.setText(String.valueOf(""+String.format("%4.2f",slider.getUpperValue() *(yMax-yMin)/(double)(ySliderMax-ySliderMin)+yMin)));
				canvas.getPad(index).setAxisRange("Y",slider.getValue() * (yMax-yMin)/(double)(ySliderMax-ySliderMin)+yMin , slider.getUpperValue()* (yMax-yMin)/(double)(ySliderMax-ySliderMin)+yMin);
				//canvas.repaint();
				canvas.update();
			}
		});
		
		//rangePanel.add(xRangePanel);
		//rangePanel.add(yRangePanel);
		axisOptions.add(rangePanel, BorderLayout.PAGE_END);
	}
	
	private void initDatasetOptions(){
		ArrayList<JComboBox> lineWidthBoxes = new ArrayList<JComboBox>();
		ArrayList<JComboBox> fillColorBoxes = new ArrayList<JComboBox>();
		ArrayList<JComboBox> fillAlphaBoxes = new ArrayList<JComboBox>();
		ArrayList<JComboBox> lineColorBoxes = new ArrayList<JComboBox>();
		ArrayList<JComboBox> lineAlphaBoxes = new ArrayList<JComboBox>();
		ArrayList<JComboBox> lineStyleBoxes = new ArrayList<JComboBox>();
		ArrayList<JButton>   removeButtons = new ArrayList<JButton>();
		ArrayList<JCheckBox>     showStats = new ArrayList<JCheckBox>();

		for(int i=0; i<dataSetPanels.size();i++){
			dataSetPanels.get(i).setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
	        c.fill = GridBagConstraints.HORIZONTAL;
			dataSetPanels.get(i).setBorder(new TitledBorder("Data Options"));
			String[] lineThickness = {"1","2","3","4","5","6","7","8","9"};
			int[] lineThicknessInts = {1,2,3,4,5,6,7,8,9};
			String[] lineStyle = {"1","2","3","4","5"};
			int[] lineStyleInts = {1,2,3,4,5};
			String[] fillColor = {"0","1","2","3","4","5","6","7","8","9"};
			int[] fillColorInts = {0,1,2,3,4,5,6,7,8,9};
			String[] fillTransparency = {"0%","20%","40%","60%","80%"};
			int[] fillAlphaInts = {0,2,3,4,5};


			datasets.get(i).getAttributes().getAsInt("line-style");
			int currntLineColorCombined = datasets.get(i).getAttributes().getAsInt("line-color");
			int currentLineColor = currntLineColorCombined - (currntLineColorCombined/10)*10;
			int currentLineAlpha = currntLineColorCombined/10;
			int currentFillColorCombined = datasets.get(i).getAttributes().getAsInt("fill-color");
			int currentFillColor = currentFillColorCombined - (currentFillColorCombined/10)*10;
			int currentFillAlpha = currentFillColorCombined/10;

			lineWidthBoxes.add(new JComboBox(lineThickness));
			lineWidthBoxes.get(i).setSelectedIndex(returnIndex(lineThicknessInts,datasets.get(i).getAttributes().getAsInt("line-width")));
			lineWidthBoxes.get(i).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(int j=0; j<lineWidthBoxes.size(); j++){
						if(lineWidthBoxes.get(j).equals(e.getSource())){
							//System.out.println("Dataset:"+j+"/"+datasets.size()+" line-width:"+lineThicknessInts[lineWidthBoxes.get(j).getSelectedIndex()]+" index:"+lineWidthBoxes.get(j).getSelectedIndex());
							/*datasets.get(j).getAttributes().getProperties().put("line-width", lineThickness[lineWidthBoxes.get(j).getSelectedIndex()]);
							canvas.getPad(index).getPad().clear();
							for(int k=0; k<datasets.size(); k++){
								canvas.getPad(index).getPad().add(datasets.get(k),"same");
							}*/
							//canvas.update();
							canvas.getPad(index).getPad().getCollection().getDataSet(j).getAttributes().getProperties().put("line-width", lineThickness[lineWidthBoxes.get(j).getSelectedIndex()]);
						}
					}
					canvas.update();
				}
			});
			fillColorBoxes.add(new JComboBox(fillColor));
			fillColorBoxes.get(i).setSelectedIndex(returnIndex(fillColorInts,currentFillColor));
			fillColorBoxes.get(i).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(int j=0; j<fillColorBoxes.size(); j++){
						if(fillColorBoxes.get(j).equals(e.getSource())){
							//System.out.println("Dataset:"+j+"/"+datasets.size()+" fill-color:"+(fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[fillColorBoxes.get(j).getSelectedIndex()])+" alpha:"+fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10);
							/*
							 * datasets.get(j).getAttributes().getProperties().put("fill-color", ""+(fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[fillColorBoxes.get(j).getSelectedIndex()]));
							canvas.getPad(index).getPad().clear();
							for(int k=0; k<datasets.size(); k++){
								canvas.getPad(index).getPad().add(datasets.get(k),"same");
							}
							*/
							canvas.getPad(index).getPad().getCollection().getDataSet(j).getAttributes().getProperties().put("fill-color", ""+(fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[fillColorBoxes.get(j).getSelectedIndex()]));
							//canvas.update();
						}
					}
					canvas.update();
				}
			});
			fillAlphaBoxes.add(new JComboBox(fillTransparency));
			fillAlphaBoxes.get(i).setSelectedIndex(returnIndex(fillAlphaInts,currentFillAlpha));
			fillAlphaBoxes.get(i).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(int j=0; j<fillAlphaBoxes.size(); j++){
						if(fillAlphaBoxes.get(j).equals(e.getSource())){
							/*
							System.out.println("Dataset:"+j+"/"+datasets.size()+" fill-color:"+(fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[fillColorBoxes.get(j).getSelectedIndex()])+" alpha:"+fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10);
							datasets.get(j).getAttributes().getProperties().put("fill-color", ""+(fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[fillColorBoxes.get(j).getSelectedIndex()]));
							canvas.getPad(index).getPad().clear();
							for(int k=0; k<datasets.size(); k++){
								canvas.getPad(index).getPad().add(datasets.get(k),"same");
							}*/
							canvas.getPad(index).getPad().getCollection().getDataSet(j).getAttributes().getProperties().put("fill-color", ""+(fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[fillColorBoxes.get(j).getSelectedIndex()]));
							//canvas.update();
						}
					}
					canvas.update();
				}
			});
			lineColorBoxes.add(new JComboBox(fillColor));
			lineColorBoxes.get(i).setSelectedIndex(returnIndex(fillColorInts,currentLineColor));
			lineColorBoxes.get(i).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(int j=0; j<lineColorBoxes.size(); j++){
						if(lineColorBoxes.get(j).equals(e.getSource())){
							//System.out.println("Dataset:"+j+"/"+datasets.size()+" fill-color:"+(fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[fillColorBoxes.get(j).getSelectedIndex()])+" alpha:"+fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10);
							/*datasets.get(j).getAttributes().getProperties().put("line-color", ""+(fillAlphaInts[lineAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[lineColorBoxes.get(j).getSelectedIndex()]));
							canvas.getPad(index).getPad().clear();
							for(int k=0; k<datasets.size(); k++){
								canvas.getPad(index).getPad().add(datasets.get(k),"same");
							}*/
							canvas.getPad(index).getPad().getCollection().getDataSet(j).getAttributes().getProperties().put("line-color", ""+(fillAlphaInts[lineAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[lineColorBoxes.get(j).getSelectedIndex()]));
							//canvas.update();
						}
					}
					canvas.update();
				}
			});
			lineAlphaBoxes.add(new JComboBox(fillTransparency));
			lineAlphaBoxes.get(i).setSelectedIndex(returnIndex(fillAlphaInts,currentLineAlpha));
			lineAlphaBoxes.get(i).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(int j=0; j<lineAlphaBoxes.size(); j++){
						if(lineAlphaBoxes.get(j).equals(e.getSource())){
							//System.out.println("Dataset:"+j+"/"+datasets.size()+" fill-color:"+(fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[fillColorBoxes.get(j).getSelectedIndex()])+" alpha:"+fillAlphaInts[fillAlphaBoxes.get(j).getSelectedIndex()]*10);
							/*
							datasets.get(j).getAttributes().getProperties().put("line-color", ""+(fillAlphaInts[lineAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[lineColorBoxes.get(j).getSelectedIndex()]));
							canvas.getPad(index).getPad().clear();
							for(int k=0; k<datasets.size(); k++){
								canvas.getPad(index).getPad().add(datasets.get(k),"same");
							}*/
							canvas.getPad(index).getPad().getCollection().getDataSet(j).getAttributes().getProperties().put("line-color", ""+(fillAlphaInts[lineAlphaBoxes.get(j).getSelectedIndex()]*10+fillColorInts[lineColorBoxes.get(j).getSelectedIndex()]));
							//canvas.update();
						}
					}
					canvas.update();
				}
			});
			lineStyleBoxes.add(new JComboBox(lineStyle));
			lineStyleBoxes.get(i).setSelectedIndex(returnIndex(lineStyleInts,datasets.get(i).getAttributes().getAsInt("line-style")));
			lineStyleBoxes.get(i).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(int j=0; j<lineStyleBoxes.size(); j++){
						if(lineStyleBoxes.get(j).equals(e.getSource())){
							//System.out.println("BLAH Dataset:"+j+"/"+datasets.size()+" line-width:"+lineStyle[lineStyleBoxes.get(j).getSelectedIndex()]+" index:"+lineStyleBoxes.get(j).getSelectedIndex());
							//datasets.get(j).getAttributes().getProperties().put("line-style", lineStyle[lineStyleBoxes.get(j).getSelectedIndex()]);
							//canvas.getPad(index).getPad().clear();
							canvas.getPad(index).getPad().getCollection().getDataSet(j).getAttributes().getProperties().put("line-style", lineStyle[lineStyleBoxes.get(j).getSelectedIndex()]);
							//for(int k=0; k<datasets.size(); k++){
						//		canvas.getPad(index).getPad().add(datasets.get(k),"same");
							//}
							//canvas.update();
						}
					}
					canvas.update();
				}
			});
			removeButtons.add(new JButton("Remove Dataset"));
			removeButtons.get(i).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(int j=0; j<removeButtons.size(); j++){
						if(removeButtons.get(j).equals(e.getSource())){
							canvas.getPad(index).getPad().remove(datasets.get(j));
							int tabSelectionIndex = tabbedPane.getSelectedIndex();
							tabbedPane.setSelectedIndex(tabSelectionIndex-1);
							tabbedPane.remove(tabSelectionIndex);
							tabbedPane.repaint();
							dataSetPanels.remove(j);
						}
					}
					canvas.update();
				}
			});
			
			showStats.add(new JCheckBox());
			showStats.get(i).setSelected(canvas.getPad(index).getPad().getCollection().getDataSetOption(i).contains("S"));
			showStats.get(i).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(int j=0; j<showStats.size(); j++){
						if(showStats.get(j).equals(e.getSource())){
							String drawOptions = canvas.getPad(index).getPad().getCollection().getDataSetOption(j);
							if(showStats.get(j).isSelected()){
								canvas.getPad(index).getPad().getCollection().setDataSetOption(j, drawOptions+"S");
							}else{
								canvas.getPad(index).getPad().getCollection().setDataSetOption(j, drawOptions.replace("S", ""));

							}
						}
					}
					canvas.update();
				}
			});
			JLabel lineWidthLabel = new JLabel("Line Width:");
			JLabel  lineColorLabel = new JLabel("Line Color:");
			JLabel  lineAlphaLabel = new JLabel("Line Alpha:");
			JLabel  fillColorLabel = new JLabel("Fill Color:");
			JLabel  fillAlphaLabel = new JLabel("Fill Alpha:");
			JLabel lineStyleLabel =  new JLabel("Line Style:");
			
			boolean[] showOptions = {true,true,true,true};
			if(datasets.get(i) instanceof Function1D){
				showOptions[3] = false;
			}
			if(datasets.get(i) instanceof H1D){
				showOptions[1] = false;
			}
			int line = 0;
			if(showOptions[0]){
				c.gridy = line++; 
				dataSetPanels.get(i).add(lineWidthLabel,c);
				dataSetPanels.get(i).add(lineWidthBoxes.get(i),c);
			}
			if(showOptions[1]){
				c.gridy = line++; 
				dataSetPanels.get(i).add(lineStyleLabel,c);
				dataSetPanels.get(i).add(lineStyleBoxes.get(i),c);
			}
			if(showOptions[2]){
				c.gridy = line++; 
				dataSetPanels.get(i).add(lineColorLabel,c);
				dataSetPanels.get(i).add(lineColorBoxes.get(i),c);
				c.gridy = line++; 
				dataSetPanels.get(i).add(lineAlphaLabel,c);
				dataSetPanels.get(i).add(lineAlphaBoxes.get(i),c);
			}
			if(showOptions[3]){
				c.gridy = line++; 
				dataSetPanels.get(i).add(fillColorLabel,c);
				dataSetPanels.get(i).add(fillColorBoxes.get(i),c);
				c.gridy = line++; 
				dataSetPanels.get(i).add(fillAlphaLabel,c);
				dataSetPanels.get(i).add(fillAlphaBoxes.get(i),c);
			}
			c.gridy = line++; 
			dataSetPanels.get(i).add(new JLabel("Show Stats:"),c);
			dataSetPanels.get(i).add(showStats.get(i),c);
			c.gridy = line++; 
			dataSetPanels.get(i).add(removeButtons.get(i),c);
		}
		
	}
	
	private JPanel makeDataSetPanel(){
		return null;
	}
}
