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
import io.github.michelfaria.mygallery.exceptions.SuspiciousPathException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PathSecuringService implements IPathSecuringService {
    @Autowired
    private MyGalleryProperties properties;

    @Override
    public Path securePath(String pathString) {
        String normalized = FilenameUtils.normalize(pathString);
        if (normalized == null) {
            throw new SuspiciousPathException(pathString);
        }
        Path path = Paths.get(properties.getGalleryPath(), normalized);
        assert path.startsWith(properties.getGalleryPath());
        return path;
    }
}
