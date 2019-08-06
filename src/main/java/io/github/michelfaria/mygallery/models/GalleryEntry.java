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

import lombok.Data;
import lombok.experimental.Delegate;
import org.springframework.lang.Nullable;

@Data
public class GalleryEntry {

    @Delegate(types = DirectoryEntry.class)
    private DirectoryEntry directoryEntry;

    /**
     * Thumbnail data in Base 64
     */
    private @Nullable
    String thumbnail64;

    /**
     * Thumbnail path relative to gallery root as String
     */
    private @Nullable
    String thumbnailPathWeb;

    /**
     * Path relative to gallery root as String
     */
    private String galleryPathWeb;
}
