package com.recognition.intellst.recognition;

import com.recognition.intellst.utils.OpenUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_NONE;
import static org.opencv.imgproc.Imgproc.equalizeHist;

@Component
public class FaceController {

    @FXML
    private Button button;
    @FXML
    private ImageView currentFrame;
    private ScheduledExecutorService timer;
    private boolean cameraActive = false;
    private int absoluteFaceSize;
    private VideoCapture capture = new VideoCapture();
    private Resource faceResource = new ClassPathResource("haarcascades/haarcascade_frontalface_alt2.xml");
    private CascadeClassifier faceCascade = new CascadeClassifier(faceResource.getFile().getAbsolutePath());

    public FaceController() throws IOException {
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

    @FXML
    public void startCamera() {
        if (!this.cameraActive) {

            this.capture.open(0);
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

        FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
        faceRecognizer.load("src/main/resources/trainedmodel/train.yml");

        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        equalizeHist(grayFrame, grayFrame);

        if (this.absoluteFaceSize == 0) {
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
            Scalar color = null;
            if (confidence[0] < 50) {

                color = new Scalar(255, 0, 0);

                name = name + " " + new DecimalFormat("#.0").format(confidence[0]);
            } else {

                String uuid = UUID.randomUUID().toString().replace("-", "");
//                int i;
//                for (i = 0; i < 10; i++) {
//                    CollectData.saveImage(frame, uuid, faceCascade);
                    name = "Unknown";
                    color = new Scalar(0, 0, 255);
//                }

            }

            Imgproc.rectangle(frame, rect.tl(), rect.br(), color, 2);
            setLabel(frame, name, rect.tl(), color);
            System.out.println("Test");
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

