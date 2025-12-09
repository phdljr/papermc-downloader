package me.phdljr.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StartFileCreator {

    public void createStartFile(File jarFile, String memory) throws IOException {
        createBatchFile(jarFile, memory);
        createEulaFile(jarFile.getParentFile());
    }

    private void createEulaFile(File folder) throws IOException {
        File eula = new File(folder, "eula.txt");
        String content = "eula=true";
        try(var bw = new BufferedWriter(new FileWriter(eula))){
            bw.write(content);
        }
    }

    private void createBatchFile(File jarFile, String memory) throws IOException {
        File bat = new File(jarFile.getParentFile(), "start.bat");
        String content = "@echo off\n" + "java -Xms" + (Integer.parseInt(memory)/2) + "G " + "-Xmx" + memory + "G -jar " + jarFile.getName() + " nogui";
        try(var bw = new BufferedWriter(new FileWriter(bat))){
            bw.write(content);
        }
    }
}
