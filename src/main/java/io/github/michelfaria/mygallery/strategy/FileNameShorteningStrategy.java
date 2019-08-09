package io.github.michelfaria.mygallery.strategy;

import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class FileNameShorteningStrategy implements IFileNameShorteningStrategy {
    @Override
    public @NonNull String shorten(@NonNull String filename) {
        int l = filename.lastIndexOf(".");
        if (l == -1) {
            if (filename.length() > 20) {
                return filename.substring(0, 17) + "...";
            } else {
                return filename;
            }
        } else if (l > 20) {
            return filename.substring(0, 17) + "..." + filename.substring(l);
        } else {
            return filename;
        }
    }
}
