package com.remeedev.codex;

import java.util.*;
import java.io.*;

public class bufferReader {
    public changeFile cf;

    public void saveBuffer(String fileName){
        try{
            ArrayList<String> fileInfo = readBuffer(fileName);
            String fileText = fileInfo.get(0);
            String path = fileInfo.get(1);
            if (fileInfo.size() == 0){
                return;
            }
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(fileText);
            fileWriter.close();
            File file = new File("./saves/"+fileName);
            file.delete();
        } catch(FileNotFoundException e){
            System.out.println("Buffer not found!");
            e.printStackTrace();
        } catch(IOException e){
            System.out.println("Writing error!");
            e.printStackTrace();
        }
    }
    public void restoreBuffer(String fileName){
        ArrayList<String> fileInfo = readBuffer(fileName);
        String fileText = fileInfo.get(0);
        String path = fileInfo.get(1);
        if (fileInfo.size() == 0){
            return;
        }
        File actualFile = new File(path);
        cf.addFile(actualFile);
        cf.fileContent.setText(fileText);
    }
    public ArrayList<String> readBuffer(String fileName){
        ArrayList<String> output = new ArrayList<String>();
        try{
            String fileText = "";
            File file = new File("./saves/"+fileName);
            Scanner reader = new Scanner(file);
            int lineCount = 0;
            String path = "";
            while (reader.hasNextLine()){
                if (lineCount == 0){
                    lineCount++;
                    path = reader.nextLine();
                    continue;
                }
                fileText = fileText + reader.nextLine() + "\n";
            }
            reader.close();
            // Output shaped in form [content, path]
            output.add(fileText);
            output.add(path);
            return output;
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return output;
    }
    public void createBuffer(File file, String content){
        try{
            File newSave = new File("./saves/"+file.getName());
            if (!newSave.exists()){
                newSave.createNewFile();
            }
            content = file.getAbsolutePath() + "\n" + content;
            FileWriter writer = new FileWriter("./saves/" + file.getName());
            writer.write(content);
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public boolean checkBuffer(String fileName){
        File savesDir = new File("./saves");
        File[] files = savesDir.listFiles();
        if (files.length == 0){
            return false;
        }
        for (File file : files){
            if (Objects.equals(file.getName(), fileName)){
                return true;
            }
        }
        return false;
    }
}
