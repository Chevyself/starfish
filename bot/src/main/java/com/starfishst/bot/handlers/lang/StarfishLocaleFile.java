package com.starfishst.bot.handlers.lang;

import com.starfishst.api.Starfish;
import com.starfishst.api.lang.LocaleFile;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.CoreFiles;
import me.googas.commons.Validate;

/** The locale file for the guido bot. It is loaded using {@link Properties} */
public class StarfishLocaleFile implements LocaleFile {

  @NonNull @Getter private final File file;
  /** The properties used to get the strings */
  @NonNull private final Properties properties = new Properties();

  /**
   * Create the guido locale file
   *
   * @param file the properties file to get the properties
   * @throws IOException in case that the properties file cannot be read
   */
  public StarfishLocaleFile(@NonNull File file) throws IOException {
    this.file = file;
    FileReader reader = new FileReader(this.file);
    this.properties.load(reader);
    reader.close();
    this.copyDefaults();
  }

  /** Copies the default (missing keys) to the file. */
  public void copyDefaults() {
    try {
      Properties defaults = new Properties();
      defaults.load(CoreFiles.getResource("lang/" + this.getLang() + ".properties"));
      for (Object key : defaults.keySet()) {
        if (this.properties.get(key) == null) {
          this.properties.put(key, defaults.get(key));
        }
      }
    } catch (IOException e) {
      Starfish.getFallback()
          .process(e, "IOException: The defaults for " + this + " could not be saved");
    }
  }

  /**
   * Get the unicode that differentiates this language
   *
   * @return the unicode to differentiate this language
   */
  public @NonNull String getUnicode() {
    return Validate.notNull(this.getRaw("unicode"), this + " has a null unicode property");
  }

  @Override
  public void setLang(@NonNull String lang) {
    throw new UnsupportedOperationException("You cannot change the language from locale files");
  }

  @Override
  public @NonNull String getLang() {
    return Validate.notNull(this.getRaw("lang"), this + " has a null lang property!");
  }

  @Override
  public void save() {
    try {
      FileWriter writer = new FileWriter(this.file);
        this.properties.store(writer, "No comments");
      writer.close();
    } catch (IOException e) {
      Starfish.getFallback()
          .process(e, "IOException: Lang file from " + this + " could not be saved");
    }
  }

  @Override
  public String getRaw(@NonNull String path) {
    return this.properties.getProperty(path);
  }
}
