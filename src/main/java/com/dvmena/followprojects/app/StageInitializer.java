package com.dvmena.followprojects.app;

import com.dvmena.followprojects.app.MainApp;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
@Component
public class StageInitializer implements ApplicationListener<MainApp.StageReadyEvent> {
    @Override
    public void onApplicationEvent(MainApp.StageReadyEvent event) {
        Stage stage = event.getStage();
    }
}
