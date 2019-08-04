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
import io.github.michelfaria.mygallery.models.GalleryEntry;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.stream.Collectors;

import static io.github.michelfaria.mygallery.config.MvcConfig.GALLERY_STATIC;
import static io.github.michelfaria.mygallery.enums.EntryType.FILE;

@Service
public class GalleryService implements IGalleryService {

    @Autowired
    private MyGalleryProperties properties;
    @Autowired
    private IFileListingService fileListingService;
    @Autowired
    private IThumbnailService thumbnailService;
    @Autowired
    private IPathSecuringService pathSecuringService;

    @Override
    public GalleryDirectory fetchDirectory(Path securePath, int pageNo) throws IOException {
        var entriesPerPage = properties.getEntriesPerPage();
        var dirEntries = fileListingService.listDir(securePath);
        var totalPages = (int) Math.ceil((double) dirEntries.size() / (double) entriesPerPage);
        if (pageNo <= 0) {
            return new GalleryDirectory(new ArrayList<>(), totalPages);
        }
        var galleryEntries = dirEntries.stream()
                .skip((pageNo - 1) * entriesPerPage)
                .map(e -> {
                    var galleryPath = FilenameUtils.separatorsToUnix(
                            Paths.get(properties.getGalleryPath())
                                    .relativize(e.getPath())
                                    .toString());
                    if (e.getType() == FILE) {
                        try {
                            var thumbnail = thumbnailService.thumbnail(e.getPath());
                            var thumbnailBytes = thumbnail.toByteArray();
                            var encodedThumbnail = Base64.getEncoder().encode(thumbnailBytes);
                            return new GalleryEntry(e, new String(encodedThumbnail), galleryPath);
                        } catch (UnsupportedFormatException ignored) {
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException(ex);
                        }
                    }
                    return new GalleryEntry(e, null, galleryPath);
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
