package com.remeedev.codex;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class delFile implements ActionListener {
    public JFrame frame;
    public changeFile cf;
    public void actionPerformed(ActionEvent e){
        if (cf.currentFile == null){
            JOptionPane.showMessageDialog(frame, "No file is currently open!");
            return;
        }
        // Evade people deleting files accidentally
        if (!Objects.equals(cf.fileContent.getText(), "")){
            Object[] options = {"Delete", "Cancel"};
            int n = JOptionPane.showOptionDialog(frame,
                "The file still has content!",
                "Deleting File",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
            if (n != 0){
                return;
            }
        }
        cf.fileContent.setText("");
        File deletingFile = new File("./saves/"+cf.currentFile.getName());
        if (deletingFile.exists()){
            deletingFile.delete();
        }
        cf.currentFile.delete();
        cf.currentFile = null;
        cf.resolveFiles();
    }
}
