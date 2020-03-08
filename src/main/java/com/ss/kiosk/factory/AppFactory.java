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
package com.ss.kiosk.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ss.kiosk.FxInitializer;
import com.ss.kiosk.config.ImageRepositoryConfig;
import com.ss.kiosk.config.RenderConfig;
import com.ss.kiosk.config.LocalCacheConfig;
import com.ss.kiosk.json.ZonedDateTimeDeserializer;
import com.ss.kiosk.repository.ImageRepository;
import com.ss.kiosk.service.ImageRotationService;
import com.ss.kiosk.service.LocalCacheService;
import com.ss.kiosk.view.ImageViewer;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.http.HttpClient;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Factory
public class AppFactory {

    @Singleton
    public @NotNull ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors()));
    }

    @Singleton
    public @NotNull ImageRotationService imageRotationService(@NotNull LocalCacheService localCacheService) {
        return new ImageRotationService(localCacheService);
    }

    @Inject
    @Singleton
    public @NotNull LocalCacheService localCacheService(
        @NotNull LocalCacheConfig config,
        @NotNull ScheduledExecutorService scheduledExecutorService
    ) {

        var service = new LocalCacheService(Paths.get(config.getFolder()));

        scheduledExecutorService.execute(service::initializeAndLoad);

        log.info(
            """
            Start local cache service with settings: {
                folder: "{}",
                reload-interval: {}
            }""",
            config.getFolder(),
            config.getReloadInterval()
        );

        scheduledExecutorService.scheduleWithFixedDelay(
            service::reload,
            config.getReloadInterval(),
            config.getReloadInterval(),
            TimeUnit.SECONDS
        );

        return service;
    }

    @Inject
    @Singleton
    public @NotNull ImageRepository imageRepositoryService(
        @NotNull ImageRepositoryConfig config,
        @NotNull HttpClient httpClient,
        @NotNull ScheduledExecutorService scheduledExecutorService,
        @NotNull Gson gson,
        @NotNull LocalCacheService localCacheService
    ) {

        var service = new ImageRepository(config, httpClient, gson, localCacheService);
        service.reload();

        log.info(
            """
            Start image download service with settings: {
                method: "{}",
                url: "{}",
                reload-interval: {}
            }""",
            config.getMethod(),
            config.getUrl(),
            config.getReloadInterval()
        );

        scheduledExecutorService.scheduleWithFixedDelay(
            service::reload,
            config.getReloadInterval(),
            config.getReloadInterval(),
            TimeUnit.SECONDS
        );

        return service;
    }

    @Singleton
    public @NotNull Stage rootWindow() {
        return FxInitializer.getStage();
    }

    @Singleton
    public @NotNull Screen primaryScreen() {
        return Screen.getPrimary();
    }

    @Context
    public @NotNull ImageViewer imageViewer(
        @NotNull Stage rootWindow,
        @NotNull ImageRotationService imageRotationService,
        @NotNull RenderConfig config,
        @NotNull ImageRepository imageRepository,
        @NotNull ScheduledExecutorService scheduledExecutorService
    ) {

        var viewer = ImageViewer.builder()
            .imageMode(config.getImageMode())
            .rotation(config.getRotation())
            .stage(rootWindow)
            .renderHeight(config.getHeight())
            .renderWidth(config.getWidth())
            .imageRotationService(imageRotationService)
            .build();

        viewer.initializeAndLoad();

        log.info(
            """
            Start image viewer with settings: {
                switch-interval: {},
                image-mode: "{}",
                rotation: {},
                with: {},
                height: {}
            }""",
            config.getSwitchInterval(),
            config.getImageMode(),
            config.getRotation(),
            config.getWidth(),
            config.getHeight()
        );

        scheduledExecutorService.scheduleWithFixedDelay(
            viewer::loadNext,
            config.getSwitchInterval(),
            config.getSwitchInterval(),
            TimeUnit.SECONDS
        );

        return viewer;
    }

    @Singleton
    public @NotNull HttpClient httpClient(@NotNull ScheduledExecutorService scheduledExecutorService) {
        return HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .executor(scheduledExecutorService)
            .build();
    }

    @Singleton
    public @NotNull Gson gson() {
        return new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
            .create();
    }
}
