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
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class LocalCacheService {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("jpg", "png");

    private final @NotNull Path cacheFolder;

    @Getter
    private volatile @NotNull List<Path> images = emptyList();

    public void initializeAndLoad() {

        if (!Files.exists(cacheFolder)) {
            Utils.unchecked(cacheFolder, Files::createDirectories);
        }

        reload();
    }

    public void reload() {
        images = Utils.uncheckedGet(cacheFolder, Files::list)
            .filter(path -> SUPPORTED_EXTENSIONS.contains(FileUtils.getExtension(path)))
            .collect(toList());
    }
}
