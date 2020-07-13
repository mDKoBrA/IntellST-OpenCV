package com.recognition.intellst.recognition;

import com.recognition.intellst.utils.OpenUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.opencv.imgproc.Imgproc.equalizeHist;

@Component
public class FaceControllerImpl {

    @FXML
    private Button button;
    @FXML
    private ImageView currentFrame;
    private ScheduledExecutorService timer;
    private boolean cameraActive = false;
    private int absoluteFaceSize;
    private Resource faceResource = new ClassPathResource("haarcascades/haarcascade_frontalface_alt2.xml");
    private VideoCapture capture = new VideoCapture();

    @FXML
    public void startCamera() {
        if (!this.cameraActive) {
            int cameraId = 0;
            this.capture.open(cameraId);
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                Runnable frameGrabber = () -> {
                    Mat frame = grabFrame();
                    OpenUtils openUtils = new OpenUtils();

                    Image imageToShow = openUtils.mat2Image(frame);
                    openUtils.updateImageView(currentFrame, imageToShow);
                };
                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
                this.button.setText("Stop Camera");
            } else {
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            this.cameraActive = false;
            this.button.setText("Start Camera");
            this.stopAcquisition();
        }
    }

    private Mat grabFrame() {
        Mat frame = new Mat();
        if (this.capture.isOpened()) {
            try {
                this.capture.read(frame);
                if (!frame.empty()) {
                    this.detectAndDisplay(frame);
                }
            } catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }
        return frame;
    }

    private void detectAndDisplay(Mat frame) throws IOException {
        var faces = new MatOfRect();
        Mat grayFrame = new Mat();

        CascadeClassifier faceDetector = new CascadeClassifier(faceResource.getFile().getAbsolutePath());

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        equalizeHist(grayFrame, grayFrame);

        if (this.absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }
        faceDetector.detectMultiScale(grayFrame, faces, 1.1, 2,
                Objdetect.CASCADE_SCALE_IMAGE, new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        Rect[] facesArray = faces.toArray();
        for (Rect rect : facesArray) {
            Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 3);

            Rect rectCrop = new Rect(rect.tl(), rect.br());
            Mat croppedImage = new Mat(frame, rectCrop);
            Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY);
            equalizeHist(croppedImage, croppedImage);
            Mat resizeImage = new Mat();
            Size size = new Size(250, 250);
            Imgproc.resize(croppedImage, resizeImage, size);
        }
    }

    public void init() {
        capture = new VideoCapture();
        new CascadeClassifier();
        this.absoluteFaceSize = 0;

        currentFrame.setFitWidth(600);
        currentFrame.setPreserveRatio(true);
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
}

