package com.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Window extends JFrame {
	
	protected static int height = 628; //Full height
	protected static int width = 1006; //Full width
	protected static int panelHeight = 600; //Map height
	protected static int panelWidth = 800; //Map width
	protected Flock panel = new Flock();
	
	public Window() {
		
		//Settings
		this.setTitle("Boids");
		this.setSize(Window.width, Window.height);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		//Right menu
		Box box = Box.createVerticalBox();
		
		//Slider 1
		box.add(Box.createVerticalStrut(50));
		JLabel sepLabel = new JLabel("Separation weight");
		sepLabel.setAlignmentX(CENTER_ALIGNMENT);
		JSlider sepSlider = new JSlider();
		sepSlider.setMaximum(100);
		sepSlider.setMinimum(0);
		sepSlider.setValue(50);
		sepSlider.setPaintTicks(true);
		sepSlider.setPaintLabels(true);
		sepSlider.setMinorTickSpacing(10);
		sepSlider.setMajorTickSpacing(20);
		sepSlider.addChangeListener(new ChangeListener() {
			public void stateChanged (ChangeEvent event) {  
				Flock.setSeparationWeight(((JSlider)event.getSource()).getValue() / 50.0 * Flock.separationWeightIni);
			}
		});
		box.add(sepLabel);
		box.add(sepSlider);
		
		//Slider 2
		box.add(Box.createVerticalStrut(50));
		JLabel cohLabel = new JLabel("Cohesion weight");
		cohLabel.setAlignmentX(CENTER_ALIGNMENT);
		JSlider cohSlider = new JSlider();
		cohSlider.setMaximum(100);
		cohSlider.setMinimum(0);
		cohSlider.setValue(50);
		cohSlider.setPaintTicks(true);
		cohSlider.setPaintLabels(true);
		cohSlider.setMinorTickSpacing(10);
		cohSlider.setMajorTickSpacing(20);
		cohSlider.addChangeListener(new ChangeListener() {
			public void stateChanged (ChangeEvent event) {  
				Flock.setCohesionWeight(((JSlider)event.getSource()).getValue() / 50.0 * Flock.cohesionWeightIni);
			}
		});
		box.add(cohLabel, BorderLayout.NORTH);
		box.add(cohSlider, BorderLayout.NORTH);
		
		//Slider 3
		box.add(Box.createVerticalStrut(50));
		JLabel aliLabel = new JLabel("Alignment weight");
		aliLabel.setAlignmentX(CENTER_ALIGNMENT);
		JSlider aliSlider = new JSlider();
		aliSlider.setMaximum(100);
		aliSlider.setMinimum(0);
		aliSlider.setValue(50);
		aliSlider.setPaintTicks(true);
		aliSlider.setPaintLabels(true);
		aliSlider.setMinorTickSpacing(10);
		aliSlider.setMajorTickSpacing(20);
		aliSlider.addChangeListener(new ChangeListener() {
			public void stateChanged (ChangeEvent event) {  
				Flock.setAlignmentWeight(((JSlider)event.getSource()).getValue() / 50.0 * Flock.alignmentWeightIni);
			}
		});
		box.add(aliLabel, BorderLayout.NORTH);
		box.add(aliSlider, BorderLayout.NORTH);
		
		//Buttons "add an obstacle" and "remove all"
		box.add(Box.createVerticalStrut(50));
		JLabel obstaclesLabel = new JLabel("Obstacles");
		obstaclesLabel.setAlignmentX(CENTER_ALIGNMENT);
		box.add(obstaclesLabel);
		JButton addObstacle = new JButton("Add");
		addObstacle.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				panel.addObstacle();
			}
		});
		JButton removeObstacles = new JButton("Remove");
		removeObstacles.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				panel.removeObstacles();
			}
		});
		Box boxH = Box.createHorizontalBox();
		boxH.add(addObstacle);
		boxH.add(removeObstacles);
		box.add(boxH);
		
		//Buttons "add a predator" and "remove all"
		box.add(Box.createVerticalStrut(50));
		JLabel predatorsLabel = new JLabel("Predators");
		predatorsLabel.setAlignmentX(CENTER_ALIGNMENT);
		box.add(predatorsLabel);
		JButton addPredator = new JButton("Add");
		addPredator.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				panel.addPredator();
			}
		});
		JButton removePredators = new JButton("Remove");
		removePredators.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				panel.removePredators();
			}
		});
		boxH = Box.createHorizontalBox();
		boxH.add(addPredator);
		boxH.add(removePredators);
		box.add(boxH);
		
		//Display window
		this.getContentPane().add(box, BorderLayout.EAST); 
		this.panel.setBackground(Color.white);
		this.getContentPane().add(panel);
		this.setVisible(true);
	
		//Launch animation
		animation();
		
	}
	
	
	public void animation() {
		for (;;) {
			this.panel.repaint(); //refresh
			try {
		        Thread.sleep(10);
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	
	//Getters
	
	public static int height() {
		return Window.height;
	}
	
	public static int width() {
		return Window.width;
	}
	
	public static int panelHeight() {
		return Window.panelHeight;
	}
	
	public static int panelWidth() {
		return Window.panelWidth;
	}
	
	//Setters
	
	public static void setPanelHeight(int h) {
		Window.panelHeight = h;
	}
	
	public static void setPanelWidth(int w) {
		Window.panelWidth = w;
	}
	
	

}
