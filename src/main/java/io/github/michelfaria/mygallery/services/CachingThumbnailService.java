/*
 * Copyright (C) 2019 Michel Faria
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.github.michelfaria.mygallery.services;

import io.github.michelfaria.mygallery.config.MyGalleryProperties;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Optional;

import static io.github.michelfaria.mygallery.config.MvcConfig.GALLERY_STATIC;

@Service
public class CachingThumbnailService implements ICachingThumbnailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachingThumbnailService.class);
    public static final String THUMBNAIL_CACHE_FOLDERNAME = ".thumbnail_cache";
    private static final String THUMBNAIL_FORMAT = "jpg";
    private static final String THUMBNAIL_EXTENSION = "." + THUMBNAIL_FORMAT;

    @Autowired
    private MyGalleryProperties properties;

    @Setter
    @Getter
    private Path thumbnailCachePath = null;

    @PostConstruct
    public void init() {
        thumbnailCachePath = Paths.get(properties.getGalleryPath()).resolve(THUMBNAIL_CACHE_FOLDERNAME);
        new Thread(this::refreshCache).start();
    }

    private void refreshCache() {
        LOGGER.info("Refreshing thumbnail cache...");
        final var galleryPath = Paths.get(properties.getGalleryPath());
        try {
            Files.walkFileTree(galleryPath, new ThumbnailCacheRefreshingVisitor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<String> thumbnail(@NonNull Path securePath) {
        var thumbnailPath = Paths.get(
                FilenameUtils.removeExtension(
                        thumbnailCachePath.resolve(Paths
                                .get(properties.getGalleryPath())
                                .relativize(securePath))
                                .toString())
                        + THUMBNAIL_EXTENSION);
        if (Files.notExists(thumbnailPath)) {
            makeThumbnail(securePath, thumbnailPath);
        }
        if (Files.notExists(thumbnailPath)) {
            return Optional.empty();
        }
        return Optional.of(
                GALLERY_STATIC + "/" + FilenameUtils.separatorsToUnix(
                        Objects.requireNonNull(thumbnailCachePath.getParent())
                                .relativize(thumbnailPath)
                                .toString()));
    }

    private void makeThumbnail(Path sourcePath, Path destPath) {
        try {
            Files.createDirectories(destPath.getParent());
            Thumbnails.of(sourcePath.toFile())
                    .outputQuality(0.8)
                    .outputFormat(THUMBNAIL_FORMAT)
                    .imageType(BufferedImage.TYPE_INT_ARGB)
                    .size(200, 200)
                    .toFile(destPath.toFile());
        } catch (UnsupportedFormatException ex) {
            LOGGER.debug("Cannot make thumbnail for {}, unsupported format", sourcePath);
        } catch (Exception ex) {
            LOGGER.debug(
                    String.format("Failed to make thumbnail of file '%s', would write to '%s'.", sourcePath, destPath),
                    ex);
        }
    }

    private class ThumbnailCacheRefreshingVisitor implements FileVisitor<Path> {
        private final Path galleryPath;

        private ThumbnailCacheRefreshingVisitor() {
            this.galleryPath = Paths.get(properties.getGalleryPath());
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            if (dir.equals(thumbnailCachePath)) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (Files.isRegularFile(file)) {
                Path cachePathEquivalent = Paths.get(
                        FilenameUtils.removeExtension(
                                thumbnailCachePath
                                        .resolve(galleryPath.relativize(file))
                                        .toString())
                                + THUMBNAIL_EXTENSION);
                if (Files.notExists(cachePathEquivalent)) {
                    LOGGER.debug("Making thumbnail of {}, writing to {}", file, cachePathEquivalent);
                    makeThumbnail(file, cachePathEquivalent);
                } else {
                    LOGGER.debug("Skipping creation of thumbnail of file since thumbnail already cached: {}", file);
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            return FileVisitResult.CONTINUE;
        }
    }
}
