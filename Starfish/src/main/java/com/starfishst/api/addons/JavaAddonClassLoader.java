package com.starfishst.api.addons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import lombok.NonNull;
import me.googas.commons.gson.GsonProvider;

/** The class loader for addons */
public class JavaAddonClassLoader extends URLClassLoader {

  /** The file where the addon is being loaded */
  @NonNull private final File addonFile;

  /**
   * Create the class loader for addons
   *
   * @param addonFile the jar file of the addon
   * @param parent the parent class loader for the addon
   * @throws MalformedURLException in case the jar file is invalid
   */
  public JavaAddonClassLoader(@NonNull File addonFile, @NonNull ClassLoader parent)
      throws MalformedURLException {
    super(new URL[] {addonFile.toURI().toURL()}, parent);
    this.addonFile = addonFile;
  }

  /**
   * Get the addon information
   *
   * @return the addon information
   * @throws IOException if the reader could not be closed or {@link FileNotFoundException} if the
   *     addon does not include the 'addon.json'
   */
  @NonNull
  public JavaAddonInformation getAddonInfo() throws IOException {
    InputStream resourceAsStream = this.getResourceAsStream("addon.json");
    if (resourceAsStream != null) {
      InputStreamReader reader = new InputStreamReader(resourceAsStream);
      JavaAddonInformation info = GsonProvider.GSON.fromJson(reader, JavaAddonInformation.class);
      reader.close();
      return info;
    } else {
      throw new FileNotFoundException(
          "The resource 'addon.json' does not exist in " + this.addonFile.getName());
    }
  }
}
