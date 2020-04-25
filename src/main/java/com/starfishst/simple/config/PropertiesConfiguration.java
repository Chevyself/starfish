package com.starfishst.simple.config;

import com.starfishst.simple.files.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/** Use a properties file as configuration */
public class PropertiesConfiguration {

  /** The name of the file */
  @NotNull private final String fileName;
  /** The file of the configuration */
  @NotNull private final File file;
  /** The properties instance to set and get values */
  @NotNull private final Properties properties;

  /**
   * Create properties configuration getting it from a resource
   *
   * @param name the name of the properties file
   * @param copyDefaults do we copy the missing keys?
   * @throws IOException in case anything goes working
   */
  public PropertiesConfiguration(@NotNull String name, boolean copyDefaults) throws IOException {
    fileName = name;
    file = FileUtils.getFileOrResource(name);
    properties = new Properties();
    properties.load(new FileInputStream(file));

    if (copyDefaults) {
      copyDefaults();
    }
  }

  /**
   * Get the properties
   *
   * @return the properties
   */
  @NotNull
  public Properties getProperties() {
    return properties;
  }

  /**
   * This copies the missing keys from the resource
   *
   * @throws IOException in case anything goes wrong
   */
  private void copyDefaults() throws IOException {
    final Properties properties = new Properties();
    properties.load(FileUtils.getResource(this.fileName));
    properties
        .stringPropertyNames()
        .forEach(
            key -> {
              if (this.properties.get(key) == null) {
                this.properties.setProperty(key, properties.getProperty(key));
              }
            });
    save();
  }

  /**
   * Saves the file
   *
   * @throws IOException in case anything goes wrong
   */
  public void save() throws IOException {
    save(null);
  }

  /**
   * Save the file but you can also add a comment
   *
   * @param comments the comment to add
   * @throws IOException in case something goes wrong
   */
  public void save(@Nullable String comments) throws IOException {
    properties.store(new FileOutputStream(file), comments);
  }
}
