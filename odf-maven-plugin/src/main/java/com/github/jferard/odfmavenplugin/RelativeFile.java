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

import java.io.File;

public class RelativeFile {
    private final File directory;
    private final String name;

    public RelativeFile(File directory, String name) {
        this.directory = directory;
        this.name = name;
    }

    public File getFile() {
        return new File(directory, name);
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.directory + "@" + this.name;
    }
}
