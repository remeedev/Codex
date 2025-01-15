package com.remeedev.codex;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.event.*;

public class newFile implements ActionListener {
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
