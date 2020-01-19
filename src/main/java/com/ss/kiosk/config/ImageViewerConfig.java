package com.ss.kiosk.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@ConfigurationProperties("image-viewer")
public class ImageViewerConfig {

    public enum ImageMode {
        horizontal,
        vertical
    }

    private @NotNull ImageMode imageMode;

    private int switchInterval;
    private int rotation;
}
