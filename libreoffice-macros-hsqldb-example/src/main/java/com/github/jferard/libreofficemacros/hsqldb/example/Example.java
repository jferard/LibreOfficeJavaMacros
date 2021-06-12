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

import com.github.jferard.libreofficemacrostools.Helper;
import com.sun.star.script.provider.XScriptContext;

/**
 * the entry point for the macro (see parcel-descriptor.xml)
 */
public class Example {

    /**
     * the entry point for the macro (see parcel-descriptor.xml)
     */
    public void example(XScriptContext xScriptContext) throws java.lang.Exception {
        Helper exampleHelper = Helper.create(xScriptContext);
        new GUI(xScriptContext, exampleHelper).execute();
    }
}
