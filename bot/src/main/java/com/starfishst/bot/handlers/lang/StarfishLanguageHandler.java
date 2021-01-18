package com.starfishst.bot.handlers.lang;

import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.loader.LanguageHandler;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import lombok.NonNull;
import me.googas.commons.CoreFiles;
import me.googas.commons.fallback.Fallback;

/** Handles the language for starfish messages */
public class StarfishLanguageHandler implements LanguageHandler {

  /** The files that this handler is using */
  @NonNull private final Collection<LocaleFile> files = new HashSet<>();

  /**
   * Loads the locale files queried
   *
   * @param toLoad the locale files to load
   */
  @NonNull
  public StarfishLanguageHandler load(@NonNull Fallback fallback, String... toLoad) {
    for (String lang : toLoad) {
      try {
          this.files.add(
            new StarfishLocaleFile(
                CoreFiles.getFileOrResource(
                    CoreFiles.currentDirectory() + "/assets/lang/" + lang + ".properties",
                    CoreFiles.getResource("lang/" + lang + ".properties"))));
      } catch (IOException e) {
        fallback.process(
            e, "IOException: File for the language \" + lang + \" could not be gotten");
      }
    }
    return this;
  }

  @Override
  public @NonNull Collection<LocaleFile> getFiles() {
    return this.files;
  }
}
