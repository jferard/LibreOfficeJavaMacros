/*
 * LibreOffice Java Macros. Write and embed LibreOffice Java macros with Maven.
 *     Copyright (C) 2021 Julien Férard
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.jferard.odfmavenplugin;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RelativePath {
    private final Path directory;
    private final Path relativePath;

    public RelativePath(Path directory, Path relativePath) {
        this.directory = directory;
        this.relativePath = relativePath;
    }

    public Path getPath() {
        return directory.resolve(relativePath);
    }

    /**
     * Return the name with universal file separator
     */
    public String getUniversalRelativePath() {
        return StreamSupport.stream(relativePath.spliterator(), false).map(Path::toString)
                .collect(Collectors.joining("/"));
   }

    /**
     * Return the name with universal file separator
     */
    public List<String> getRelativePathParts() {
        return StreamSupport.stream(relativePath.spliterator(), false).map(Path::toString)
                .collect(Collectors.toList());
    }

    public String toString() {
        return this.directory + "@" + this.relativePath;
    }
}
