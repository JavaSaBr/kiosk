package com.ss.kiosk;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.runtime.Micronaut;
import org.jetbrains.annotations.NotNull;

public class UiApplication implements EmbeddedApplication<UiApplication> {

    private final @NotNull ApplicationContext applicationContext;
    private final @NotNull ApplicationConfiguration applicationConfiguration;

    public UiApplication() {
        this.applicationContext = Micronaut.build(new String[0])
            .classes(UiApplication.class)
            .mainClass(UiApplication.class)
            .start();
        this.applicationConfiguration = applicationContext.getBean(ApplicationConfiguration.class);
    }

    @Override
    public @NotNull ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public @NotNull ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    @Override
    public boolean isRunning() {
        return true;
    }
}
