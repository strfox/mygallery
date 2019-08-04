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

package io.github.michelfaria.mygallery.models;

import io.github.michelfaria.mygallery.enums.EntryType;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.nio.file.Path;

@Data
public class GalleryEntry {
    private DirectoryEntry directoryEntry;
    private @Nullable
    String thumbnail64;
    private String galleryPath;

    public GalleryEntry(DirectoryEntry directoryEntry, @Nullable String thumbnail64, String galleryPath) {
        super();
        this.directoryEntry = directoryEntry;
        this.thumbnail64 = thumbnail64;
        this.galleryPath = galleryPath;
    }

    public String getName() {
        return getDirectoryEntry().getName();
    }

    public EntryType getType() {
        return getDirectoryEntry().getType();
    }

    public Path getAbsolutePath() {
        return getDirectoryEntry().getPath();
    }

    public void setName(String name) {
        getDirectoryEntry().setName(name);
    }

    public void setType(EntryType type) {
        getDirectoryEntry().setType(type);
    }

    public void setAbsolutePath(Path absolutePath) {
        getDirectoryEntry().setPath(absolutePath);
    }

}
