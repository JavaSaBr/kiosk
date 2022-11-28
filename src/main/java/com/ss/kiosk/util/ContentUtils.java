package com.ss.kiosk.util;

import com.ss.rlib.common.util.FileUtils;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class ContentUtils {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "png");
    private static final Set<String> VIDEO_EXTENSIONS = Set.of("mp4");
    private static final Set<String> SUPPORTED_EXTENSIONS = Stream
        .concat(IMAGE_EXTENSIONS.stream(), VIDEO_EXTENSIONS.stream())
        .collect(Collectors.toUnmodifiableSet());

    public static boolean isSupported(@NotNull Path path) {
        return SUPPORTED_EXTENSIONS.contains(FileUtils.getExtension(path));

    }
    public static boolean isImage(@NotNull Path path) {
        return IMAGE_EXTENSIONS.contains(FileUtils.getExtension(path));
    }

    public static boolean isVideo(@NotNull Path path) {
        return VIDEO_EXTENSIONS.contains(FileUtils.getExtension(path));
    }
}
