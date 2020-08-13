package com.recognition.intellst.service.impl;

import com.recognition.intellst.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.recognition.intellst.recognition.CollectData.saveImage;
import static com.recognition.intellst.recognition.FaceDisplay.detectAndDisplay;
import static com.recognition.intellst.recognition.FaceDisplay.threadImage;
import static com.recognition.intellst.recognition.RecognitionConstants.VIDEO_HEIGHT;
import static com.recognition.intellst.recognition.RecognitionConstants.VIDEO_WIDTH;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {

    private static VideoCapture videoCapture;

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


    @Override
    public void startCamera(String videoURL, boolean activeCamera) {
        log.info("method = startCamera");

        if (activeCamera) {
            log.info("Camera is Active");
            videoCapture = new VideoCapture();
            videoCapture.open(0, Videoio.CAP_ANY);
            videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, VIDEO_WIDTH);
            videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, VIDEO_HEIGHT);
            videoCapture.set(Videoio.CAP_PROP_FPS, 30);

            Mat frame = new Mat();
            while (true) {
                if (videoCapture.isOpened()) {
                    videoCapture.read(frame);
                    if (!frame.empty()) {
                        grabFrame();
                    }
                }
            }
        } else {
            try {
                videoCapture.release();
                log.info("Camera is stopped");
            } catch (Exception e) {
                log.info("Camera is stopped error occurred, camera is already stopped");
                e.getCause();
            }
        }
    }
}