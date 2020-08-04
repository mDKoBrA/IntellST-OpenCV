package com.recognition.intellst.recognition;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.recognition.intellst.recognition.RecognitionConstants.HAAR_RESOURCE;

public class CollectData implements Runnable {

    public static String uuid;
    public static String path;
    private static int sample = 0;
//    public Mat savedImages;

    public static void saveImage(Mat image) throws IOException {
        CascadeClassifier faceCascade = new CascadeClassifier(HAAR_RESOURCE.getFile().getAbsolutePath());
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(image, grayFrame, Imgproc.COLOR_BGR2GRAY);

        faceCascade.detectMultiScale(grayFrame, faces);

        Rect[] facesArray = faces.toArray();
        if (facesArray.length >= 1) {
            sample++;
            System.out.println("image: " + sample);
            Imgcodecs.imwrite(path + "/" + "2" + "-" + uuid + "_" + (sample) + ".png",
                    image.submat(facesArray[0]));
        }
    }

    public void imageData() throws IOException {
        uuid = UUID.randomUUID().toString().replace("-", "");
        File file = new File("src/main/resources/training/" + uuid);
        file.mkdir();
        path = file.getCanonicalPath();
    }

    @Override
    public void run() {

    }
}

