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
