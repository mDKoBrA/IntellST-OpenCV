package com.recognition.intellst.recognition;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.IOException;

import static com.recognition.intellst.recognition.CollectData.saveImage;
import static com.recognition.intellst.recognition.RecognitionConstants.VIDEO_HEIGHT;
import static com.recognition.intellst.recognition.RecognitionConstants.VIDEO_WIDTH;

public class VideoController {
    private static VideoCapture videoCapture;

    public static void startCamera() {
        videoCapture = new VideoCapture();
        videoCapture.open(0);
        videoCapture.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, VIDEO_WIDTH);
        videoCapture.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, VIDEO_HEIGHT);
        videoCapture.set(Videoio.CAP_PROP_FPS, 60);


        Mat frame = new Mat();
        HTTPStreamingServer httpStreamingServer = new HTTPStreamingServer(frame);
        new Thread(httpStreamingServer).start();

        while (true) {
            if (videoCapture.isOpened()) {
                videoCapture.read(frame);
                if (!frame.empty()) {
                    httpStreamingServer.image = grabFrame();

                }
            }
        }
    }

    private static Mat grabFrame() {
        Mat frame = new Mat();

        if (videoCapture.isOpened()) {
            videoCapture.read(frame);
            try {
                if (!frame.empty()) {
                    if (FaceDisplay.threadImage == null) {
                        FaceDisplay.detectAndDisplay(frame);
                    } else {
                        if (FaceDisplay.threadImage.isAlive()) {
                            saveImage(frame);
                        } else {
                            FaceDisplay.detectAndDisplay(frame);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }
        return frame;
    }
}



