/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ss.kiosk.view;

import com.ss.kiosk.config.RenderConfig.ImageMode;
import com.ss.kiosk.service.ImageRotationService;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Builder
@RequiredArgsConstructor
public class ImageViewer {

    private final @NotNull Stage stage;
    private final @NotNull ImageMode imageMode;
    private final @NotNull ImageRotationService imageRotationService;
    private final @NotNull StackPane container = new StackPane();

    private final int rotation;
    private final int renderWidth;
    private final int renderHeight;

    public void initializeAndLoad() {
        Platform.runLater(() -> {
            container.setAlignment(Pos.CENTER);

            stage.initStyle(StageStyle.UNDECORATED);
            stage.setOnCloseRequest(event -> System.exit(0));
            stage.setScene(new Scene(container, Color.BLACK));
            stage.setWidth(renderWidth);
            stage.setHeight(renderHeight);
            stage.show();
            stage.setX(0);
            stage.setY(0);

            loadInFxThread();
        });
    }

    public void loadNext() {
        Platform.runLater(this::loadInFxThread);
    }

    private void loadInFxThread() {

        var nextImage = imageRotationService.nextImage();

        if (nextImage == null) {
            log.warn("No any cached images...");
            return;
        }

        var url = nextImage
            .toUri()
            .toString();

        var image = new Image(url);
        var imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setRotate(rotation);

        switch (imageMode) {
            case horizontal -> {
                imageView.fitWidthProperty().bind(stage.widthProperty());
                imageView.fitHeightProperty().bind(stage.heightProperty());
            }
            case vertical -> {
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
    }
}
