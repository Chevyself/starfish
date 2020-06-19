package com.starfishst.bot.addons;

import com.starfishst.simple.gson.GsonProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.jetbrains.annotations.NotNull;

/** The class loader for addons */
public class AddonClassLoader extends URLClassLoader {

  /**
   * Create the class loader for addons
   *
   * @param addonFile the jar file of the addon
   * @param parent the parent class loader for the addon
   * @throws MalformedURLException in case the jar file is invalid
   */
  public AddonClassLoader(@NotNull File addonFile, @NotNull ClassLoader parent)
      throws MalformedURLException {
    super(new URL[] {addonFile.toURI().toURL()}, parent);
  }

  /**
   * Get the addon information
   *
   * @return the addon information
   * @throws IOException if the reader could not be closed
   */
  @NotNull
  public AddonInformation getInfo() throws IOException {
    InputStream resourceAsStream = getResourceAsStream("addon.json");
    InputStreamReader reader = new InputStreamReader(resourceAsStream);
    AddonInformation info = GsonProvider.GSON.fromJson(reader, AddonInformation.class);
    reader.close();
    return info;
  }
}
