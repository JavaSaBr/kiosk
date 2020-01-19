package com.ss.kiosk.view;

import com.ss.kiosk.config.ImageViewerConfig.ImageMode;
import com.ss.kiosk.service.ImageRotationService;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Builder
@RequiredArgsConstructor
public class ImageViewer {

    private final @NotNull Stage stage;
    private final @NotNull ImageMode imageMode;
    private final @NotNull ImageRotationService imageRotationService;
    private final @NotNull StackPane container = new StackPane();

    private final int rotation;

    public void initializeAndLoad() {
        container.setAlignment(Pos.CENTER);
        loadNext();
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.setScene(new Scene(container, Color.WHITE));
        stage.setFullScreen(true);
        stage.show();
    }

    public void loadNext() {
        Platform.runLater(() -> {

            var url = imageRotationService
                .nextImage()
                .toUri()
                .toString();

            var image = new Image(url);
            var imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setRotate(rotation);

            switch (imageMode) {
                case horizontal: {
                    imageView.fitWidthProperty().bind(stage.widthProperty());
                    imageView.fitHeightProperty().bind(stage.heightProperty());
                    break;
                }
                case vertical: {
                    imageView.fitWidthProperty().bind(stage.heightProperty());
                    imageView.fitHeightProperty().bind(stage.widthProperty());
                }
            }

            var children = container.getChildren();
            children.stream()
                .filter(ImageView.class::isInstance)
                .map(ImageView.class::cast)
                .peek(iw -> iw.fitHeightProperty().unbind())
                .forEach(iw -> iw.fitWidthProperty().unbind());

            children.clear();
            children.add(imageView);
        });
    }
}