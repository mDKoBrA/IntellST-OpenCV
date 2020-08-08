package com.recognition.intellst.recognition;

import com.recognition.intellst.utils.OpenCVImageUtils;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPStreamingServer implements Runnable {
    public Mat image;
    private Socket socket;

    public HTTPStreamingServer(Mat frame) {
        this.image = frame;
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

    public void writeImage(Mat frame) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
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

    public void startStreamingServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8085);
        socket = serverSocket.accept();
        writeHeader(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                startStreamingServer();
                while (true) {
                    writeImage(image);
                }
            } catch (Exception e) {
                System.out.println("Problems occurred to start streaming server");
            }
        }
    }
}