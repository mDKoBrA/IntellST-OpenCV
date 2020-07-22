package com.recognition.intellst.app;

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
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    @Value("${spring.application.ui.title} ")
    private String applicationTitle;
    @Value("classpath:${applicatiun.url.path}")
    private Resource charResource;
    @Value("${application.stylesheet.path}")
    private String stylesheet;

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(charResource.getURL());
            BorderPane root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(stylesheet);

            Stage stage = stageReadyEvent.getStage();
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
