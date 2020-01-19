package com.ss.kiosk.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class ImageRotationService {

    private final @NotNull LocalCacheService localCacheService;
    private final @NotNull AtomicInteger index = new AtomicInteger(0);

    public @NotNull Path nextImage() {

        var nextIndex = index.incrementAndGet();
        var images = localCacheService.getImages();

        if (nextIndex >= images.size()) {
            index.weakCompareAndSetAcquire(nextIndex, 0);
            nextIndex = 0;
        }

        return images.get(nextIndex);
    }
}
