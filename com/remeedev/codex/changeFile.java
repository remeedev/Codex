package com.remeedev.codex;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class changeFile implements ActionListener {
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

    public void resolveFiles(String name){
        ArrayList<File> newFileList = new ArrayList<File>();
        ArrayList<JButton> newFileObjs = new ArrayList<JButton>();
        for (int i = 0; i < fileList.size(); i++){
            if (fileList.get(i).exists() && !Objects.equals(fileList.get(i).getName(), name)){
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
                    reader.close();
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
