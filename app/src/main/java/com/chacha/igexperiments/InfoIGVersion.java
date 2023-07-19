package com.chacha.igexperiments;

public class InfoIGVersion {
    private final String version;
    private final String classToHook;
    private final String url;

    public InfoIGVersion(String version, String classToHook, String url) {
        super();
        this.version = version;
        this.classToHook = classToHook;
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public String getClassToHook() {
        return classToHook;
    }

    public String getUrl() {
        return url;
    }

    public String toString() {
        return this.getVersion();
    }
}
