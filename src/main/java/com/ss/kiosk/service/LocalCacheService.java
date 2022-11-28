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
package com.ss.kiosk.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import com.ss.kiosk.model.RemoteImage;
import com.ss.rlib.common.util.FileUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalCacheService {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("jpg", "png");

    final @NotNull Path cacheFolder;

    @Getter
    volatile @NotNull List<Path> images = emptyList();

    public void initializeAndLoad() {

        if (!Files.exists(cacheFolder)) {
            FileUtils.createDirectories(cacheFolder);
        }

        reload();
    }

    public void reload() {

        if (!Files.exists(cacheFolder)) {
            FileUtils.createDirectories(cacheFolder);
        }

        images = FileUtils.getFiles(cacheFolder)
            .stream()
            .filter(path -> SUPPORTED_EXTENSIONS.contains(FileUtils.getExtension(path)))
            .collect(toList());
    }

    public void reload(@NotNull List<RemoteImage> remoteImages) {

        var filesInCache = FileUtils.getFiles(cacheFolder);
        var fileNameToFile = filesInCache.stream()
            .filter(path -> SUPPORTED_EXTENSIONS.contains(FileUtils.getExtension(path)))
            .collect(toMap(path -> path.getFileName().toString(), Function.identity()));

        for (var remoteImage : remoteImages) {

            if (remoteImage.name() == null || remoteImage.url() == null) {
                log.warn("Invalid remote image: {}", remoteImage);
                continue;
            }

            var cachedFile = fileNameToFile.get(remoteImage.name());

            // if this image was not cached previously
            if (cachedFile == null) {
                var newFile = cacheFolder.resolve(remoteImage.name());
                try {
                    log.info("Download new image from URL: \"{}\"", remoteImage.url());
                    Files.copy(remoteImage.url().openStream(), newFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                continue;
            }

            var lastModifiedTime = FileUtils
                .getLastModifiedTime(cachedFile)
                .toInstant();

            // if we have new version of the cached file
            if (remoteImage.lastModified() == null || lastModifiedTime.isBefore(lastModifiedTime)) {
                try {
                    // delete old version to avoid any reading during re-writing
                    log.info("Delete old version of cached image: \"{}\"", cachedFile);
                    Files.deleteIfExists(cachedFile);
                    log.info("Download updated image from URL: \"{}\"", remoteImage.url());
                    Files.copy(remoteImage.url().openStream(), cachedFile);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                log.info("Cached image \"{}\" is up to date", cachedFile);
            }
        }

        var nameToRemoteImage = remoteImages
            .stream()
            .collect(toMap(RemoteImage::name, Function.identity()));

        fileNameToFile.entrySet()
            .stream()
            .filter(entry -> !nameToRemoteImage.containsKey(entry.getKey()))
            .peek(entry -> log.info("Delete unnecessary cached image: {}", entry.getValue()))
            .forEach(entry -> FileUtils.delete(entry.getValue()));

        reload();
    }
}
