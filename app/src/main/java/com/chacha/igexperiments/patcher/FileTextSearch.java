package com.chacha.igexperiments.patcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
public class FileTextSearch {

    public static List<File> searchFilesWithText(File directory, String searchText) {
        List<File> result = new ArrayList<>();

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (Thread.interrupted()) {
                    //System.out.println("Stopping file search in " + directory.getParentFile().getName());
                    return result;
                }

                if (file.isDirectory() && file.getName().equals("X")) {
                    result.addAll(searchFilesWithText(file, searchText));
                } else if (file.isFile() && containsText(file, searchText)) {
                    result.add(file);
                }
            }
        }

        return result;
    }

    private static boolean containsText(File file, String searchText) {

        try {
            String content = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8).toString();
           // String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            return content.contains(searchText);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //HAHAHA HELP windows is so trash the name is case insensitive so for example 19M.smali is same as 19m.smali :))))
    public static File similarFileExists(String targetFileName, File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().startsWith(targetFileName)) {
                        return f;
                    }
                }
            }
        }

        return null;
    }
    public static File findSmaliFile(WhatToPatch whatToPatch, ApkUtils apkUtils) throws FileNotFoundException {
        String path = whatToPatch.getClassToPatch();
        File[] classesFolders = apkUtils.getClassesFolders();

        if(classesFolders == null){
            throw new RuntimeException("No classes folder not found in " + apkUtils.getOutDir().getAbsolutePath());
        }

        /*
         * I could use exists() method from File class but on Windows the name is case insensitive
         * Sometimes the class name to patch can be for example 19m.1.smali, a 19M.smali file exists too and on Windows it will use 19M.smali file for sure :D
         */

        for(File folder : classesFolders){
            File folderToSearchIn = new File(folder + File.separator + path.substring(0, path.lastIndexOf(".")));
            String fileNameToSearch = path.substring(path.lastIndexOf(".") + 1);
            File fileToPatch = similarFileExists(fileNameToSearch, folderToSearchIn);
            if(fileToPatch!=null && fileToPatch.exists()){
                return fileToPatch;
            }
        }

        throw new FileNotFoundException("Smali file to patch not found!");
    }
}
