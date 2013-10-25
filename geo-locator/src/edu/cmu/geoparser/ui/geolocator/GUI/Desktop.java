package edu.cmu.geoparser.ui.geolocator.GUI;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import edu.cmu.geoparser.Disambiguation.ContextDisamb;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.io.GetWriter;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.languagedetector.LangDetector;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.parser.english.EnglishParser;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class Desktop {

  private JFrame frame;

  private JTextField txtCusers;

  private JTextField txtCusersoutput;

  private BufferedWriter bw;// for writing to the file

  private BufferedReader br;// for reading to the file

  private StringBuilder sb;// for showing the text in the output box

  private String gazpath, resroot, enNER, langd;

  private IndexSupportedTrie topotrie;

  private FeatureGenerator enfgen;

  private EnglishParser enparser;

  private ContextDisamb c;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Desktop window = new Desktop();
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
  public Desktop() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {

    /**
     * Initialize tagging resourses, get ready for tagging.
     */
    final IndexSupportedTrie topotrie = new IndexSupportedTrie("GeoNames/cities1000.txt", "GazIndex/",true, false);

    final EnglishParser enparser = new EnglishParser("res/", topotrie, false);
 
    final ContextDisamb c = new ContextDisamb();

    /**
     * Initialize the main window
     */
    Border blackline = BorderFactory.createLineBorder(Color.black);
    frame = new JFrame();
    frame.setBounds(100, 100, 588, 486);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.setTitle("GeoLocator");

    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);

    JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);

    JMenuItem mntmNew = new JMenuItem("New");
    mnFile.add(mntmNew);

    JMenuItem mntmOpen = new JMenuItem("Open");
    mnFile.add(mntmOpen);

    JMenuItem mntmExit = new JMenuItem("Exit");
    mntmExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    mnFile.add(mntmExit);

    JMenu mnHelp = new JMenu("Help");
    menuBar.add(mnHelp);

    JMenuItem mntmAbout = new JMenuItem("About");
    mnHelp.add(mntmAbout);
    mntmAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        About window = new About();
        window.frame.setVisible(true);
      }
    });
    frame.getContentPane().setLayout(null);

    /**
     * Message Box text area.
     */
    final JTextArea txtrMessageBox = new JTextArea();

    txtrMessageBox
            .setText("Welcome to Geo-parser! It's developed by the Language Technology Institute in CMU.");
    txtrMessageBox.setEditable(false);
    txtrMessageBox.setLineWrap(true);
    txtrMessageBox.setBounds(10, 11, 288, 178);

    frame.getContentPane().add(txtrMessageBox);

    /**
     * Output box text area
     */
    final JTextArea txtrOutputBox = new JTextArea();
    txtrOutputBox.setText("Run the algorithm, and part of the results will be shown here.");
    txtrOutputBox.setBounds(10, 217, 288, 178);
    txtrOutputBox.setBorder(blackline);
    txtrOutputBox.setLineWrap(true);
    JScrollPane oscrollPane = new JScrollPane(txtrOutputBox);
    oscrollPane.setVisible(true);
    frame.getContentPane().add(txtrOutputBox);

    /**
     * input path, read in inbox content as file name
     */
    txtCusers = new JTextField();
    txtCusers.setText("Input file");
    txtCusers.setToolTipText("Input path");
    txtCusers.setBounds(338, 11, 180, 20);
    frame.getContentPane().add(txtCusers);
    txtCusers.setColumns(10);

    /**
     * output path, read in output box content as file name
     */
    txtCusersoutput = new JTextField();
    txtCusersoutput.setText("output file");
    txtCusersoutput.setToolTipText("Output path");
    txtCusersoutput.setBounds(338, 59, 180, 20);
    frame.getContentPane().add(txtCusersoutput);
    txtCusersoutput.setColumns(10);

    /**
     * Input file button clicked
     */
    JButton btnNewButton = new JButton("Input File");
    btnNewButton.setToolTipText("Select file of toponyms to tag.");
    btnNewButton.setBounds(528, 10, 33, 23);
    frame.getContentPane().add(btnNewButton);
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // to do: fill out the input text area with selected file path.
      }
    });
    /**
     * output file button clicked, output file is ready
     */
    JButton btnNewButton_1 = new JButton("Ouput File");
    btnNewButton_1.setToolTipText("Select file to store results.");
    btnNewButton_1.setBounds(528, 58, 33, 23);
    frame.getContentPane().add(btnNewButton_1);
    btnNewButton_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // to do: fill out the output text area with selected file path.
      }
    });

    JButton btnRun = new JButton("Run");
    btnRun.setToolTipText("Run geolocator algorithm on above chosen files.");
    btnRun.setBounds(338, 218, 89, 23);
    frame.getContentPane().add(btnRun);
    btnRun.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          br = GetReader.getUTF8FileReader(txtCusers.getText().trim());
          bw = GetWriter.getFileWriter(txtCusersoutput.getText().trim());
        } catch (FileNotFoundException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        sb = new StringBuilder();
        Tweet t;
        String line = null;
        try {
          txtrMessageBox.setText("Tagging");
          while ((line = br.readLine()) != null) {

            sb.append(line).append("\t");

            t = new Tweet();
            t.setText(line);
            List<String> topo = enparser.parse(t);
            System.out.println(topo.toString());
            HashSet<String> reducedmatch = new HashSet<String>();
            for (String s : topo)
              reducedmatch.add(s.substring(3, s.length() - 3));
            HashMap<String, String[]> result = c.returnBestTopo(topotrie, reducedmatch);
            Iterator<Entry<String, String[]>> i = result.entrySet().iterator();
            while (i.hasNext()) {
              Entry<String, String[]> a = i.next();
              sb.append("[ ").append(a.getKey())
//              .append(",").append(a.getValue()[0]).append(",")
//                      .append(a.getValue()[1]).append(",").append(a.getValue()[2])
                      .append(" ] ");
            }
            sb.append("\n");
          }
          txtrOutputBox.setText(sb.toString());
          bw.write(sb.toString());
          bw.close();
          br.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
    JLabel lblNoteThereShould = new JLabel("Note: The text should not exceed 100MB.");
    lblNoteThereShould.setBounds(20, 192, 278, 14);
    frame.getContentPane().add(lblNoteThereShould);
  }
}
