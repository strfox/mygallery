package io.github.michelfaria.mygallery.strategy;

import lombok.NonNull;

public interface IFileNameShorteningStrategy {
    @NonNull String shorten(@NonNull String filename);
}
