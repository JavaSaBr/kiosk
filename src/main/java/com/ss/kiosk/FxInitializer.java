package com.ss.kiosk;

import com.ss.rlib.common.util.ObjectUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class FxInitializer extends Application {

    private static volatile @Nullable Stage stage;

    public static @NotNull Stage getStage() {
        return ObjectUtils.notNull(stage);
    }

    public static void main(@NotNull String[] args) {
        launch(args);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private @Nullable UiApplication application;

    @Override
    public void start(@NotNull Stage primaryStage) {
        FxInitializer.stage = primaryStage;
        this.application = new UiApplication();
        log.info("Application is started.");
    }
}
