package com.chacha.igexperiments;

/**
 * This class is used to store information about the Instagram version
 */
public class InfoIGVersion {

    private final String version;
    private final String classToHook, methodToHook, secondClassToHook;
    private final String url;

    /**
     * Constructor
     * @param version version of Instagram
     * @param classToHook class to hook
     * @param methodToHook method to hook
     * @param url url to download the apk
     */
    public InfoIGVersion(String version, String classToHook, String methodToHook, String secondClassToHook, String url) {
        super();
        this.version = version;
        this.classToHook = classToHook;
        this.methodToHook = methodToHook;
        this.secondClassToHook = secondClassToHook;
        this.url = url;
    }

    /**
     *
     * @return version of Instagram
     */
    public String getVersion() {
        return version;
    }

    /**
     *
     * @return the class to hook
     */
    public String getClassToHook() {
        return classToHook;
    }

    /**
     *
     * @return the method to hook
     */
    public String getMethodToHook(){
        return methodToHook;
    }

    /**
     *
     * @return second class to hook
     */
    public String getSecondClassToHook(){
        return secondClassToHook;
    }

    /**
     *
     * @return the url to download the apk
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @return the version of Instagram
     */
    @Override
    public String toString() {
        return this.getVersion();
    }
}
