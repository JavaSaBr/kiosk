package com.ss.kiosk;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Launcher {
    public static void main(@NotNull String[] args) {
        log.info("Initializing application...");
        FxInitializer.main(args);
    }
}
