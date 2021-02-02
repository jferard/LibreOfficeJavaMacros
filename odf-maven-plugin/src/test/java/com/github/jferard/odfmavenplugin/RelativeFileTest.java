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

package com.github.jferard.odfmavenplugin;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Path;

public class RelativeFileTest {
    @Test
    public void testJimfs() throws NoSuchFieldException, IllegalAccessException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path temp = fs.getPath("C:\\temp");
        Assert.assertEquals(fs.getPath("C:\\"), temp.getParent());

        Path tempTest = fs.getPath("C:\\temp\\test");
        Assert.assertEquals(fs.getPath("C:\\temp\\"), tempTest.getParent());
    }

    @Test
    public void testUniversalSep() throws NoSuchFieldException, IllegalAccessException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path directory = fs.getPath("C:\\temp");
        RelativePath rf = new RelativePath(directory, fs.getPath("foo\\bar\\baz.txt"));
        Assert.assertEquals(fs.getPath("C:\\temp\\foo\\bar\\baz.txt"), rf.getPath());
        Assert.assertEquals("foo/bar/baz.txt", rf.getUniversalRelativePath());
        Assert.assertEquals("C:\\temp@foo\\bar\\baz.txt", rf.toString());
    }
}