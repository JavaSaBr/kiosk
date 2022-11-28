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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentRotationService {

    @NotNull LocalCacheService localCacheService;
    @NotNull AtomicInteger index = new AtomicInteger(0);

    public @Nullable Path nextContentFile() {

        var nextIndex = index.incrementAndGet();
        var cachedFiles = localCacheService.getCachedFiles();

        if (cachedFiles.isEmpty()) {
            return null;
        }

        if (nextIndex >= cachedFiles.size()) {
            index.compareAndSet(nextIndex, 0);
            nextIndex = 0;
        }

        return cachedFiles.get(nextIndex);
    }
}
