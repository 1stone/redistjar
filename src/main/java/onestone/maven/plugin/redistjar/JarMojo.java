/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package onestone.maven.plugin.redistjar;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Goal to redistribute a pre-build jar file
 *
 * @version $Id: $Id
 */
@Mojo(name = "jar",
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresProject = true,
        threadSafe = true)
public class JarMojo
        extends AbstractMojo {

  /**
   * Pre-build JAR file that should be used as the main artifact.
   */
  @Parameter(required = true)
  private File jarFile;

  /**
   * Directory containing the generated JAR.
   */
  @Parameter(defaultValue = "${project.build.directory}", required = true)
  private File outputDirectory;

  /**
   * Name of the generated JAR.
   */
  @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
  private String finalName;

  /**
   * Classifier to add to the artifact generated. If given, the artifact will be
   * attached as a supplemental artifact. If not given this will create the main
   * artifact which is the default behavior. If you try to do that a second time
   * without using a classifier the build will fail.
   */
  @Parameter
  private String classifier;

  /**
   * The {@link {MavenProject}.
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  /**
   *
   */
  @Component
  private MavenProjectHelper projectHelper;

  /**
   * {@inheritDoc}
   *
   * @return a {@link java.lang.String} object.
   */
  protected String getClassifier() {
    return classifier;
  }

  /**
   * {@inheritDoc}
   *
   * @return a {@link java.lang.String} object.
   */
  protected String getType() {
    return "jar";
  }

  /**
   * {@inheritDoc}
   *
   * @return a {@link java.io.File} object.
   */
  protected File getJarFile() {
    return jarFile;
  }

  /**
   * <p>Getter for the field <code>project</code>.</p>
   *
   * @return the {@link #project}
   */
  protected final MavenProject getProject() {
    return project;
  }

  /**
   * Execute Plugin
   *
   * @throws org.apache.maven.plugin.MojoExecutionException if any.
   * @throws org.apache.maven.plugin.MojoFailureException if any.
   */
  public void execute() throws MojoExecutionException, MojoFailureException {
    File targetFile = provideArchive();

    if (hasClassifier()) {
      projectHelper.attachArtifact(getProject(), getType(), getClassifier(), targetFile);
    } else {
      if (projectHasAlreadySetAnArtifact()) {
        throw new MojoExecutionException("You have to use a classifier "
                + "to attach supplemental artifacts to the project instead of replacing them.");
      }
      getProject().getArtifact().setFile(targetFile);
    }
  }

  /**
   * Returns the Jar file to generate, based on an optional classifier.
   *
   * @param basedir the output directory
   * @param resultFinalName the name of the ear file
   * @param classifier an optional classifier
   * @return the file to generate
   */
  protected File getTargetJarFile(File basedir, String resultFinalName, String classifier) {
    if (basedir == null) {
      throw new IllegalArgumentException("basedir is not allowed to be null");
    }
    if (resultFinalName == null) {
      throw new IllegalArgumentException("finalName is not allowed to be null");
    }

    StringBuilder fileName = new StringBuilder(resultFinalName);

    if (hasClassifier()) {
      fileName.append("-").append(classifier);
    }

    fileName.append(".jar");

    return new File(basedir, fileName.toString());
  }

  /**
   * <p>provideArchive.</p>
   *
   * @return a {@link java.io.File} object.
   * @throws org.apache.maven.plugin.MojoExecutionException if any.
   */
  protected File provideArchive() throws MojoExecutionException {
    File targetFile = getTargetJarFile(outputDirectory, finalName, getClassifier());
    try {
      File dir = targetFile.getParentFile();
      if (!dir.isDirectory()) {
        Files.createDirectory(targetFile.getParentFile().toPath());
      }
      Files.copy(jarFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
      getLog().error("Error during copying from " + jarFile + " to " + targetFile);
      throw new MojoExecutionException("Error copying JAR from ", ex);
    }
    return targetFile;
  }

  private boolean projectHasAlreadySetAnArtifact() {
    if (getProject().getArtifact().getFile() != null) {
      return getProject().getArtifact().getFile().isFile();
    } else {
      return false;
    }
  }

  /**
   * <p>hasClassifier.</p>
   *
   * @return true in case where the classifier is not {@code null} and contains
   * something else than white spaces.
   */
  protected boolean hasClassifier() {
    boolean result = false;
    if (getClassifier() != null && getClassifier().trim().length() > 0) {
      result = true;
    }

    return result;
  }
}
