package com.remeedev.codex;

import javax.swing.*;
import java.awt.event.*;

public class closeFile implements ActionListener {
    public JFrame frame;
    public changeFile cf;
    public void actionPerformed(ActionEvent e){
        if (cf.currentFile == null){
            JOptionPane.showMessageDialog(frame, "No file is currently open!");
            return;
        }
        cf.resolveFiles(cf.currentFile.getName());
        cf.currentFile = null;
    }
}
