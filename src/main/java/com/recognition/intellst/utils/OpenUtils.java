package com.recognition.intellst.utils;


import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;


public final class OpenUtils {

    private static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
        Platform.runLater(() -> property.set(value));
    }

    private static BufferedImage matToBufferedImage(Mat matrix) {
        int width = matrix.width();
        int height = matrix.height();
        int type = matrix.channels() != 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;

        if (type == BufferedImage.TYPE_3BYTE_BGR) {
            Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_BGR2RGB);
        }

        byte[] data = new byte[width * height * (int) matrix.elemSize()];
        matrix.get(0, 0, data);

        BufferedImage image = new BufferedImage(width, height, type);
        image.getRaster().setDataElements(0, 0, width, height, data);

        return image;
    }

    public Image mat2Image(Mat frame) {
        return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
    }

    public void updateImageView(ImageView view, Image image) {
        onFXThread(view.imageProperty(), image);
    }
}
