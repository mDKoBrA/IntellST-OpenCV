package com.recognition.intellst;

import com.recognition.intellst.app.ChartApplication;
import javafx.application.Application;
import org.opencv.core.Core;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IntellStApplication {


    public static void main(String[] args) {
        Application.launch(ChartApplication.class, args);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
