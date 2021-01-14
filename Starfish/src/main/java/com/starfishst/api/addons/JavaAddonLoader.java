package com.starfishst.api.addons;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import lombok.CustomLog;
import lombok.NonNull;

@CustomLog
public class JavaAddonLoader implements AddonLoader {

  /** The directory in which this loader will be working */
  @NonNull private final File directory;
  /** The class loader to use */
  @NonNull private final ClassLoader parentLoader;
  /** The list of loaded addons */
  @NonNull private final List<Addon> loaded;

  /**
   * Create the addon loader
   *
   * @param directory the directory in which it will work
   * @throws IllegalArgumentException if the directory file is not an actual directory
   * @throws IOException if the directory could not be created
   */
  public JavaAddonLoader(@NonNull File directory) throws IOException {
    if (!directory.exists() && !directory.mkdir())
      throw new IOException(directory.getName() + " could not be created!");
    if (!directory.isDirectory())
      throw new IllegalArgumentException(directory + " is not a directory!");
    this.directory = directory;
    this.parentLoader = this.getClass().getClassLoader();
    this.loaded = new ArrayList<>();
  }

  /**
   * Initialize the addon that is supposed too be inside of the parameter file
   *
   * @param file the file which must be a jar containing the addon
   * @return the addon if it was initialized null otherwise
   */
  private JavaAddon initializeAddon(@NonNull File file) {
    log.info(String.format("Initializing addon in %s", file.getName()));
    try {
      JavaAddonClassLoader addonLoader = new JavaAddonClassLoader(file, parentLoader);
      JavaAddonInformation info = addonLoader.getAddonInfo();
      Class<?> clazz = Class.forName(info.getMain(), true, addonLoader);
      Object instance = clazz.getConstructor(JavaAddonInformation.class).newInstance(info);
      if (instance instanceof JavaAddon) return (JavaAddon) instance;
    } catch (IllegalAccessException
        | InstantiationException
        | InvocationTargetException
        | ClassNotFoundException
        | NoSuchMethodException
        | IOException e) {
      log.log(Level.SEVERE, e, null);
    }
    return null;
  }

  /**
   * Get the jar files inside {@link #directory}
   *
   * @return the jar files inside a list
   */
  @NonNull
  private List<File> getJars() {
    List<File> jars = new ArrayList<>();
    File[] files = directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.getName().endsWith(".jar")) jars.add(file);
      }
    }
    return jars;
  }

  @NonNull
  public Collection<Addon> load() {
    List<Addon> loaded = new ArrayList<>();
    for (File file : this.getJars()) {
      JavaAddon addon = initializeAddon(file);
      if (addon != null) {
        try {
          addon.onEnable();
        } catch (Throwable e) {
          log.log(Level.SEVERE, e, null);
        }
      } else {
        log.info("Addon in " + file.getName() + " could not be loaded");
      }
    }
    return loaded;
  }

  @NonNull
  public List<Addon> unload() {
    List<Addon> unloaded = new ArrayList<>();
    for (Addon addon : this.loaded) {
      unloaded.add(this.unload(addon));
    }
    loaded.clear();
    return unloaded;
  }

  @Override
  @NonNull
  public List<Addon> getLoaded() {
    return loaded;
  }
}
