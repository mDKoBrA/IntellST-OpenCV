package com.recognition.intellst.recognition;

import com.recognition.intellst.utils.OpenCVImageUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPStreamingServer implements Runnable {

    private static void writeImage(OutputStream outputStream, Mat frame) throws IOException {
        BufferedImage image = OpenCVImageUtils.matToBufferedImage(frame);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();
        outputStream.write(("Content-type: image/jpeg\r\n" +
                "Content-Length: " + imageBytes.length + "\r\n" +
                "\r\n").getBytes());
        outputStream.write(imageBytes);
        outputStream.write(("\r\n--" + "stream" + "\r\n").getBytes());
    }

    private static void writeHeader(OutputStream outputStream) throws IOException {
        outputStream.write(("HTTP/1.0 200 OK\r\n" +
                "Connection: close\r\n" +
                "Max-Age: 0\r\n" +
                "Expires: 0\r\n" +
                "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n" +
                "Pragma: no-cache\r\n" +
                "Content-Type: multipart/x-mixed-replace; " +
                "boundary=" + "stream" + "\r\n" +
                "\r\n" +
                "--" + "stream" + "\r\n").getBytes());
    }

    public static void main(String[] args) throws Exception {
        System.load("D:\\Projects\\some\\IntellST-OpenCV\\src\\main\\resources\\lib\\" +
                Core.NATIVE_LIBRARY_NAME + ".dll");
        Mat mat = new Mat();
        VideoCapture vid = new VideoCapture(0);
        vid.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        vid.set(Videoio.CAP_PROP_FRAME_HEIGHT, 120);
        vid.open(0);
        System.out.println("Camera open");

        ServerSocket ss = new ServerSocket(8086);
        Socket sock = ss.accept();
        System.out.println("Socket connected");
        writeHeader(sock.getOutputStream());
        System.out.println("Written header");
        sock.setKeepAlive(true);

        long stime = System.currentTimeMillis();
        int cnt = 0;
        while (true) {
            vid.read(mat);
            if (!mat.empty()) {
                writeImage(sock.getOutputStream(), mat);
                System.out.println("Written jpg");
                if (cnt++ >= 100) {
                    long stop = System.currentTimeMillis();
                    System.out.println("Frame rate: " + (cnt * 1000 / (stop - stime)));
                    cnt = 0;
                    stime = stop;
                }
            } else {
                System.out.println("No picture");
            }

            sock.setKeepAlive(true);
//            sock.close();
//            ss.close();
        }
    }

    @Override
    public void run() {


    }
}