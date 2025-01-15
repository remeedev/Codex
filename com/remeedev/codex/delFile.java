package com.remeedev.codex;

import javax.swing.*;
import java.awt.event.*;

public class delFile implements ActionListener {
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
