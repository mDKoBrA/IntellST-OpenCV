package com.recognition.intellst.recognition;

import com.recognition.intellst.utils.OpenUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.opencv.imgproc.Imgproc.*;

@Component
public class FaceController {
    public int index = 0;
    public int ind = 0;
    public String newname;
    public HashMap<Integer, String> names = new HashMap<Integer, String>();
    public int random = (int) (Math.random() * 20 + 3);

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


//    private static void setLabel(Mat im, String label, Point or, Scalar color) {
//        int fontface = FONT_HERSHEY_SIMPLEX;
//        double scale = 0.8;
//        int thickness = 2;
//        int[] baseline = new int[1];
//
//        Size text = Imgproc.getTextSize(label, fontface, scale, thickness, baseline);
//        Imgproc.rectangle(im, new Point(or.x, or.y),
//                new Point(or.x + text.width, or.y - text.height - baseline[0] - baseline[0]), color,
//                Core.FILLED);
//
//        Imgproc.putText(im, label, new Point(or.x, or.y - baseline[0]), fontface, scale,
//                new Scalar(255, 255, 255), thickness);
//    }

    @FXML
    public void startCamera() {
        if (!this.cameraActive) {

            this.capture.open("rtsp://admin:159852753-FCIM@169.254.182.206:554/Streaming/Channels/1/", Videoio.CAP_FFMPEG);
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
    private double[] faceRecognition(Mat currentFace) {

        // predict the label

        int[] predLabel = new int[1];
        double[] confidence = new double[1];
        int result = -1;

        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        faceRecognizer.read("src/main/resources/trainedmodel" + "/train.yml");
        faceRecognizer.predict(currentFace, predLabel, confidence);
        result = faceRecognizer.predict_label(currentFace);
        result = predLabel[0];

        return new double[]{result, confidence[0]};
    }


    /**
     * Method for face detection and tracking
     *
     * @param frame it looks for faces in this frame
     */
    private void detectAndDisplay(Mat frame) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);

            // Crop the detected faces
            Rect rectCrop = new Rect(facesArray[i].tl(), facesArray[i].br());
            Mat croppedImage = new Mat(frame, rectCrop);
            // Change to gray scale
            Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY);
            // Equalize histogram
            Imgproc.equalizeHist(croppedImage, croppedImage);
            // Resize the image to a default size
            Mat resizeImage = new Mat();
            Size size = new Size(250, 250);
            Imgproc.resize(croppedImage, resizeImage, size);

            // check if 'New user' checkbox is selected
            // if yes start collecting training data (50 images is enough)
            if ((true && !newname.isEmpty())) {
                if (index < 20) {
                    Imgcodecs.imwrite("resources/trainingset/combined/" +
                            random + "-" + newname + "_" + (index++) + ".png", resizeImage);
                }
            }
//			int prediction = faceRecognition(resizeImage);
            double[] returnedResults = faceRecognition(resizeImage);
            double prediction = returnedResults[0];
            double confidence = returnedResults[1];

//			System.out.println("PREDICTED LABEL IS: " + prediction);
            int label = (int) prediction;
            String name = "";
            if (names.containsKey(label)) {
                name = names.get(label);
            } else {
                name = "Unknown";
            }

            // Create the text we will annotate the box with:
//            String box_text = "Prediction = " + prediction + " Confidence = " + confidence;
            String box_text = "Prediction = " + name + " Confidence = " + confidence;
            // Calculate the position for annotated text (make sure we don't
            // put illegal values in there):
            double pos_x = Math.max(facesArray[i].tl().x - 10, 0);
            double pos_y = Math.max(facesArray[i].tl().y - 10, 0);
            // And now put it into the image:
            Imgproc.putText(frame, box_text, new Point(pos_x, pos_y),
                    FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
        }
    }
//    private void detectAndDisplay(Mat frame) throws IOException {
//        CascadeClassifier faceDetector = new CascadeClassifier(FaceDetector.class.getResource("haarcascade_frontalface_alt.xml").getPath());
//        Mat image = Highgui
//                .imread(FaceDetector.class.getResource("shekhar.JPG").getPath());
//
//        MatOfRect faceDetections = new MatOfRect();
//        faceDetector.detectMultiScale(image, faceDetections);
//
//        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
//
//        for (Rect rect : faceDetections.toArray()) {
//            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
//                    new Scalar(0, 255, 0));
//        }
//
//        String filename = "ouput.png";
//        System.out.println(String.format("Writing %s", filename));
//        Highgui.imwrite(filename, image);
//        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
//        faceRecognizer.read("src/main/resources/trainedmodel/train.yml");
//
//        MatOfRect faces = new MatOfRect();
//        Mat grayFrame = new Mat();
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
//        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2,
//                Objdetect.CASCADE_SCALE_IMAGE, new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
//
//        Rect[] facesArray = faces.toArray();
//        for (Rect rect : facesArray) {
//            int[] label = new int[1];
//            double[] confidence = new double[1];
//            faceRecognizer.predict(grayFrame.submat(rect), label, confidence);
//            String name = faceRecognizer.getLabelInfo(label[0]);
//            Scalar color = null;
//            if (confidence[0] < 50) {
//
//                color = new Scalar(255, 0, 0);
//
//                name = name + " " + new DecimalFormat("#.0").format(confidence[0]);
//            } else {
//
//                String uuid = UUID.randomUUID().toString().replace("-", "");
////                int i;
////                for (i = 0; i < 10; i++) {
////                    CollectData.saveImage(frame, uuid, faceCascade);
//                name = "Unknown";
//                color = new Scalar(0, 0, 255);
////                }
//
//            }
//
//            Imgproc.rectangle(frame, rect.tl(), rect.br(), color, 2);
//            setLabel(frame, name, rect.tl(), color);
//            System.out.println("Test");
//        }
//
//    }

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

