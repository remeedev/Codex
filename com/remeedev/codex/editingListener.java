package com.remeedev.codex;

import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;

public class editingListener implements DocumentListener {
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
