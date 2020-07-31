package com.recognition.intellst.recognition;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import static com.recognition.intellst.recognition.RecognitionConstants.VIDEO_HEIGHT;
import static com.recognition.intellst.recognition.RecognitionConstants.VIDEO_WIDTH;

public class VideoController {
//    public static Mat frame = null;
    private VideoCapture videoCapture;


    public void startCamera() {
        videoCapture = new VideoCapture();
        videoCapture.open(0);
        videoCapture.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, VIDEO_WIDTH);
        videoCapture.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, VIDEO_HEIGHT);
        videoCapture.set(Videoio.CAP_PROP_FPS, 60);


        Mat frame = new Mat();
        HTTPStreamingServer httpStreamingServer = new HTTPStreamingServer(frame);
        new Thread(httpStreamingServer).start();

        long stime = System.currentTimeMillis();
        int cnt = 0;
        while (true) {
            try {
                if (videoCapture.isOpened()) {

                    httpStreamingServer.image = grabFrame();

                }
                if (cnt++ >= 100) {
                    long stop = System.currentTimeMillis();
                    System.out.println("Frame rate: " + (cnt * 1000 / (stop - stime)));
                    cnt = 0;
                    stime = stop;
                }

            } catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }
    }

    private Mat grabFrame() {
        Mat frame = new Mat();
        if (videoCapture.isOpened()) {
            videoCapture.read(frame);
            try {
                if (!frame.empty()) {
                    FaceDisplay faceDisplay = new FaceDisplay();
                    faceDisplay.detectAndDisplay(frame);
                }
            } catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }
        return frame;
    }
}

