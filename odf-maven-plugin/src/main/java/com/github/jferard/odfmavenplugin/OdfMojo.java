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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Mojo(name = "odf", defaultPhase = LifecyclePhase.INSTALL,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class OdfMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "odf.module", required = true, alias = "module")
    private String moduleName;

    @Parameter(property = "odf.src", required = true, alias = "src")
    private String sourcePathStr;

    @Parameter(property = "odf.dest", alias = "dest")
    private String destPath;

    @Parameter(property = "odf.embed", defaultValue = "${basedir}/src/main/resources/embed",
            alias = "embed")
    private String embedPathStr;

    public void execute() throws MojoExecutionException {
        try {
            List<RelativePath> filesToEmbed = this.getFilesToEmbed();
            Path destPath = getDestPath();
            getLog().info(
                    "Embed " + filesToEmbed + " in " + sourcePathStr + " -> " + this.destPath);
            new OdfUpdater(moduleName, Paths.get(sourcePathStr), destPath, filesToEmbed)
                    .updateZip();
        } catch (Exception e) {
            throw new MojoExecutionException("odf-maven-plugin failed", e);
        }

    }

    private List<RelativePath> getFilesToEmbed() throws IOException {
        List<RelativePath> filesToEmbed = new ArrayList<>();
        Artifact artifact = project.getArtifact();
        Path jarPath = artifact.getFile().toPath();
        filesToEmbed.add(new RelativePath(jarPath.getParent(), jarPath.getFileName()));

        List<Path> paths = new LinkedList<>();
        Path embedPath = Paths.get(embedPathStr);
        paths.add(embedPath);
        while (!paths.isEmpty()) {
            Path curPath = paths.remove(0);
            Iterator<Path> it = Files.list(curPath).iterator();
            while (it.hasNext()) {
                Path childPath = it.next();
                if (Files.isDirectory(childPath)) {
                    paths.add(childPath);
                } else {
                    Path relativePath = embedPath.relativize(childPath);
                    filesToEmbed.add(new RelativePath(embedPath, relativePath));
                }
            }
        }
        return filesToEmbed;
    }

    private Path getDestPath() {
        Path destFile;
        if (destPath == null) {
            String destName = Util.insertSuffix(Paths.get(sourcePathStr).getFileName().toString(), "-target");
            destFile = Paths.get(project.getBuild().getOutputDirectory(), destName);
        } else {
            destFile = Paths.get(destPath);
        }
        return destFile;
    }
}
