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

import io.github.michelfaria.mygallery.enums.EntryType;
import io.github.michelfaria.mygallery.exceptions.NotADirectoryException;
import io.github.michelfaria.mygallery.models.DirectoryEntry;
import io.github.michelfaria.mygallery.strategy.IFileNameShorteningStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.michelfaria.mygallery.enums.EntryType.*;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;

@Service
public class FileListingService implements IFileListingService {

    @Autowired
    private IFileNameShorteningStrategy fileNameShorteningStrategy;

    @Override
    public List<DirectoryEntry> listDir(Path path) throws IOException {
        if (!isDirectory(path)) {
            throw new NotADirectoryException("Provided path is not a directory.");
        }
        return Files.list(path)
                .map(p -> {
                    var filename = p.getFileName().toString();
                    return new DirectoryEntry(
                            filename,
                            entryType(p),
                            p,
                            fileNameShorteningStrategy.shorten(filename));
                })
                .filter(p -> !p.getName().startsWith("."))
                .collect(Collectors.toList());
    }

    private EntryType entryType(Path path) {
        if (isDirectory(path)) {
            return DIRECTORY;
        } else if (isRegularFile(path)) {
            return FILE;
        } else {
            return OTHER;
        }
    }
}
