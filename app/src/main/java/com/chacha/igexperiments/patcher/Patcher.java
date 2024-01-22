package com.chacha.igexperiments.patcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import brut.androlib.exceptions.AndrolibException;
import brut.common.BrutException;
import brut.directory.ExtFile;

public class Patcher {
    private final File apkFile;
    private final ApkUtils apkUtils;
    private final ExperimentsUtils experimentsUtils;
    private WhatToPatch whatToPatch;
    private File smaliToRecompile;
    public Patcher(File apkFile) {
        this.apkFile = apkFile;
        this.apkUtils = new ApkUtils(apkFile);
        this.experimentsUtils = new ExperimentsUtils();
    }

    /**
     * Find the class and method to patch
     */
    public void findWhatToPatch() throws IOException {
        List<Future<?>> futures = new ArrayList<>();

        apkUtils.extractDexFiles();
       // ExecutorService executor = Executors.newFixedThreadPool(apkUtils.getDexFiles().length);

        for(File smaliClass : apkUtils.getDexFiles()){
           /* Future<?> future = executor.submit(() -> {*/
                File decodedSmali;

                try {
                    decodedSmali = apkUtils.decodeSmali(smaliClass);
                } catch (AndrolibException ex) {
                    throw new RuntimeException(ex);
                }

                List<File> f = getFilesCallingDevOptions(decodedSmali);
                if (f.isEmpty()) {
                    System.err.println("\nNo file calling method to enable dev options found in " + decodedSmali.getName());
                } else {
                    try {
                      //  executor.shutdownNow();
                        for(File fileToTryWith : f){
                            this.whatToPatch = experimentsUtils.findWhatToPatch(fileToTryWith);
                            if(whatToPatch != null){
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("\nError while finding what to patch: \n\n" + e.getMessage());
                    }
                }
           /* });

            futures.add(future);*/
        }

        /*for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace(); // Handle exceptions as needed
            }
        }*/

        if(whatToPatch==null) {
            System.err.println("Sorry an error occured: the method to patch was not found. You can try with another Instagram version :/");
            return;
        }

        System.out.println("Class to patch: " + whatToPatch.getClassToPatch());
        System.out.println("Method to patch: " + whatToPatch.getMethodToPatch());
        System.out.println("Argument type: " + whatToPatch.getArgumentType());

        //executor.shutdown(); // Shutdown the executor when done
    }

    /**
     * Patch the apk file
     */
    public void patch() throws BrutException {
        System.out.println("Patching: " + apkFile.getAbsolutePath());

        try {
            findWhatToPatch();
            enableDevOptions(apkUtils);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        apkUtils.compileToApk(apkFile, new ExtFile(getFileToRecompile()));
        System.out.println("Patched successfully !!!!");
        System.out.println("\nYou can use Uber Apk Signer if you want to sign easily your patched apk (https://github.com/patrickfav/uber-apk-signer/releases)");
    }

    /**
     * @return the file containing the call to the method that enable experiments
     */
    public List<File> getFilesCallingDevOptions(File folderToSearchIn) {
        System.out.println("Searching for call to method enabling dev options in " + folderToSearchIn.getName() + "...");
        return FileTextSearch.searchFilesWithText(folderToSearchIn, "const-string v0, \"is_employee\"");
    }


    /**
     * Enable experiments by patching the method
     */
    private void enableDevOptions(ApkUtils apkUtils) throws IOException, InterruptedException {
        System.out.println("Enabling dev options...");
        File fileToPatch = FileTextSearch.findSmaliFile(whatToPatch, apkUtils);
        System.out.println("File to patch: " + fileToPatch.getAbsolutePath());
        setPkgToRecompile(fileToPatch);
        experimentsUtils.makeMethodReturnTrue(fileToPatch, whatToPatch.getMethodToPatch());
        System.out.println("Dev options enabled successfully.");
    }


    /**
     * Set smali folder to recompile to .dex
     * @param file the edited smali file
     */
    private void setPkgToRecompile(File file){
        File currentFile = file.getAbsoluteFile();

        // Iterate until we reach the root directory
        while (!currentFile.getName().startsWith(ApkUtils.DEX_BASE_NAME)) {
            currentFile = currentFile.getParentFile();
        }
        smaliToRecompile = currentFile;
    }

    /**
     * @return the file to recompile to .dex
     */
    public File getFileToRecompile(){
        return smaliToRecompile;
    }
}
