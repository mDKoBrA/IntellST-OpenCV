package com.recognition.intellst.recognition;

import lombok.Setter;
import org.opencv.core.*;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.IOException;
import java.text.DecimalFormat;

import static com.recognition.intellst.recognition.CollectData.uuid;
import static com.recognition.intellst.recognition.RecognitionConstants.HAAR_RESOURCE;
import static com.recognition.intellst.recognition.RecognitionConstants.TRAINED_MODEL;
import static org.opencv.imgproc.Imgproc.equalizeHist;

public class FaceDisplay {

    @Setter
    public static Thread threadImage;
    private static int absoluteFaceSize;

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

    public static void detectAndDisplay(Mat frame) throws IOException {

        CollectData collectData = new CollectData();
        CascadeClassifier faceCascade = new CascadeClassifier(HAAR_RESOURCE.getFile().getAbsolutePath());

        FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
        faceRecognizer.load(TRAINED_MODEL);

        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        equalizeHist(grayFrame, grayFrame);

        if (absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                absoluteFaceSize = Math.round(height * 0.2f);
            }
        }
        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2,
                Objdetect.CASCADE_SCALE_IMAGE, new Size(absoluteFaceSize, absoluteFaceSize), new Size());

        Rect[] facesArray = faces.toArray();
        for (Rect rect : facesArray) {
            int[] label = new int[1];
            double[] confidence = new double[1];
            faceRecognizer.predict(grayFrame.submat(rect), label, confidence);
            StringBuilder name = new StringBuilder(faceRecognizer.getLabelInfo(label[0]));
            Scalar color;

            if (confidence[0] < 50) {

                color = new Scalar(255, 0, 0);
                name.append(" ").append(new DecimalFormat("#.0").format(confidence[0]));

            } else {

                Runnable collect = new CollectData();
                threadImage = new Thread(collect);
                threadImage.start();
                setThreadImage(threadImage);

                collectData.imageData();

                name = new StringBuilder(uuid);
                color = new Scalar(0, 0, 255);
            }

            Imgproc.rectangle(frame, rect.tl(), rect.br(), color, 2);
            setLabel(frame, name.toString(), rect.tl(), color);
        }
    }
}
