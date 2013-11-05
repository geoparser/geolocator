/**
 * 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 * 
 * @author Wei Zhang,  Language Technology Institute, School of Computer Science, Carnegie-Mellon University.
 * email: wei.zhang@cs.cmu.edu
 * 
 */
package edu.cmu.geoparser.ui.geolocator.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class About {

	public JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					About window = new About();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public About() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 240);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		frame.setTitle("About");
		
		JLabel lblVersion = new JLabel("Version:");
		lblVersion.setBounds(103, 105, 59, 14);
		lblVersion.setFont(new Font("Tahoma", Font.BOLD, 11));
		frame.getContentPane().add(lblVersion);
		
		JLabel lblSource = new JLabel("Source:");
		lblSource.setBounds(103, 125, 59, 34);
		lblSource.setFont(new Font("Tahoma", Font.BOLD, 11));
		frame.getContentPane().add(lblSource);
		
		JButton btnClose = new JButton("Close");
		btnClose.setBounds(178, 167, 77, 23);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		frame.getContentPane().add(btnClose);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 0, 0, 2);
		frame.getContentPane().add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(1374, 22, 0, 2);
		frame.getContentPane().add(separator_1);
		
		JLabel label = new JLabel("3.0");
		label.setBounds(168, 105, 42, 14);
		frame.getContentPane().add(label);
		
		JLabel lblHttpwwwgithubcomgelernter = new JLabel("http://www.github.com/gelernter/...");
		lblHttpwwwgithubcomgelernter.setBounds(168, 135, 256, 14);
		frame.getContentPane().add(lblHttpwwwgithubcomgelernter);
		
		JTextArea txtrTheGeolocationSoftware = new JTextArea();
		txtrTheGeolocationSoftware.setBounds(10, 10, 414, 64);
		txtrTheGeolocationSoftware.setBackground(UIManager.getColor("menu"));
		txtrTheGeolocationSoftware.setWrapStyleWord(true);
		txtrTheGeolocationSoftware.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtrTheGeolocationSoftware.setLineWrap(true);
		txtrTheGeolocationSoftware.setText("The geolocation software package contains a geo-parser that extracts locations and a geo-coder that assigns latitude and longitude to each location extracted. Both the geoparser results and geo-coder results can be scored separately using score algorithms included in the package.");
		frame.getContentPane().add(txtrTheGeolocationSoftware);
	}
}
