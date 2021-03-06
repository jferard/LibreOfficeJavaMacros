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

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A helper class to writ the manifest
 */
public class ManifestHelper {
    private final Namespace manifestNS;
    private String moduleName;
    private final XMLOutputter xmlOutput;
    private List<RelativePath> filesToEmbed;

    /**
     * @param moduleName  the name of the module
     * @param filesToEmbed thelist of the files to embed
     */
    public ManifestHelper(String moduleName,
                          List<RelativePath> filesToEmbed) {
        this.filesToEmbed = filesToEmbed;
        manifestNS = Namespace
                .getNamespace("manifest", "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0");
        this.moduleName = moduleName;
        xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
    }

    /**
     * @param is the META-INF/manifest.xml input stream
     * @param out the META-INF/manifest.xml output stream
     * @throws JDOMException if there is a XML exception
     * @throws IOException if there is a IO exception.
     */
    public void update(InputStream is, OutputStream out) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(is);
        Element root = document.getRootElement();
        root.getChildren().addAll(createElements());
        xmlOutput.output(document, out);
    }

    private List<Element> createElements() {
        SortedSet<String> directories = findDirectories();
        List<Element> elements = new ArrayList<Element>();
        for (String directory : directories) {
            elements.add(createDirectoryElement(directory));
        }
        for (RelativePath rf : this.filesToEmbed) {
            elements.add(createFileElement("Scripts/java/"+this.moduleName+"/"+rf.getUniversalRelativePath()));
        }
        return elements;
    }

    private SortedSet<String> findDirectories() {
        SortedSet<String> directories = new TreeSet<String>();
        directories.add("Scripts");
        directories.add("Scripts/java");
        directories.add("Scripts/java/"+moduleName);
        String base = "Scripts/java/" + this.moduleName + "/";
        for (RelativePath rf : this.filesToEmbed) {
            List<String> parts = rf.getRelativePathParts();
            if (parts.size() > 1) {
                StringBuilder sb = new StringBuilder(parts.get(0));
                directories.add(base + sb.toString());
                for (int i = 1; i < parts.size() - 1; i++) {
                    directories.add(base + sb.toString());
                    sb.append("/").append(parts.get(i));
                    directories.add(base + sb.toString());
                }
            }
        }
        return directories;
    }

    private Element createFileElement(String path) {
        if (path.endsWith(".xml")) {
            return createElement(path, "text/xml");
        } else {
            return createElement(path, "application/binary");
        }
    }

    private Element createDirectoryElement(String path) {
        return createElement(path, "application/binary");
    }

    private Element createElement(String path, String type) {
        Element scriptsElement = new Element("file-entry", manifestNS);
        scriptsElement.getAttributes().add(new Attribute("full-path", path,
                manifestNS));
        scriptsElement.getAttributes().add(new Attribute("media-type", type,
                manifestNS));
        return scriptsElement;
    }
}
