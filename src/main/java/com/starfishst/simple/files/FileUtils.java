package com.starfishst.simple.files;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/** Many file utils */
public class FileUtils {

  /** The class to load files */
  @NotNull private static final ClassLoader LOADER = FileUtils.class.getClassLoader();

  /**
   * Get a file using a path
   *
   * @param url the path to the file
   * @return the file if fond
   * @throws FileNotFoundException if the file is not found
   */
  @NotNull
  public static File getFile(String url) throws FileNotFoundException {
    final File file = new File(url);
    if (file.exists()) {
      return file;
    } else {
      throw new FileNotFoundException();
    }
  }

  /**
   * Gets the current working directory
   *
   * @return the current working directory
   */
  @NotNull
  public static String getCurrentDirectory() {
    return System.getProperty("user.dir");
  }

  /**
   * Gets a resource from the JAR file
   *
   * @param name the name of the resource
   * @return the resource
   */
  @NotNull
  public static InputStream getResource(@NotNull String name) {
    return Objects.requireNonNull(
        LOADER.getResourceAsStream(name), "The resource " + name + " does not exist.");
  }

  /**
   * Gets the file from the same place as the jar or gets it from the resources and copies it
   *
   * @param name the name of the file
   * @return the file if found
   * @throws IOException in case IO went wrong
   */
  @NotNull
  public static File getFileOrResource(@NotNull String name) throws IOException {
    final File file = new File(name);
    if (file.exists()) {
      return file;
    } else {
      return copyResource(name);
    }
  }

  /**
   * Copies a resource to a file
   *
   * @param name the name of the resource
   * @return the resource as file
   * @throws IOException in case IO went wrong
   */
  public static File copyResource(@NotNull String name) throws IOException {
    File file = new File(name);
    Files.copy(getResource(name), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    return file;
  }

  /**
   * Get a file reader using a file
   *
   * @param file the file to get the reader from
   * @return the reader
   * @throws FileNotFoundException if the file is not null
   */
  @NotNull
  public static Reader getReader(@NotNull File file) throws FileNotFoundException {
    return new BufferedReader(new FileReader(file));
  }
}
