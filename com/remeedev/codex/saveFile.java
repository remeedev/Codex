package com.remeedev.codex;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.util.*;

public class saveFile implements ActionListener{
    public changeFile cf;
    public JFrame frame;

    public Action saveAction = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                saveCurrentFile();
            }
        };


    public void saveCurrentFile(){
        if (cf.currentFile == null){
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "html", "css", "js", "py", "cpp", "c", "java", "txt", "md");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION){
                File unsaved = new File("./saves/unsaved.txt");
                if (!unsaved.exists()){
                    return;
                }
                bufferReader br = new bufferReader();
                ArrayList<String> fileInfo = br.readBuffer("unsaved.txt");
                String content = fileInfo.get(0);
                if (!chooser.getSelectedFile().exists()){
                    try{
                        chooser.getSelectedFile().createNewFile();            

                        FileWriter fw = new FileWriter(chooser.getSelectedFile().getAbsolutePath());
                        fw.write(content);
                        fw.close();
                        if (unsaved.exists()){
                            unsaved.delete();
                        }
                        File unsavedBuff = new File("./saves/unsaved.txt");
                        if (unsavedBuff.exists()){
                            unsavedBuff.delete();
                        }
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
                    if (unsaved.exists()){
                        unsaved.delete();
                    }
                    File unsavedBuff = new File("./saves/unsaved.txt");
                    if (unsavedBuff.exists()){
                        unsavedBuff.delete();
                    }                }
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
