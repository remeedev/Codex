package com.remeedev.codex;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;

class bufferReader {
    public changeFile cf;

    public void saveBuffer(String fileName){
        try{
            ArrayList<String> fileInfo = readBuffer(fileName);
            String fileText = fileInfo.get(0);
            String path = fileInfo.get(1);
            if (fileInfo.size() == 0){
                return;
            }
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(fileText);
            fileWriter.close();
            File file = new File("./saves/"+fileName);
            file.delete();
        } catch(FileNotFoundException e){
            System.out.println("Buffer not found!");
            e.printStackTrace();
        } catch(IOException e){
            System.out.println("Writing error!");
            e.printStackTrace();
        }
    }
    public void restoreBuffer(String fileName){
        ArrayList<String> fileInfo = readBuffer(fileName);
        String fileText = fileInfo.get(0);
        String path = fileInfo.get(1);
        if (fileInfo.size() == 0){
            return;
        }
        File actualFile = new File(path);
        cf.addFile(actualFile);
        cf.fileContent.setText(fileText);
    }
    public ArrayList<String> readBuffer(String fileName){
        ArrayList<String> output = new ArrayList<String>();
        try{
            String fileText = "";
            File file = new File("./saves/"+fileName);
            Scanner reader = new Scanner(file);
            int lineCount = 0;
            String path = "";
            while (reader.hasNextLine()){
                if (lineCount == 0){
                    lineCount++;
                    path = reader.nextLine();
                    continue;
                }
                fileText = fileText + reader.nextLine() + "\n";
            }
            // Output shaped in form [content, path]
            output.add(fileText);
            output.add(path);
            return output;
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return output;
    }
    public void createBuffer(File file, String content){

    }
    public boolean checkBuffer(String fileName){
        File savesDir = new File("./saves");
        File[] files = savesDir.listFiles();
        if (files.length == 0){
            return false;
        }
        for (File file : files){
            if (Objects.equals(file.getName(), fileName)){
                return true;
            }
        }
        return false;
    }
}

class editingListener implements DocumentListener {
    public JTextPane lines;

    public void updateLines(DocumentEvent e){
        try {
            String text = e.getDocument().getText(0, e.getDocument().getLength());
            String[] lineList = text.split("\r\n|\r|\n");
            int lineCount = lineList.length;
            String counting = "";
            for (int i = 1; i-1 < lineCount; i++){
                counting = counting + String.valueOf(i) + "\n";
            }
            lines.setText(counting);
        } catch (BadLocationException err) {
            err.printStackTrace();
        }
    }

    public void changedUpdate(DocumentEvent e){

    }

    public void insertUpdate(DocumentEvent e){
        updateLines(e);
    }

    public void removeUpdate(DocumentEvent e){
        updateLines(e);
    }
}

class changeFile implements ActionListener {
    private ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<JButton> fileObjs = new ArrayList<JButton>();
    public File currentFile;
    public JTextPane fileContent;
    public JTextPane lines;
    public JPanel fileListObj;
    public JButton defaultButt;
    private bufferReader br = new bufferReader();

    public void loadFile(String fileName){
        for (int i = 0; i < fileList.size(); i++){
            if (Objects.equals(fileList.get(i).getName(), fileName)){
                currentFile = fileList.get(i);
                String fileText = "";
                try{
                    File file = fileList.get(i);
                    Scanner reader = new Scanner(file);
                    while (reader.hasNextLine()){
                        fileText = fileText + reader.nextLine() + "\n";
                    }
                    fileContent.setText(fileText);
                } catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public void actionPerformed(ActionEvent e){
        String fileName = e.getActionCommand();
        if (br.checkBuffer(fileName)){
            fileContent.setText(br.readBuffer(fileName).get(0));
            return;
        }
        loadFile(fileName);
    }

    public void removeFile(String fileName){
        for (int i = 0; i < fileList.size(); i++){
            if (Objects.equals(fileList.get(i).getName(), fileName)){
                fileList.remove(i);
                return;
            }
        }
    }

    public boolean inFiles(String fileName){
        for (int i = 0; i < fileList.size(); i++){
            if (Objects.equals(fileList.get(i).getName(), fileName)){
                return true;
            }
        }
        return false;
    }

    public void openFile(File file){
        fileListObj.remove(defaultButt);
        JButton fileButton = new JButton(file.getName());
        fileButton.addActionListener(this);
        fileListObj.add(fileButton);
        fileListObj.revalidate();
        fileListObj.repaint();
        loadFile(file.getName());
    }

    public void addFile(File file){
        fileList.add(file);
        this.openFile(file);
    }
}

class openFile implements ActionListener{
    public JFrame frame;
    public changeFile fileHandles;

    public void actionPerformed(ActionEvent e){
        // Adding the file chooser
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "html", "css", "js", "py", "cpp", "c", "java", "txt", "md");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION){
            if (fileHandles.inFiles(chooser.getSelectedFile().getName())){
                System.out.println("File already open!");
            }else{
                fileHandles.addFile(chooser.getSelectedFile());
            }
        }
    }
}

class saveFile implements ActionListener{
    public changeFile cf;
    public void saveCurrentFile(){
        if (cf.currentFile == null){
            return;
        }
        bufferReader br = new bufferReader();
        if (br.checkBuffer(cf.currentFile.getName())){
            br.saveBuffer(cf.currentFile.getName());
        } 
    }
    public void actionPerformed(ActionEvent e){
        saveCurrentFile();
    }
}

class wl implements WindowListener{
    public JFrame frame;
    public saveFile handle;
    public void windowOpened(WindowEvent e) {
    }
    public void windowClosing(WindowEvent e) {
        File saveDir = new File("./saves");
        File[] files = saveDir.listFiles();
        if (files.length != 0){
            Object[] options = {"Save", "Don't Save", "Cancel"};
            int n = JOptionPane.showOptionDialog(frame,
                "There are still unsaved changes!",
                "Unsaved changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);
            if (n == 2 || n == -1){
                return;
            }
            if (n == 0){
                handle.saveCurrentFile();
            }
            if (n == 1){
                System.out.println("Exiting without saving...");
            }
        }
        frame.dispose();
    }
    public void windowClosed(WindowEvent e) {
    }
    public void windowIconified(WindowEvent e) {
    }
    public void windowDeiconified(WindowEvent e) {
    }
    public void windowActivated(WindowEvent e) {
    }
    public void windowDeactivated(WindowEvent e) {
    }
}

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
        openFile openFileHandle = new openFile();
        openFileHandle.frame = frame;
        openButton.addActionListener(openFileHandle);
        topBar.add(openButton);

        // Referenced by save button
        changeFile fileChanger = new changeFile();

        // Creating save file button
        JButton saveButton = new JButton("Save File");
        saveFile saveFileHandle = new saveFile();
        saveFileHandle.cf = fileChanger;
        winl.handle = saveFileHandle;
        saveButton.addActionListener(saveFileHandle);
        topBar.add(saveButton);

        // Creating new file button
        JButton newFileButton = new JButton("New File");
        topBar.add(newFileButton);

        // Adding topbar to the window
        frame.add(topBar, BorderLayout.PAGE_START);

        // Creating text editing panel
        JPanel middle = new JPanel(new BorderLayout());
        
        // Creating open files
        JPanel openFiles = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton defFile = new JButton("(new file)");
        openFileHandle.fileHandles = fileChanger;
        fileChanger.fileListObj = openFiles;
        fileChanger.defaultButt = defFile;
        defFile.addActionListener(fileChanger);
        openFiles.add(defFile);
        middle.add(openFiles, BorderLayout.PAGE_START);

        // Creating actual text editor
        JTextPane lines = new JTextPane();
        lines.setEditable(false);
        lines.setBackground(Color.lightGray);
        // Code to align line numbers to right
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setAlignment(attr, StyleConstants.ALIGN_RIGHT);
        lines.setParagraphAttributes(attr, true);
        JTextPane currentText = new JTextPane();
        JScrollPane jsp = new JScrollPane(currentText);
        jsp.setRowHeaderView(lines);
        fileChanger.fileContent = currentText;
        fileChanger.lines = lines;
        middle.add(jsp, BorderLayout.CENTER);
        
        // Updating listener
        editingListener eL = new editingListener();
        eL.lines = lines;
        currentText.getDocument().addDocumentListener(eL);

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
