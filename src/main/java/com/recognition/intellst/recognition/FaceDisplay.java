package com.recognition.intellst.recognition;

import org.opencv.core.*;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.DecimalFormat;

import static org.opencv.face.Face.createLBPHFaceRecognizer;
import static org.opencv.imgproc.Imgproc.equalizeHist;

public class FaceDisplay {

}
//    private int absoluteFaceSize = 0;
//    private Resource faceResource = new ClassPathResource("haarcascades/haarcascade_frontalface_alt2.xml");
//
//    public void detectAndDisplay(Mat frame) throws IOException {
//        var faces = new MatOfRect();
//        Mat grayFrame = new Mat();
//
//        CascadeClassifier faceDetector = new CascadeClassifier(faceResource.getFile().getAbsolutePath());
//
//        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
//        equalizeHist(grayFrame, grayFrame);
//
//        if (this.absoluteFaceSize == 0) {
//            int height = grayFrame.rows();
//            if (Math.round(height * 0.2f) > 0) {
//                this.absoluteFaceSize = Math.round(height * 0.2f);
//            }
//        }
//        faceDetector.detectMultiScale(grayFrame, faces, 1.1, 2,
//                Objdetect.CASCADE_SCALE_IMAGE, new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
//
//        Rect[] facesArray = faces.toArray();
//        for (Rect rect : facesArray) {
//            Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 3);
//
//            Rect rectCrop = new Rect(rect.tl(), rect.br());
//            Mat croppedImage = new Mat(frame, rectCrop);
//            Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY);
//            equalizeHist(croppedImage, croppedImage);
//            Mat resizeImage = new Mat();
//            Size size = new Size(250, 250);
//            Imgproc.resize(croppedImage, resizeImage, size);
//        }
//    }

