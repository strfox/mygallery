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

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ThumbnailService implements IThumbnailService {
    @Override
    public FastByteArrayOutputStream thumbnail(Path securePath) throws IOException {
        if (!Files.isRegularFile(securePath)) {
            throw new IllegalArgumentException("Provided path is not a file: " + securePath);
        }
        final var os = new FastByteArrayOutputStream();
        Thumbnails.of(securePath.toFile())
                .outputQuality(1.0)
                .outputFormat("png")
                .imageType(BufferedImage.TYPE_INT_ARGB)
                .size(200, 200)
                .toOutputStream(os);
        return os;
    }
}
