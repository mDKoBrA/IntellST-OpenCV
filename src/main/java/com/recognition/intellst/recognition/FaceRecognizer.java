package com.recognition.intellst.recognition;

import org.opencv.core.Mat;
import org.opencv.face.Face;

import java.io.File;
import java.io.IOException;

public class FaceRecognizer {
    public static void main() throws IOException {

        File f = new File("src/main/resources/training/");


        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.print("directory:");
                } else {
                    System.out.print("     file:");
                }
                System.out.println(file.getCanonicalPath());
            }
        }

    }

    public double[] faceRecognition(Mat currentFace) {

        int[] predLabel = new int[1];
        double[] confidence = new double[1];
        int result;

        org.opencv.face.FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
        faceRecognizer.load("train.yml");
        faceRecognizer.predict(currentFace, predLabel, confidence);
        result = predLabel[0];

        return new double[]{result, confidence[0]};
    }
}