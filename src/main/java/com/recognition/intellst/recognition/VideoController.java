package com.recognition.intellst.recognition;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.IOException;

import static com.recognition.intellst.recognition.CollectData.saveImage;
import static com.recognition.intellst.recognition.FaceDisplay.detectAndDisplay;
import static com.recognition.intellst.recognition.FaceDisplay.threadImage;
import static com.recognition.intellst.recognition.RecognitionConstants.VIDEO_HEIGHT;
import static com.recognition.intellst.recognition.RecognitionConstants.VIDEO_WIDTH;

public class VideoController {
    private static VideoCapture videoCapture;

    public static void startCamera() {
        videoCapture = new VideoCapture();
        videoCapture.open(0, Videoio.CAP_ANY);
        videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, VIDEO_WIDTH);
        videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, VIDEO_HEIGHT);
        videoCapture.set(Videoio.CAP_PROP_FPS, 60);

        Mat frame = new Mat();

        while (true) {
            if (videoCapture.isOpened()) {
                videoCapture.read(frame);
                if (!frame.empty()) {
                    grabFrame();
                }
            }
        }
    }

    private static void grabFrame() {
        Mat frame = new Mat();

        if (videoCapture.isOpened()) {
            videoCapture.read(frame);
            try {
                if (!frame.empty()) {
                    if (threadImage == null) {
                        detectAndDisplay(frame);
                    } else {
                        if (threadImage.isAlive()) {
                            saveImage(frame);
                        } else {
                            detectAndDisplay(frame);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }
    }
}



