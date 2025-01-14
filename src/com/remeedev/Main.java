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
        try{
            File newSave = new File("./saves/"+file.getName());
            if (!newSave.exists()){
                newSave.createNewFile();
            }
            content = file.getAbsolutePath() + "\n" + content;
            FileWriter writer = new FileWriter("./saves/" + file.getName());
            writer.write(content);
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }
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
    public changeFile cf;
    public File fileBuff;
    public String[] buffer = new String[25];
    public int position = 0;
    private int ignore = 0;

    public Action undoAction = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                if (position == buffer.length-1 || buffer[position+1] == null){
                    return;
                }
                position++;
                ignore = 2;
                cf.fileContent.setText(buffer[position]);
            }
        };

    public Action redoAction = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                if (position-1 < 0){
                    return;
                }
                position--;
                ignore = 2;
                cf.fileContent.setText(buffer[position]);
            }
        };


    public void updateLines(DocumentEvent e){
        if (ignore > 0){
            ignore--;
            return;
        }
        try {
            for (int i = 0; i < buffer.length-position; i++){
                if (buffer[position+i] == null){
                    break;
                }
                buffer[i] = buffer[position+i];
            }
            position = 0;
            for (int i = 0; i < buffer.length; i++){
                if (i == buffer.length-1){
                    continue;
                }
                buffer[buffer.length-1-i] = buffer[buffer.length-2-i];
            }
            String text = e.getDocument().getText(0, e.getDocument().getLength());
            buffer[0] = text;
            if (cf.currentFile != null){
                if (fileBuff == null){
                    fileBuff = cf.currentFile;
                    return;
                }
                if (!Objects.equals(fileBuff.getName(), cf.currentFile.getName())){
                    fileBuff = cf.currentFile;
                    return;
                }
                bufferReader br = new bufferReader();
                br.createBuffer(cf.currentFile, text);
            }else{
                bufferReader br = new bufferReader();
                try{
                    File unsaved = new File("unsaved.txt");
                    if (!unsaved.exists()){
                        unsaved.createNewFile();
                    }
                    br.createBuffer(unsaved, text);
                }catch(IOException err){
                    err.printStackTrace();
                }
            }
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

    public void fixButtons(){
        if (currentFile == null){
            return;
        }
        for (int i = 0; i < fileList.size(); i++){
            if (Objects.equals(fileList.get(i).getName(), currentFile.getName())){
                fileObjs.get(i).setBackground(Color.LIGHT_GRAY);
            }else{
                fileObjs.get(i).setBackground(Color.GRAY);
            }
        }
    }

    public void resolveFiles(){
        ArrayList<File> newFileList = new ArrayList<File>();
        ArrayList<JButton> newFileObjs = new ArrayList<JButton>();
        for (int i = 0; i < fileList.size(); i++){
            if (fileList.get(i).exists()){
                newFileList.add(fileList.get(i));
                newFileObjs.add(fileObjs.get(i));
                fileObjs.get(i).setBackground(Color.GRAY);
            }else{
                fileListObj.remove(fileObjs.get(i));
            }
        }
        fileList = newFileList;
        fileObjs = newFileObjs;
        if (fileList.size() == 0){
            fileListObj.remove(defaultButt);
            fileListObj.add(defaultButt);
        }else{
            if (currentFile == null){
                currentFile = fileList.get(fileList.size()-1);
                fixButtons();
            }
        }
        fileListObj.revalidate();
        fileListObj.repaint();
    }

    public void loadFile(String fileName){
        for (int i = 0; i < fileList.size(); i++){
            if (Objects.equals(fileList.get(i).getName(), fileName)){
                currentFile = fileList.get(i);
                fixButtons();
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
        File unsaved = new File("unsaved.txt");
        File unsavedBuff = new File("./saves/unsaved.txt");
        if (unsaved.exists()){
            unsaved.delete();
        }
        if (unsavedBuff.exists()){
            unsavedBuff.delete();
        }
        fileListObj.remove(defaultButt);
        JButton fileButton = new JButton(file.getName());
        fileButton.addActionListener(this);
        fileListObj.add(fileButton);
        fileObjs.add(fileButton);
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
        JFileChooser chooser = new JFileChooser(fileHandles.currentFile);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "html", "css", "js", "py", "cpp", "c", "java", "txt", "md");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION){
            if (fileHandles.inFiles(chooser.getSelectedFile().getName())){
                JOptionPane.showMessageDialog(frame, "File is already open!");
            }else{
                // Handle files that don't exist
                if (chooser.getSelectedFile().exists()){
                    fileHandles.addFile(chooser.getSelectedFile());
                }
            }
        }
    }
}

class newFile implements ActionListener {
    public JFrame frame;
    public changeFile cf;
    public void actionPerformed(ActionEvent e){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "html", "css", "js", "py", "cpp", "c", "java", "txt", "md");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION){
            if (!chooser.getSelectedFile().exists()){
                try{
                    chooser.getSelectedFile().createNewFile();            
                    cf.addFile(chooser.getSelectedFile());
                } catch(IOException err){
                    err.printStackTrace();
                }
            }else{
                JOptionPane.showMessageDialog(frame, "File already exists!");
            }
        }
    }
}

class delFile implements ActionListener {
    public JFrame frame;
    public changeFile cf;
    public void actionPerformed(ActionEvent e){
        if (cf.currentFile == null){
            JOptionPane.showMessageDialog(frame, "No file is currently open!");
            return;
        }
        cf.currentFile.delete();
        cf.currentFile = null;
        cf.resolveFiles();
    }
}

class saveFile implements ActionListener{
    public changeFile cf;
    public JFrame frame;
    public void saveCurrentFile(){
        if (cf.currentFile == null){
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "html", "css", "js", "py", "cpp", "c", "java", "txt", "md");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION){
                bufferReader br = new bufferReader();
                ArrayList<String> fileInfo = br.readBuffer("unsaved.txt");
                String content = fileInfo.get(0);
                if (!chooser.getSelectedFile().exists()){
                    try{
                        chooser.getSelectedFile().createNewFile();            

                        FileWriter fw = new FileWriter(chooser.getSelectedFile().getAbsolutePath());
                        fw.write(content);
                        fw.close();
                        File unsaved = new File("unsaved.txt");
                        unsaved.delete();
                        File unsavedBuff = new File("./saves/unsaved.txt");
                        unsavedBuff.delete();
                    } catch(IOException err){
                        err.printStackTrace();
                    }
                }else{
                    if (cf.inFiles(chooser.getSelectedFile().getName())){
                        JOptionPane.showMessageDialog(frame, "File is already open!");
                        return;
                    }
                    Object[] options = {"Overwrite", "Cancel"};
                    int n = JOptionPane.showOptionDialog(frame,
                        "The file already exists!",
                        "File already exists",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                    if (n == 0){
                        try{
                        FileWriter fw = new FileWriter(chooser.getSelectedFile().getAbsolutePath());
                        fw.write(content);
                        fw.close();
                        } catch (IOException err){
                            err.printStackTrace();
                        }
                    }
                    File unsaved = new File("unsaved.txt");
                    unsaved.delete();
                    File unsavedBuff = new File("./saves/unsaved.txt");
                    unsavedBuff.delete();
                }
                cf.addFile(chooser.getSelectedFile());
            }else{
                this.saveCurrentFile();
            }
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
                for (File file:files){
                    file.delete();
                }
                File file = new File("unsaved.txt");
                if (file.exists()){
                    file.delete();
                }
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
        newFile newFileHandle = new newFile();
        newFileHandle.frame = frame;
        newFileHandle.cf = fileChanger;
        newFileButton.addActionListener(newFileHandle);
        topBar.add(newFileButton);

        // Creating new file button
        JButton delFileButton = new JButton("Delete File");
        delFile delFileHandle = new delFile();
        delFileHandle.frame = frame;
        delFileHandle.cf = fileChanger;
        delFileButton.addActionListener(delFileHandle);
        topBar.add(delFileButton);


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
        eL.cf = fileChanger;
        currentText.getDocument().addDocumentListener(eL);

        // Setting up Undo and Redo key
        InputMap tMap = currentText.getInputMap();
        String UNDO = "Undo Action Key";
        String REDO = "Redo Action Key";
        currentText.getActionMap().put(UNDO, eL.undoAction);
        currentText.getActionMap().put(REDO, eL.redoAction);
        tMap.put(KeyStroke.getKeyStroke("control Z"), UNDO);
        tMap.put(KeyStroke.getKeyStroke("control Y"), REDO);

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
