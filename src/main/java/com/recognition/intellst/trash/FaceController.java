package com.recognition.intellst.trash;

import com.recognition.intellst.utils.OpenCVImageUtils;
//import javafx.scene.control.Button;
//import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class FaceController {


//    private Button button;

//    private ImageView currentFrame;
    private ScheduledExecutorService timer;
    private boolean cameraActive = false;
    private int absoluteFaceSize;
    private VideoCapture capture = new VideoCapture();


    public void startCamera() {
        if (!this.cameraActive) {
            int cameraId = 0;
            this.capture.open(cameraId);
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                Runnable frameGrabber = () -> {
                    Mat frame = grabFrame();
                    OpenCVImageUtils openCVImageUtils = new OpenCVImageUtils();

//                    Image imageToShow = openCVImageUtils.mat2Image(frame);
//                    openCVImageUtils.updateImageView(currentFrame, imageToShow);
                };
                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
//                this.button.setText("Stop Camera");
            } else {
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            this.cameraActive = false;
//            this.button.setText("Start Camera");
            this.setClosed();
        }
    }

    private Mat grabFrame() {
        Mat frame = new Mat();
        if (this.capture.isOpened()) {
            try {
                this.capture.read(frame);
                if (!frame.empty()) {
//                    this.detectAndDisplay(frame);
                }
            } catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }
        return frame;
    }

    public void init() {
        capture = new VideoCapture();
        new CascadeClassifier();
        this.absoluteFaceSize = 0;

//        currentFrame.setFitWidth(600);
//        currentFrame.setPreserveRatio(true);
    }

    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                this.timer.isShutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }
        if (this.capture.isOpened()) {
            this.capture.release();
        }
    }

    public void setClosed() {
        this.stopAcquisition();
    }

    public void startCapture(String cameraIP, boolean start) {

    }
}

