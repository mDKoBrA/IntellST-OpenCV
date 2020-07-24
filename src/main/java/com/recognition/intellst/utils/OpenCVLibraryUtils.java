package com.recognition.intellst.utils;

import com.recognition.intellst.enums.Extension;
import org.opencv.core.Core;

import java.io.File;

public class OpenCVLibraryUtils {
    private static String OS = System.getProperty("os.name").toLowerCase();

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    }

    public static void prepareLib() {
        String ext;
        String fixedLD = null;

        if (isUnix()) {
            ext = Extension.NIX.getLabel();
            File f = FSProvider.getInstance().getCurrentPathNormalizedFile();
            fixedLD = f.getPath() + File.separator + Core.NATIVE_LIBRARY_NAME + ext;
        } else if (isWindows()) {
            ext = Extension.WIN.getLabel();
            String libPath = "D:\\Projects\\some\\IntellST-OpenCV\\src\\main\\resources\\lib\\";
            fixedLD = libPath + Core.NATIVE_LIBRARY_NAME + ext;
        }
        System.load(fixedLD);
    }
}
