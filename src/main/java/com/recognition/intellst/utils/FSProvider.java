package com.recognition.intellst.utils;

import java.io.File;
import java.net.URI;

public class FSProvider {
    private static FSProvider INSTANCE = new FSProvider();

    public static FSProvider getInstance() {
        return INSTANCE;
    }

    private File getCurrentPathFile() {
        File ret = null;
        try {
            URI jarURI = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            File fPK = new File(jarURI.getSchemeSpecificPart());
            if (jarURI.toString().endsWith(".jar")) {
                File fJar = new File(jarURI.getSchemeSpecificPart());
                fPK = new File(fJar.getAbsolutePath());
            }
            ret = fPK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public File getCurrentPathNormalizedFile() {
        File ret;

        File f = getCurrentPathFile();
        String p = f.getParentFile().getAbsolutePath();
        if (f.getAbsolutePath().endsWith(".jar")) {
            ret = new File(p);
        } else {
            ret = f;
        }
        return ret;
    }
}
