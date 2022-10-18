package com.dvmena.followprojects.app;
import com.dvmena.followprojects.Main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import java.io.IOException;

@RequiredArgsConstructor
public class MainApp extends Application {
    private ConfigurableApplicationContext applicationContext;
    private Parent root;

    @FXML
    Label lblDetailsName;
    @Override
    public void start(Stage stage){
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(this.getClass().getResource("/com/dvmena/followprojects/myProjects.css").toExternalForm());
        Image image = new Image(this.getClass().getResourceAsStream("/com/dvmena/followprojects/images/icon.png"));
        stage.getIcons().add(image);
        stage.setTitle("Projects");
        stage.setScene(scene);
        stage.show();
        this.applicationContext.publishEvent(new StageReadyEvent(stage));
    }
    @Override
    public void init() throws IOException {
        ApplicationContextInitializer<GenericApplicationContext> initializer =
                applicationContext->applicationContext
                        .registerBean(Application.class,()->MainApp.this);

        this.applicationContext = new SpringApplicationBuilder()
                .sources(Main.class)
                .initializers(initializer)
                .run(getParameters().getRaw().toArray(new String[0]));
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/com/dvmena/followprojects/myProjects.fxml"));
        fxmlLoader.setControllerFactory(this.applicationContext::getBean);
        root = fxmlLoader.load();
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }
    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }
        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
