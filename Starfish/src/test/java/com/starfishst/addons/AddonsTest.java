package com.starfishst.addons;

import com.starfishst.api.addons.AddonInformation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import me.googas.commons.CoreFiles;
import me.googas.commons.gson.GsonProvider;

public class AddonsTest {

  public static void main(String[] args)
      throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
          InvocationTargetException, InstantiationException {
    File file = new File(CoreFiles.validatePath(CoreFiles.currentDirectory() + "/addons"));
    System.out.println(file.exists());
    System.out.println(file.isDirectory());
    if (!file.exists()) {
      file.mkdir();
    }
    File[] files = file.listFiles();
    if (files != null) {
      for (File addon : files) {
        if (addon.getName().endsWith(".jar")) {
          System.out.println(addon.getName());
          URLClassLoader classLoader =
              new URLClassLoader(
                  new URL[] {addon.toURI().toURL()}, AddonsTest.class.getClassLoader());
          InputStream resource = classLoader.getResourceAsStream("addon.json");
          if (resource != null) {
            InputStreamReader reader = new InputStreamReader(resource);
            AddonInformation info = GsonProvider.GSON.fromJson(reader, AddonInformation.class);
            Class<?> aClass = Class.forName(info.getMain(), true, classLoader);
            Method onEnable = aClass.getDeclaredMethod("onEnable");
            Object o = aClass.getConstructors()[0].newInstance();
            onEnable.invoke(o);
            reader.close();
          }
        }
      }
    }
  }
}
