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
package com.github.jferard.libreofficemacrosexample;

import com.github.jferard.libreofficemacrostools.Helper;
import com.sun.star.script.provider.XScriptContext;
import com.sun.star.uno.Exception;

public class Example {
    public void example(XScriptContext xScriptContext) throws Exception {
        Helper exampleHelper = Helper.create(xScriptContext);
        new GUI(xScriptContext, exampleHelper).execute();
    }
}
