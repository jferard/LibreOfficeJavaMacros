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
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.ElementExistException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XModel;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.script.provider.XScriptContext;
import com.sun.star.sdb.XOfficeDatabaseDocument;
import com.sun.star.sdb.XQueryDefinitionsSupplier;
import com.sun.star.sdbc.XDataSource;
import com.sun.star.ui.dialogs.ExecutableDialogResults;
import com.sun.star.ui.dialogs.XExecutableDialog;
import com.sun.star.ui.dialogs.XFilePicker;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;

public class GUI {
    private XScriptContext xScriptContext;
    private Helper exampleHelper;

    public GUI(XScriptContext xScriptContext,
               Helper exampleHelper) {
        this.xScriptContext = xScriptContext;
        this.exampleHelper = exampleHelper;
    }

    public void execute() throws Exception {
        try {
            File file = getFile();
            Data.createBase(file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        XModel xDocModel = xScriptContext.getDocument();
        XDataSource oDataSource = connectToBase(xDocModel);
        Object oQueryDefinition = createQuery(exampleHelper);
        addQuery(oDataSource, oQueryDefinition);
    }

    private File getFile() throws Exception, URISyntaxException, MalformedURLException {
        Object oFilePicker = exampleHelper.createService("com.sun.star.ui.dialogs.FilePicker");
        XFilePicker xFilePicker = UnoRuntime.queryInterface(XFilePicker.class, oFilePicker);
        XExecutableDialog xDialog = UnoRuntime.queryInterface(XExecutableDialog.class, oFilePicker);
        if (xDialog.execute() == ExecutableDialogResults.CANCEL) {
            return null;
        }
        String[] files = xFilePicker.getFiles();
        if (files.length > 1) {
            //
        }
        String url = files[0];
        File f = new File(new URL(url).toURI());
        return f;
    }

    private void addQuery(XDataSource oDataSource, Object oQueryDefinition)
            throws WrappedTargetException, ElementExistException {
        XQueryDefinitionsSupplier
                xQuerySupplier = UnoRuntime.queryInterface(XQueryDefinitionsSupplier.class,
                oDataSource);
        XNameAccess xNameAccess = xQuerySupplier.getQueryDefinitions();
        XNameContainer xNameContainer =
                UnoRuntime.queryInterface(XNameContainer.class, xNameAccess);
        try {
            xNameContainer.removeByName("first query");
        } catch (NoSuchElementException e) {
            //
        }
        xNameContainer.insertByName("first query", oQueryDefinition);
    }

    private Object createQuery(Helper exampleHelper) throws Exception {
        Object oQueryDefinition = exampleHelper.createService("com.sun.star.sdb.QueryDefinition");
        XPropertySet
                xQueryDefinition = UnoRuntime.queryInterface(XPropertySet.class, oQueryDefinition);
        xQueryDefinition.setPropertyValue("Command", "SELECT * FROM \"MYTABLE\" WHERE \"FIELD1\">1");
        return oQueryDefinition;
    }

    private XDataSource connectToBase(XModel xDocModel)
            throws Exception {
        XOfficeDatabaseDocument
                xModel2 = UnoRuntime.queryInterface(XOfficeDatabaseDocument.class, xDocModel);
        XDataSource oDataSource = xModel2.getDataSource();
        XPropertySet
                xDataSource = UnoRuntime.queryInterface(XPropertySet.class, oDataSource);
        xDataSource.setPropertyValue("URL", "jdbc:hsqldb:file:./base.h2");
        xDataSource.setPropertyValue("User", "");
        Object oSettings = xDataSource.getPropertyValue("Settings");
        XPropertySet
                xSettings = UnoRuntime.queryInterface(XPropertySet.class, oSettings);
        xSettings.setPropertyValue("JavaDriverClass", "org.hsqldb.jdbcDriver");
        return oDataSource;
    }
}
