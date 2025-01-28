package com.remeedev.codex;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.event.*;

public class openFile implements ActionListener{
    public JFrame frame;
    public changeFile fileHandles;

    public Action openAction = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                openFileXX();
            }
        };

    public void openFileXX(){
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
    public void actionPerformed(ActionEvent e){
        openFileXX();
    }
}
