package com.chacha.igexperiments;

public class InfoIGVersion {
    private final String version;
    private final String classToHook, methodToHook;
    private final String url;

    public InfoIGVersion(String version, String classToHook, String methodToHook, String url) {
        super();
        this.version = version;
        this.classToHook = classToHook;
        this.methodToHook = methodToHook;
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public String getClassToHook() {
        return classToHook;
    }

    public String getMethodToHook(){
        return methodToHook;
    }

    public String getUrl() {
        return url;
    }

    public String toString() {
        return this.getVersion();
    }
}
