package com.recognition.intellst.recognition;

import org.opencv.core.*;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.DecimalFormat;

import static org.opencv.imgproc.Imgproc.equalizeHist;

public class FaceDisplay {
    private Resource faceResource = new ClassPathResource("haarcascades/haarcascade_frontalface_alt2.xml");
    private CascadeClassifier faceCascade;
    private int absoluteFaceSize;

    {
        try {
            faceCascade = new CascadeClassifier(faceResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setLabel(Mat im, String label, Point or, Scalar color) {
        int fontface = Core.FONT_HERSHEY_SIMPLEX;
        double scale = 0.8;
        int thickness = 2;
        int[] baseline = new int[1];

        Size text = Imgproc.getTextSize(label, fontface, scale, thickness, baseline);
        Imgproc.rectangle(im, new Point(or.x, or.y),
                new Point(or.x + text.width, or.y - text.height - baseline[0] - baseline[0]), color,
                Core.FILLED);

        Imgproc.putText(im, label, new Point(or.x, or.y - baseline[0]), fontface, scale,
                new Scalar(255, 255, 255), thickness);
    }

    public void detectAndDisplay(Mat frame) {

        FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
        faceRecognizer.load("src/main/resources/trainedmodel/train.yml");

        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        equalizeHist(grayFrame, grayFrame);

        if (absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }
        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2,
                Objdetect.CASCADE_SCALE_IMAGE, new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        Rect[] facesArray = faces.toArray();
        for (Rect rect : facesArray) {
            int[] label = new int[1];
            double[] confidence = new double[1];
            faceRecognizer.predict(grayFrame.submat(rect), label, confidence);
            String name = faceRecognizer.getLabelInfo(label[0]);
            Scalar color;
            if (confidence[0] < 50) {

                color = new Scalar(255, 0, 0);

                name = name + " " + new DecimalFormat("#.0").format(confidence[0]);
            } else {

//                String uuid = UUID.randomUUID().toString().replace("-", "");
//                int i;
////                for (i = 0; i < 10; i++) {
//                    CollectData.saveImage(frame, uuid, faceCascade);
                name = "Unknown";
                color = new Scalar(0, 0, 255);
            }
            Imgproc.rectangle(frame, rect.tl(), rect.br(), color, 2);
            setLabel(frame, name, rect.tl(), color);
        }
    }
}
