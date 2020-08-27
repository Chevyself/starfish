package com.starfishst.api.addons;

import com.starfishst.bot.util.Console;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The addon loader loads addons */
public class AddonLoader {

  /** The directory in which this loader will be working */
  @NotNull private final File directory;
  /** The class loader to use */
  @NotNull private final ClassLoader parentLoader;
  /** The list of loaded addons */
  @NotNull private final List<Addon> loaded;

  /**
   * Create the addon loader
   *
   * @param directory the directory in which it will work
   * @throws IllegalArgumentException if the directory file is not an actual directory
   * @throws IOException if the directory could not be created
   */
  public AddonLoader(@NotNull File directory) throws IOException {
    if (!directory.exists()) {
      if (!directory.mkdir()) {
        throw new IOException(directory.getName() + " could not be created!");
      }
    }
    if (!directory.isDirectory()) {
      throw new IllegalArgumentException(directory + " is not a directory!");
    }
    this.directory = directory;
    this.parentLoader = this.getClass().getClassLoader();
    this.loaded = new ArrayList<>();
  }

  /** Loads the addons */
  public void load() {
    File[] files = directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.getName().endsWith(".jar")) {
          try {
            AddonClassLoader addonLoader = new AddonClassLoader(file, parentLoader);
            AddonInformation info = addonLoader.getInfo();
            Class<?> clazz = Class.forName(info.getMain(), true, addonLoader);
            Object addonObj = clazz.getConstructor().newInstance();
            System.out.println(addonObj.getClass().getSuperclass().getName());
            if (addonObj instanceof Addon) {
              Addon addon = (Addon) addonObj;
              addon.setInformation(info);
              addon.onEnable();
              loaded.add(addon);
            } else {
              Console.severe(
                  "Failed to load "
                      + info.getName()
                      + " main class( "
                      + clazz
                      + ") does not extend "
                      + Addon.class);
            }
          } catch (IOException
              | ClassNotFoundException
              | NoSuchMethodException
              | IllegalAccessException
              | InstantiationException
              | InvocationTargetException e) {
            Console.severe(file.getName() + " could not be loaded");
            e.printStackTrace();
          }
        }
      }
    }
  }

  /** Unloads all the addons */
  public void unload() {
    loaded.forEach(Addon::onDisable);
    loaded.clear();
  }

  /** Loads all the addons */
  public void reload() {
    unload();
    load();
  }

  /**
   * Get an addon by name
   *
   * @param name the name to match
   * @return the addon if the name matches
   */
  @Nullable
  public Addon getAddonByName(@NotNull String name) {
    for (Addon addon : this.loaded) {
      if (addon.getInformation() != null && addon.getInformation().getName().equalsIgnoreCase(name)){
        return addon;
      }
    }
    return null;
  }

  /**
   * Get the list of loaded addons
   *
   * @return the list of loaded addons
   */
  @NotNull
  public List<Addon> getLoaded() {
    return loaded;
  }
}
