package com.remeedev.codex;

import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class Main{
    public static void main(String[] args){
        // Checking for unsaved files
        File saveDir = new File("./saves");
        File[] files = saveDir.listFiles();
        
        for (File file : files){
            System.out.println(file.getName());
        }
            
        // Creating the frame
        JFrame frame = new JFrame();
        wl winl = new wl();
        winl.frame = frame;
        frame.addWindowListener(winl);
        // Setting up frame
        frame.setSize(800, 450);
        frame.setLayout(new BorderLayout());
        frame.setTitle("Code Editor");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Creating top panel
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        // Creating open file button
        JButton openButton = new JButton("Open File");
        openButton.setBackground(Color.LIGHT_GRAY);
        openFile openFileHandle = new openFile();
        openFileHandle.frame = frame;
        openButton.addActionListener(openFileHandle);
        topBar.add(openButton);

        // Referenced by save button
        changeFile fileChanger = new changeFile();

        // Creating save file button
        JButton saveButton = new JButton("Save File");
        saveButton.setBackground(Color.LIGHT_GRAY);
        saveFile saveFileHandle = new saveFile();
        saveFileHandle.cf = fileChanger;
        winl.handle = saveFileHandle;
        saveButton.addActionListener(saveFileHandle);
        topBar.add(saveButton);

        // Creating new file button
        JButton newFileButton = new JButton("New File");
        newFileButton.setBackground(Color.LIGHT_GRAY);
        newFile newFileHandle = new newFile();
        newFileHandle.frame = frame;
        newFileHandle.cf = fileChanger;
        newFileButton.addActionListener(newFileHandle);
        topBar.add(newFileButton);

        // Creating new file button
        JButton delFileButton = new JButton("Delete File");
        delFileButton.setBackground(Color.LIGHT_GRAY);
        delFile delFileHandle = new delFile();
        delFileHandle.frame = frame;
        delFileHandle.cf = fileChanger;
        delFileButton.addActionListener(delFileHandle);
        topBar.add(delFileButton);

        // Creating new file button
        JButton closeFileButton = new JButton("Close File");
        closeFileButton.setBackground(Color.LIGHT_GRAY);
        closeFile closeFileHandle = new closeFile();
        closeFileHandle.frame = frame;
        closeFileHandle.cf = fileChanger;
        closeFileButton.addActionListener(closeFileHandle);
        topBar.add(closeFileButton);

        // Adding topbar to the window
        frame.add(topBar, BorderLayout.PAGE_START);

        // Creating text editing panel
        JPanel middle = new JPanel(new BorderLayout());
        
        // Creating open files
        JPanel openFiles = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton defFile = new JButton("(new file)");
        defFile.setBackground(Color.LIGHT_GRAY);
        openFileHandle.fileHandles = fileChanger;
        fileChanger.fileListObj = openFiles;
        fileChanger.defaultButt = defFile;
        defFile.addActionListener(fileChanger);
        openFiles.add(defFile);
        middle.add(openFiles, BorderLayout.PAGE_START);

        int fontSize = 12;
        // Creating the font and applying to the text fields
        Font font = new Font("monospaced", 0, fontSize);
        // Creating actual text editor
        JTextPane lines = new JTextPane();
        lines.setEditable(false);
        lines.setBackground(Color.lightGray);
        // Code to align line numbers to right
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setAlignment(attr, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setFontSize(attr, fontSize);
        StyleConstants.setFontFamily(attr, "monospaced");
        lines.setParagraphAttributes(attr, true);
        JTextPane currentText = new JTextPane() {
            public boolean getScrollableTracksViewportWidth() {
                if( getSize().width < getParent().getSize().width ) {
                    return true;
                }
                else {
                    return false;
                }
            }
  
            public void setSize( Dimension d ) {
                if( d.width < getParent().getSize().width ) {
                    d.width = getParent().getSize().width;
                }
                super.setSize( d );
            }
        }; 
        currentText.setFont(font);
        currentText.setCaretColor(Color.WHITE);
        currentText.setBackground(Color.DARK_GRAY);
        currentText.setForeground(Color.WHITE);
        JScrollPane jsp = new JScrollPane(currentText);
        jsp.setRowHeaderView(lines);
        fileChanger.fileContent = currentText;
        fileChanger.lines = lines;
        middle.add(jsp, BorderLayout.CENTER);
        
        // Updating listener
        editingListener eL = new editingListener();
        eL.fontSize = fontSize;
        eL.attr = attr;
        eL.lines = lines;
        eL.cf = fileChanger;
        eL.font = font;
        currentText.getDocument().addDocumentListener(eL);

        // Setting up Undo and Redo key
        InputMap tMap = currentText.getInputMap();
        String UNDO = "Undo Action Key";
        String REDO = "Redo Action Key";
        currentText.getActionMap().put(UNDO, eL.undoAction);
        currentText.getActionMap().put(REDO, eL.redoAction);
        tMap.put(KeyStroke.getKeyStroke("control Z"), UNDO);
        tMap.put(KeyStroke.getKeyStroke("control Y"), REDO);

        // Setting up increaseFont and decreaseFont
        String IFont = "Increase Font Key";
        String DFont = "Decrease Font Key";
        currentText.getActionMap().put(IFont, eL.increaseFont);
        currentText.getActionMap().put(DFont, eL.decreaseFont);
        tMap.put(KeyStroke.getKeyStroke(107, InputEvent.CTRL_DOWN_MASK), IFont);
        tMap.put(KeyStroke.getKeyStroke(109, InputEvent.CTRL_DOWN_MASK), DFont);

        // Adding basic key strokes, save and open
        String saveF = "Save File Key";
        String openF = "Open File Key";
        currentText.getActionMap().put(saveF, saveFileHandle.saveAction);
        currentText.getActionMap().put(openF, openFileHandle.openAction);
        tMap.put(KeyStroke.getKeyStroke(79, InputEvent.CTRL_DOWN_MASK), openF);
        tMap.put(KeyStroke.getKeyStroke(83, InputEvent.CTRL_DOWN_MASK), saveF);

        // Adding middle panel to center of window
        frame.add(middle, BorderLayout.CENTER);
 
        // Restoration of lost files
        if (files.length != 0){
            Object[] options = {"Restore", "Delete"};
            int n = JOptionPane.showOptionDialog(frame,
                "Unsaved files were found, what would you like to do with them?",
                "Found some files!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            if (n == 0){
                bufferReader br = new bufferReader();
                br.cf = fileChanger;
                for (File file : files){
                    br.restoreBuffer(file.getName());
                }
            }else{
                System.out.println("Deleting files...");
            }
        }       

        // Showing the frame
        frame.setVisible(true);
    }
}
