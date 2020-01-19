package com.ss.kiosk.factory;

import com.ss.kiosk.FxInitializer;
import com.ss.kiosk.config.ImageViewerConfig;
import com.ss.kiosk.config.LocalCacheConfig;
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
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Factory
public class AppFactory {

    @Singleton
    public @NotNull ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Singleton
    public @NotNull ImageRotationService imageRotationService(
        @NotNull LocalCacheService localCacheService
    ) {
        return new ImageRotationService(localCacheService);
    }

    @Inject
    @Singleton
    public @NotNull LocalCacheService localCacheService(
        @NotNull LocalCacheConfig config,
        @NotNull ScheduledExecutorService scheduledExecutorService
    ) {

        var cacheService = new LocalCacheService(Paths.get(config.getFolder()));
        cacheService.initializeAndLoad();

        log.info(
            """
            Start cache service with settings: {
                reload-interval: {},
            }""",
            config.getReloadInterval()
        );

        scheduledExecutorService.scheduleWithFixedDelay(
            cacheService::reload,
            config.getReloadInterval(),
            config.getReloadInterval(),
            TimeUnit.SECONDS
        );

        return cacheService;
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
        @NotNull ImageViewerConfig config,
        @NotNull ScheduledExecutorService scheduledExecutorService
    ) {

        var viewer = ImageViewer.builder()
            .imageMode(config.getImageMode())
            .rotation(config.getRotation())
            .stage(rootWindow)
            .imageRotationService(imageRotationService)
            .build();

        viewer.initializeAndLoad();

        log.info(
            """
            Start image viewer with settings: {
                switch-interval: {},
                image-mode: "{}",
            }""",
            config.getSwitchInterval(),
            config.getImageMode()
        );

        scheduledExecutorService.scheduleWithFixedDelay(
            viewer::loadNext,
            config.getSwitchInterval(),
            config.getSwitchInterval(),
            TimeUnit.SECONDS
        );

        return viewer;
    }
}
