package com.recognition.intellst.recognition;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class CollectData {

    public static String path = "src/main/resources/training/";
    private static int sample = 0;

    public static void saveImage(Mat image, String uuid, CascadeClassifier faceCascade) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(image, grayFrame, Imgproc.COLOR_BGR2GRAY);

        faceCascade.detectMultiScale(grayFrame, faces);

        Rect[] facesArray = faces.toArray();
        if (facesArray.length >= 1) {
            sample++;
            System.out.println("image: " + sample);
            Imgcodecs.imwrite(path + "2" + "-" + uuid + "_" + (sample) + ".png",
                    image.submat(facesArray[0]));
        }
    }
}

