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

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilTest {
    @Test
    public void testNoExt() {
        Assert.assertEquals("foo-bar", Util.insertSuffix("foo", "-bar"));
    }

    @Test
    public void testExt() {
        Assert.assertEquals("foo-bar.baz", Util.insertSuffix("foo.baz", "-bar"));
    }
}