package com.recognition.intellst.app;

import com.recognition.intellst.app.ChartApplication;
import com.recognition.intellst.recognition.FaceController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<ChartApplication.StageReadyEvent> {

    @Value("${intellst.videofx.url}")
    private Resource charResource;
    private String applicationTitle;

    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle) {
        this.applicationTitle = applicationTitle;
    }

    @Override
    public void onApplicationEvent(ChartApplication.StageReadyEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(charResource.getURL());
            BorderPane root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add("application.css");

            Stage stage = event.getStage();
            stage.setScene(scene);
            stage.setTitle(applicationTitle);
            stage.show();

            FaceController controller = loader.getController();
            controller.init();

            stage.setOnCloseRequest((windowEvent -> controller.setClosed()));

        } catch (Exception e) {
            System.err.println("Error to create scene" + e);
        }

    }
}