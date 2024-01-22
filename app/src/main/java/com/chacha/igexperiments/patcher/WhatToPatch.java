package com.chacha.igexperiments.patcher;

public class WhatToPatch {
    private String methodToPatch;
    private String classToPatch;
    private String argumentType;

    public WhatToPatch(){
    }

    public void setClassToPatch(String classToPatch) {
        this.classToPatch = classToPatch;
    }

    public void setMethodToPatch(String methodToPatch) {
        this.methodToPatch = methodToPatch;
    }

    public void setArgumentType(String argumentType) {
        this.argumentType = argumentType;
    }

    public String getMethodToPatch() {
        return methodToPatch;
    }

    public String getClassToPatch() {
        return classToPatch;
    }

    public String getArgumentType() {
        return argumentType;
    }
}
