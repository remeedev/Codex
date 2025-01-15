package com.remeedev.codex;

import java.io.*;
import javax.swing.*;
import java.awt.event.*;

public class wl implements WindowListener{
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
