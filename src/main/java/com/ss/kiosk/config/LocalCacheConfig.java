package com.ss.kiosk.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@ConfigurationProperties("local-cache")
public class LocalCacheConfig {

    private @NotNull String folder;

    private int reloadInterval;
}
