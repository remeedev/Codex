package com.remeedev.codex;

import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.util.*;
import java.awt.*;
import javax.swing.event.*;

public class editingListener implements DocumentListener {
    public JTextPane lines;
    public changeFile cf;
    public Font font;
    public int fontSize;
    public SimpleAttributeSet attr;
    public File fileBuff;
    public String[] buffer = new String[25];
    public int position = 0;
    private int ignore = 0;
    private int bufferSize = 0;

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

    public void updateFont(){
        StyleConstants.setFontSize(attr, fontSize);
        StyledDocument sD = cf.lines.getStyledDocument();
        sD.setCharacterAttributes(0, sD.getLength(), attr, true);
        cf.fileContent.setFont(new Font("monospaced", 0, fontSize));
    }

    public Action increaseFont = new AbstractAction(){
        public void actionPerformed(ActionEvent e){
            fontSize++;
            fontSize++;
            updateFont();
        }
    };

    public Action decreaseFont = new AbstractAction(){
        public void actionPerformed(ActionEvent e){
            fontSize--;
            fontSize--;
            if (fontSize <= 0){
                fontSize = 2;
            }
            updateFont();
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
            bufferReader br = new bufferReader();
            if (cf.currentFile != null){
                if (fileBuff == null){
                    fileBuff = cf.currentFile;
                }
                if (!Objects.equals(fileBuff.getName(), cf.currentFile.getName())){
                    fileBuff = cf.currentFile;
                }
                br.createBuffer(cf.currentFile, text);
            }else{
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
            File unsaved = new File("unsaved.txt");
            File finalCompare;
            if (unsaved.exists()){
                finalCompare = unsaved;
            }else{
                finalCompare = cf.currentFile;
            }
            String[] lineList = (text+"hello, world!").split(System.lineSeparator());
            int lineCount = lineList.length;
            if (bufferSize == lineCount){
                System.out.println("aaa");
                bufferSize = lineCount;
                return;
            }
            String read = "";
            try{
                Scanner reader = new Scanner(finalCompare);
                while (reader.hasNextLine()){
                    read = read+reader.nextLine()+"\n";
                }
                reader.close();
            }catch(FileNotFoundException err){
                err.printStackTrace();
            }
            String[] ogLineList = (read+"TIHI").split(System.lineSeparator());
            if (Objects.equals(read, text) && ogLineList.length == lineCount){
                if (Objects.equals(finalCompare.getName(), "unsaved.txt")){
                    finalCompare.delete();
                }
                File unsavedBuff = new File("./saves/"+finalCompare.getName());
                unsavedBuff.delete();
            }
            String counting = "";
            for (int i = 1; i-1 < lineCount; i++){
                counting = counting + String.valueOf(i) + "\n";
            }
            lines.setText(counting);
            updateFont();
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
