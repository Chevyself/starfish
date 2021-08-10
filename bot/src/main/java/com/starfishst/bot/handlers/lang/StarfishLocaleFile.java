package com.starfishst.bot.handlers.lang;

import com.starfishst.api.Starfish;
import com.starfishst.api.StarfishFiles;
import com.starfishst.api.lang.LocaleFile;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import lombok.Getter;
import lombok.NonNull;
import me.googas.io.StarboxFile;
import me.googas.io.context.PropertiesContext;

/** The locale file for the guido bot. It is loaded using {@link Properties} */
public class StarfishLocaleFile implements LocaleFile {

  @NonNull @Getter private final StarboxFile file;
  /** The properties used to get the strings */
  @NonNull private Properties properties;

  /**
   * Create the guido locale file
   *
   * @param file the properties file to get the properties
   * @throws IOException in case that the properties file cannot be read
   */
  public StarfishLocaleFile(@NonNull StarboxFile file) {
    this.file = file;
    this.properties = new Properties();
  }

  public StarfishLocaleFile load() {
    PropertiesContext context = StarfishFiles.Contexts.PROPERTIES;
    String lang = this.getLang();
    Properties resource =
        context
            .read(StarfishFiles.Resources.getLangResource(lang))
            .handle(
                e -> {
                  Starfish.getFallback()
                      .process(e, "Could not read language resource: " + lang + ".properties");
                })
            .provide()
            .orElseGet(Properties::new);
    this.properties =
        this.file
            .read(context)
            .handle(
                e -> {
                  Starfish.getFallback()
                      .process(e, "Could not read language resource: " + lang + ".properties");
                })
            .provide()
            .orElse(resource);
    resource.forEach(
        (key, value) -> {
          if (!this.properties.containsKey(key)) {
            this.properties.put(key, value);
          }
        });
    return this;
  }

  /**
   * Get the unicode that differentiates this language
   *
   * @return the unicode to differentiate this language
   */
  public @NonNull String getUnicode() {
    return this.getRaw("unicode")
        .orElseThrow(() -> new NullPointerException(this + " has a null unicode property!"));
  }

  @Override
  public void setLang(@NonNull String lang) {
    throw new UnsupportedOperationException("You cannot change the language from locale files");
  }

  @Override
  public @NonNull String getLang() {
    return this.getRaw("lang")
        .orElseThrow(() -> new NullPointerException(this + " has a null lang property!"));
  }

  @Override
  public StarfishLocaleFile save() {
    StarfishFiles.Contexts.PROPERTIES.write(this.file, this.properties, "");
    return this;
  }

  @Override
  public Optional<String> getRaw(@NonNull String path) {
    return Optional.ofNullable(this.properties.getProperty(path));
  }
}
