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

package com.github.jferard.libreofficemacrostools;

import com.sun.star.frame.XModel;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.script.provider.XScript;
import com.sun.star.script.provider.XScriptContext;
import com.sun.star.script.provider.XScriptProvider;
import com.sun.star.script.provider.XScriptProviderFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.File;

public class Helper {
    public static Helper create(XScriptContext xScriptContext) {
        XComponentContext xContext = xScriptContext.getComponentContext();
        XMultiComponentFactory xFactory = xContext.getServiceManager();
        XModel xDocModel = xScriptContext.getDocument();
        return new Helper(xContext, xDocModel, xFactory);

    }

    private final XComponentContext xContext;
    private final XModel xDocModel;
    private final XMultiComponentFactory xFactory;
    private XScript xrayScript;

    public Helper(XComponentContext xContext, XModel xDocModel,
                  XMultiComponentFactory xFactory) {
        this.xContext = xContext;
        this.xDocModel = xDocModel;
        this.xFactory = xFactory;
    }

    public XComponentContext getXContext() {
        return this.xContext;
    }

    public Object createService(final String serviceName) throws Exception {
        return xFactory.createInstanceWithContext(serviceName, xContext);
    }

    public URL getDocumentURL() throws MalformedURLException {
        return new URL(xDocModel.getURL());
    }

    public File getDocumentDirectoryFile() throws MalformedURLException, URISyntaxException {
        return new File(new URL(xDocModel.getURL()).toURI()).getParentFile();
    }

    public void xray(XScriptContext xScriptContext, Object target)
            throws Exception {
        if (xrayScript == null) {
            initXray(xScriptContext);
        }
        xrayScript.invoke(new Object[]{target}, new short[100][100], new Object[100][100]);
    }

    private void initXray(XScriptContext xScriptContext) throws Exception {
        XComponentContext xContext = xScriptContext.getComponentContext();
        XMultiComponentFactory factory = xContext.getServiceManager();
        Object oFactory = factory.createInstanceWithContext(
                "com.sun.star.script.provider.MasterScriptProviderFactory", xContext);
        XScriptProviderFactory xFactory =
                UnoRuntime.queryInterface(XScriptProviderFactory.class, oFactory);
        XScriptProvider xProvider = xFactory.createScriptProvider("");
        xrayScript =
                xProvider.getScript(
                        "vnd.sun.star.script:XrayTool._Main.Xray?language=Basic&location=application");
    }
}
