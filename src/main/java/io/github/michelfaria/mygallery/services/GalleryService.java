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
import io.github.michelfaria.mygallery.exceptions.NotAFileException;
import io.github.michelfaria.mygallery.exceptions.SuspiciousPathException;
import io.github.michelfaria.mygallery.models.GalleryEntry;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static io.github.michelfaria.mygallery.config.MvcConfig.GALLERY_STATIC;
import static io.github.michelfaria.mygallery.enums.EntryType.DIRECTORY;
import static io.github.michelfaria.mygallery.enums.EntryType.FILE;
import static io.github.michelfaria.mygallery.services.CachingThumbnailService.THUMBNAIL_CACHE_FOLDERNAME;

@Service
public class GalleryService implements IGalleryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GalleryService.class);

    @Autowired
    private MyGalleryProperties properties;
    @Autowired
    private IFileListingService fileListingService;
    @Autowired
    private ICachingThumbnailService thumbnailService;

    @Override
    public GalleryDirectory fetchDirectory(Path securePath, int pageNo) throws IOException {
        if (Paths.get(properties.getGalleryPath())
                .relativize(securePath)
                .startsWith(THUMBNAIL_CACHE_FOLDERNAME)) {
            throw new SuspiciousPathException("Tried to fetch thumbnail cache");
        }

        var entriesPerPage = properties.getEntriesPerPage();
        var dirEntries = fileListingService.listDir(securePath);
        var totalPages = (int) Math.ceil((double) dirEntries.size() / (double) entriesPerPage);
        if (pageNo <= 0) {
            return new GalleryDirectory(new ArrayList<>(), totalPages);
        }
        var galleryEntries = dirEntries.stream()
                .sorted((o1, o2) -> {
                    if (o1.getType() == DIRECTORY && o2.getType() != DIRECTORY) {
                        return -1;
                    } else if (o1.getType() == o2.getType()) {
                        return 0;
                    } else {
                        return 1;
                    }
                })
                .skip((pageNo - 1) * entriesPerPage)
                .map(dirEntry -> {
                    final var entryPathWeb = FilenameUtils.separatorsToUnix(
                            Paths.get(properties.getGalleryPath())
                                    .relativize(dirEntry.getPath())
                                    .toString());
                    final var galEntry = new GalleryEntry();
                    galEntry.setDirectoryEntry(dirEntry);
                    galEntry.setName(dirEntry.getName());
                    galEntry.setGalleryPathWeb(entryPathWeb);

                    if (dirEntry.getType() == FILE) {
                        try {
                            galEntry.setThumbnailPathWeb(thumbnailService
                                    .thumbnail(dirEntry.getPath())
                                    .orElse(null));
                        } catch (Exception ex) {
                            LOGGER.error("Error while rendering thumbnail (" + dirEntry.getPath() + ")", ex);
                        }
                    }
                    return galEntry;

                })
                .limit(entriesPerPage)
                .collect(Collectors.toList());
        return new GalleryDirectory(galleryEntries, totalPages);
    }

    @Override
    public String staticGalleryFilePath(Path securePath) {
        if (!Files.isRegularFile(securePath)) {
            throw new NotAFileException(securePath.toString());
        }
        return GALLERY_STATIC + "/" + FilenameUtils.separatorsToUnix(
                Paths.get(properties.getGalleryPath())
                        .relativize(securePath)
                        .toString());
    }
}
