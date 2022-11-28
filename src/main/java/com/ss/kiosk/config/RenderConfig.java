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
package com.ss.kiosk.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import org.jetbrains.annotations.NotNull;

@ConfigurationProperties("render")
public record RenderConfig(
    @NotNull ImageMode imageMode,
    int switchInterval,
    int rotation,
    int width,
    int height) {

    public enum ImageMode {
        horizontal,
        vertical
    }
}
