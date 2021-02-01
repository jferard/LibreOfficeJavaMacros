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

import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class OdfUpdater {
    private String moduleName;
    private File sourceOdf;
    private final File destOdf;
    private List<RelativeFile> filesToEmbed;

    public OdfUpdater(String moduleName, File sourceOdf, File destOdf, List<RelativeFile> filesToEmbed) {
        this.moduleName = moduleName;
        this.sourceOdf = sourceOdf;
        this.destOdf = destOdf;
        this.filesToEmbed = filesToEmbed;
    }

    public void updateZip()
            throws IOException, ParserConfigurationException, SAXException, JDOMException {
        ZipFile zipFile = new ZipFile(sourceOdf);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destOdf));
        try {
            for (Enumeration e = zipFile.entries(); e.hasMoreElements(); ) {
                ZipEntry entryIn = (ZipEntry) e.nextElement();
                if (!entryIn.getName().equalsIgnoreCase("META-INF/manifest.xml")) {
                    copyEntry(zipFile, entryIn, zos);
                } else {
                    updateManifest(zipFile, entryIn, zos);
                }
                zos.closeEntry();
            }
            String destPath = "Scripts/java/" + moduleName+ "/";
            for (RelativeFile fileToEmbed : filesToEmbed) {
                zos.putNextEntry(new ZipEntry(destPath+fileToEmbed.getName()));
                copyStream(new FileInputStream(fileToEmbed.getFile()), zos);
                zos.closeEntry();
            }
        } finally {
            zos.close();
        }
    }

    private void copyEntry(ZipFile zipFile, ZipEntry entryIn, ZipOutputStream zos)
            throws IOException {
        zos.putNextEntry(entryIn);
        InputStream is = zipFile.getInputStream(entryIn);
        copyStream(is, zos);
    }

    private void copyStream(InputStream is, java.io.OutputStream os) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            os.write(buf, 0, len);
        }
    }

    private void updateManifest(ZipFile zipFile, ZipEntry entryIn, ZipOutputStream zos)
            throws IOException, ParserConfigurationException, SAXException, JDOMException {
        zos.putNextEntry(new ZipEntry(entryIn.getName()));
        InputStream is = zipFile.getInputStream(entryIn);
        new ManifestHelper(moduleName, filesToEmbed).update(is, zos);
    }
}
