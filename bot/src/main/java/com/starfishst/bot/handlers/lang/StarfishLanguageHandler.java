package com.starfishst.bot.handlers.lang;

import com.starfishst.api.Fallback;
import com.starfishst.api.StarfishFiles;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.loader.LanguageHandler;
import java.util.Collection;
import java.util.HashSet;
import lombok.NonNull;

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
      this.files.add(new StarfishLocaleFile(StarfishFiles.Assets.Lang.getLangFile(lang)).load());
    }
    return this;
  }

  @Override
  public @NonNull Collection<LocaleFile> getFiles() {
    return this.files;
  }
}
