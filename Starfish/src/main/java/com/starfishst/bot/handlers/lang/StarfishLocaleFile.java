package com.starfishst.bot.handlers.lang;

import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.utility.console.Console;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import me.googas.commons.CoreFiles;
import me.googas.commons.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The locale file for the guido bot. It is loaded using {@link Properties} */
public class StarfishLocaleFile implements LocaleFile {

  /** The actual file that this is using */
  @NotNull private final File file;
  /** The properties used to get the strings */
  @NotNull private final Properties properties = new Properties();

  /**
   * Create the guido locale file
   *
   * @param file the properties file to get the properties
   * @throws IOException in case that the properties file cannot be read
   */
  public StarfishLocaleFile(@NotNull File file) throws IOException {
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
      Console.exception(e, "IOException: The defaults for " + this + " could not be saved");
    }
  }

  /**
   * Get the unicode that differentiates this language
   *
   * @return the unicode to differentiate this language
   */
  public @NotNull String getUnicode() {
    return Validate.notNull(this.getRaw("unicode"), this + " has a null unicode property");
  }

  @Override
  public void setLang(@NotNull String lang) {
    throw new UnsupportedOperationException("You cannot change the language from locale files");
  }

  @Override
  public @NotNull String getLang() {
    return Validate.notNull(this.getRaw("lang"), this + " has a null lang property!");
  }

  @Override
  public void save() {
    try {
      FileWriter writer = new FileWriter(this.file);
      properties.store(writer, "No comments");
      writer.close();
    } catch (IOException e) {
      Console.exception(e, "IOException: Lang file from " + this + " could not be saved");
    }
  }

  @Override
  public @Nullable String getRaw(@NotNull String path) {
    return this.properties.getProperty(path);
  }

  @Override
  public @NotNull File getFile() {
    return this.file;
  }
}
