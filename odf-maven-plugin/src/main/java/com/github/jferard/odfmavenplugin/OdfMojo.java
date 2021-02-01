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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import sun.awt.X11.XEmbeddedFrame;

import java.io.FileDescriptor;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

@Mojo(name = "odf", defaultPhase = LifecyclePhase.INSTALL, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class OdfMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "odf.module", required = true, alias = "module")
    private String moduleName;

    @Parameter(property = "odf.src", required = true, alias = "src")
    private String sourcePath;

    @Parameter(property = "odf.dest", alias = "dest")
    private String destPath;

    @Parameter(property = "odf.embed", defaultValue = "${basedir}/src/main/resources/embed", alias = "embed")
    private String embedPath;

    public void execute() throws MojoExecutionException, MojoFailureException {
        List<RelativeFile> filesToEmbed = getFilesToEmbed();

        File destFile = getDestFile();
        getLog().info("Embed "+filesToEmbed+" in "+sourcePath+" -> "+destPath);
        try {
            new OdfUpdater(moduleName, new File(sourcePath), destFile, filesToEmbed).updateZip();
        } catch (Exception e) {
            throw new MojoExecutionException("odf-maven-plugin failed", e);
        }

    }

    private List<RelativeFile> getFilesToEmbed() {
        List<RelativeFile> filesToEmbed = new ArrayList<RelativeFile>();
        Artifact artifact = project.getArtifact();
        File jarFile = artifact.getFile();
        filesToEmbed.add(new RelativeFile(jarFile.getParentFile(), jarFile.getName()));

        List<File> files = new LinkedList<>();
        File embedDir = new File(embedPath);
        files.add(embedDir);
        while (!files.isEmpty()) {
            File nextFile = files.remove(0);
            for (File f : nextFile.listFiles()) {
                if (f.isDirectory()) {
                    files.add(f);
                } else {
                    String basePath = embedDir.getAbsolutePath();
                    String curPath = f.getAbsolutePath();
                    assert curPath.startsWith(basePath);
                    String name = curPath.substring(basePath.length()+1);
                    filesToEmbed.add(new RelativeFile(embedDir, name));
                }
            }
        }
        return filesToEmbed;
    }

    private File getDestFile() {
        File destFile;
        if (destPath == null) {
            String destName = Util.insertSuffix(new File(sourcePath).getName(), "-target");
            destFile = new File(project.getBuild().getOutputDirectory(), destName);
        } else {
            destFile = new File(destPath);
        }
        return destFile;
    }
}
