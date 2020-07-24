package com.recognition.intellst.enums;

public enum Extension {
    WIN(".dll"),
    NIX(".so");


    private String label;

    Extension(String lib) {
        this.label = lib;
    }

    public String getLabel() {
        return label;
    }
}
