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

import io.github.michelfaria.mygallery.models.GalleryEntry;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IGalleryService {

    @AllArgsConstructor
    class GalleryDirectory {
        public List<GalleryEntry> entries;
        public int totalPages;
    }

    @NonNull GalleryDirectory fetchDirectory(@NonNull Path securePath, int pageNo) throws IOException;

    @NonNull String staticGalleryFilePath(@NonNull Path securePath);
}
