package com.chacha.igexperiments.patcher;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import brut.androlib.exceptions.AndrolibException;
import brut.androlib.src.SmaliBuilder;
import brut.androlib.src.SmaliDecoder;
import brut.common.BrutException;
import brut.directory.ExtFile;

public class ApkUtils {
    private File out;
    private final File apkFile;

    public static final String DEX_BASE_NAME = "classes";

    public ApkUtils(File apkFile){
        this.apkFile = apkFile;
    }

    /**
     * Decompile an apk file
     */
    public void extractDexFiles() throws IOException {
        out = new File(apkFile.getPath() + ".out");
        if(out.exists()) {
            return;
        }

        ZipFile zipFile = new ZipFile(apkFile);
        for(FileHeader fileHeader : zipFile.getFileHeaders()){
            if(fileHeader.getFileName().endsWith(".dex")){

                zipFile.extractFile(fileHeader, out.getAbsolutePath());
            }


        }
    }

    public File[] getDexFiles(){
        return out.listFiles((dir, name) -> name.endsWith(".dex"));
    }

    public File decodeSmali(File dexFile) throws AndrolibException {
        File decodedSmali = new File(getOutDir().getAbsolutePath() + File.separator + dexFile.getName().replace(".dex", ""));

        if(decodedSmali.exists()) {
            System.out.println(decodedSmali.getName() + " already exists, skipping decompilation");
            return decodedSmali;
        }

        System.out.println("Decompiling " + dexFile.getName() + " to smali...");
        SmaliDecoder.decode(apkFile, decodedSmali, dexFile.getName(), false, 0);
        return decodedSmali;
    }

     /**
      * @return the out directory where the apk was decompiled
      */
     public File getOutDir() {
         return out;
     }

     public File[] getClassesFolders(){
         return this.getOutDir().listFiles((dir, name) -> name.startsWith(ApkUtils.DEX_BASE_NAME) && !name.endsWith(".dex"));
     }

        /**
        * Compile a smali directory to dex
        */
     private void compileSmaliToDex(ExtFile smaliDir, File dexFile) throws BrutException {
        System.out.println("Compiling " + smaliDir + " to dex...");
        SmaliBuilder.build(smaliDir, dexFile, 0);
     }

    /**
     * Compile a smali directory to dex and copy it to the apk
     * @param apkFile the apk to copy the dex file to
     * @param smaliDir the classes directory to compile
     */
     public void compileToApk(File apkFile, ExtFile smaliDir) throws BrutException {
        File dexFile = new File(smaliDir + ".dex");
         compileSmaliToDex(smaliDir, dexFile);
         try {
             copyCompiledFileToApk(apkFile, dexFile);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
         dexFile.delete();
     }


    /**
     * Copy a compiled dex file to the apk
     * @param apkFile the apk to copy the dex file to
     * @param dexFile the dex file to copy
     */
    private void copyCompiledFileToApk(File apkFile, File dexFile) throws IOException {
        System.out.println("Copying compiled " + dexFile.getName() + " to APK...");
        File newApk = new File(apkFile.getAbsolutePath().replace(".apk", "-patched.apk"));
        if(newApk.exists())
            newApk.delete();
        Files.copy(apkFile.toPath(), newApk.toPath());

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(CompressionMethod.STORE);
        new ZipFile(newApk).addFile(dexFile, zipParameters);

        System.out.println("Compiled " + dexFile.getName() + " copied to APK successfully.");
    }
}
