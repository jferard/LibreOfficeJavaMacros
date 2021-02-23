/*
 * LibreOffice Macros. Write and embed LibreOffice Java macros with Maven.
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

package com.github.jferard.libreofficemacros.hsqldb.example;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {
    public static void copyFile(File source, File dest) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(source));
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
            try {
                byte[] buffer = new byte[1024];
                int lengthRead = in.read(buffer);
                while (lengthRead > 0) {
                    out.write(buffer, 0, lengthRead);
                    out.flush();
                    lengthRead = in.read(buffer);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
