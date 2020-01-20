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
