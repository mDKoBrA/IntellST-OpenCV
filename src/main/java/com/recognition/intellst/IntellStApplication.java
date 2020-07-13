package com.recognition.intellst;

import com.recognition.intellst.app.ChartApplication;
import javafx.application.Application;
import org.opencv.core.Core;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IntellStApplication {

    static {
        System.load("D:\\Projects\\some\\IntellST-OpenCV\\src\\main\\resources\\lib\\" +
                Core.NATIVE_LIBRARY_NAME + ".dll");
    }

    public static void main(String[] args) {
        Application.launch(ChartApplication.class, args);
    }
}
