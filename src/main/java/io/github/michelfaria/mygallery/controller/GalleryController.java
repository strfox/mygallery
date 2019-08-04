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

package io.github.michelfaria.mygallery.controller;

import io.github.michelfaria.mygallery.exceptions.NotADirectoryException;
import io.github.michelfaria.mygallery.exceptions.SuspiciousPathException;
import io.github.michelfaria.mygallery.services.IGalleryService;
import io.github.michelfaria.mygallery.services.IPathSecuringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/")
public class GalleryController {

    @Autowired
    private IGalleryService galleryService;
    @Autowired
    private IPathSecuringService pathSecuringService;

    @GetMapping("/")
    public String index(
            Model model,
            @RequestParam(value = "path", defaultValue = "/") String path,
            @RequestParam(value = "page", required = false) Integer page) throws IOException {

        page = page == null ? 1 : page;
        assert path != null;

        var securedPath = pathSecuringService.securePath(path);

        if (Files.isDirectory(securedPath)) {
            var directory = galleryService.fetchDirectory(securedPath, page);

            model.addAttribute("entries", directory.entries);
            model.addAttribute("totalPages", directory.totalPages);
        } else {
            var staticFilePath = galleryService.staticGalleryFilePath(securedPath);
            model.addAttribute("filePath", staticFilePath);
        }

        model.addAttribute("page", page);
        model.addAttribute("path", path);

        /*
         * Map of path and its folder name
         * Example:
         * :: /              ==> /
         * :: /photos/       ==> photos
         * :: /photos/andrew ==> andrew
         */
        var pathTree = new LinkedHashMap<>();

        pathTree.put("ROOT", "/");

        var tokens = path.split("/");
        for (int i = 0, tokensLength = tokens.length; i < tokensLength; i++) {
            var token = tokens[i];
            var sb = new StringBuilder();
            for (int j = 0; j < i; j++) {
                sb.append(tokens[j]);
                sb.append("/");
            }
            sb.append(token);
            pathTree.put(token, sb.toString());
        }
        model.addAttribute("pathTree", pathTree);

        return "gallery/main";
    }

    @ExceptionHandler({SuspiciousPathException.class, NotADirectoryException.class})
    public String handleError() {
        return "whoops";
    }
}
