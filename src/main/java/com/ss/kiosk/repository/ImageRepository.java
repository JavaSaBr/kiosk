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

package com.ss.kiosk.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ss.kiosk.config.ImageRepositoryConfig;
import com.ss.kiosk.model.RemoteContent;
import com.ss.kiosk.service.LocalCacheService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class ImageRepository {

    private static final Type REMOTE_IMAGES_LIST_TYPE = new TypeToken<List<RemoteContent>>() {}.getType();

    private final @NotNull HttpRequest request;
    private final @NotNull LocalCacheService localCacheService;
    private final @NotNull HttpClient httpClient;
    private final @NotNull Gson gson;

    public ImageRepository(
        @NotNull ImageRepositoryConfig config,
        @NotNull HttpClient httpClient,
        @NotNull Gson gson,
        @NotNull LocalCacheService localCacheService
    ) {
        this.gson = gson;
        this.httpClient = httpClient;
        this.localCacheService = localCacheService;
        this.request = HttpRequest.newBuilder(URI.create(config.url()))
            .method(config.method(), BodyPublishers.noBody())
            .build();
    }

    public void reload() {
        httpClient
            .sendAsync(request, BodyHandlers.ofString(StandardCharsets.UTF_8))
            .thenAccept(this::parseResponse)
            .whenComplete((aVoid, ex) -> {
                if (ex != null) {
                    log.error(ex.getMessage(), ex);
                }
            });
    }

    private void parseResponse(@NotNull HttpResponse<String> httpResponse) {

        if (httpResponse.statusCode() != 200) {
            log.error("Got unexpected response: {}", httpResponse);
            return;
        }

        log.info("Received raw response: {}", httpResponse.body());

        List<RemoteContent> remoteContents = gson.fromJson(httpResponse.body(), REMOTE_IMAGES_LIST_TYPE);

        log.info("Received parsed data: {}", gson.toJson(remoteContents));

        localCacheService.reload(remoteContents);
    }
}
