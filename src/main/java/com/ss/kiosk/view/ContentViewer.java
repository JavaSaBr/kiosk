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
import com.ss.kiosk.service.ContentRotationService;
import com.ss.kiosk.util.ContentUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@Slf4j
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentViewer {

    @NotNull Stage stage;
    @NotNull ImageMode imageMode;
    @NotNull ContentRotationService contentRotationService;
    @NotNull StackPane container = new StackPane();

    int rotation;
    int renderWidth;
    int renderHeight;

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

        var nextContent = contentRotationService.nextContentFile();

        if (nextContent == null) {
            log.warn("No any cached content...");
            return;
        }

        var children = container.getChildren();
        children.forEach(this::cleanupNode);
        children.clear();
        children.add(buildViewer(nextContent));
    }

    @NotNull
    private Node buildViewer(@NotNull Path content) {
        if (ContentUtils.isImage(content)) {
            return buildImageViewer(content);
        } else if (ContentUtils.isVideo(content)) {
            return buildVideoViewer(content);
        } else {
            throw new IllegalArgumentException("Unsupported content: " + content);
        }
    }

    @NotNull
    private Node buildImageViewer(@NotNull Path content) {

        var url = content.toUri()
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

        return imageView;
    }

    @NotNull
    private Node buildVideoViewer(@NotNull Path content) {

        var url = content.toUri()
            .toString();

        var media = new Media(url);
        var mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        var mediaView = new MediaView(mediaPlayer);
        mediaView.setPreserveRatio(true);
        mediaView.setRotate(rotation);

        switch (imageMode) {
            case horizontal -> {
                mediaView.fitWidthProperty().bind(stage.widthProperty());
                mediaView.fitHeightProperty().bind(stage.heightProperty());
            }
            case vertical -> {
                mediaView.fitWidthProperty().bind(stage.heightProperty());
                mediaView.fitHeightProperty().bind(stage.widthProperty());
            }
        }

        return mediaView;
    }

    protected void cleanupNode(@NotNull Node node) {
        if (node instanceof MediaView mv) {
            mv.fitHeightProperty().unbind();
            mv.fitWidthProperty().unbind();
        } else if (node instanceof ImageView iv) {
            iv.fitHeightProperty().unbind();
            iv.fitWidthProperty().unbind();
        }
    }
}
