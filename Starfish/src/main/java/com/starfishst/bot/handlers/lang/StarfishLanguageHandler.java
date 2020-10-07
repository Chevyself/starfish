package com.starfishst.bot.handlers.lang;

import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.utility.console.Console;
import com.starfishst.bot.handlers.StarfishHandler;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.messages.MessagesProvider;
import com.starfishst.jda.result.ResultType;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.CoreFiles;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Handles the language for guido messages */
public class StarfishLanguageHandler implements MessagesProvider, StarfishHandler {

  /** The files that this handler is using */
  @NotNull private final Set<StarfishLocaleFile> files = new HashSet<>();

  /** The loader to get the localization */
  @NotNull private DataLoader dataLoader;

  /**
   * Create the guido localization handler
   *
   * @param dataLoader the file loader to get the user data
   */
  public StarfishLanguageHandler(@NotNull DataLoader dataLoader) {
    this.dataLoader = dataLoader;
  }

  /**
   * Loads the locale files queried
   *
   * @param toLoad the locale files to load
   */
  public void load(String... toLoad) {
    for (String lang : toLoad) {
      try {
        files.add(
            new StarfishLocaleFile(
                CoreFiles.getFileOrResource(
                    CoreFiles.currentDirectory() + "/assets/lang/" + lang + ".properties",
                    CoreFiles.getResource("lang/" + lang + ".properties"))));
      } catch (IOException e) {
        Console.exception(e, "IOException: File for the language " + lang + " could not be gotten");
      }
    }
  }

  /**
   * Get the file for certain lang
   *
   * @param lang the lang to get the file for
   * @return the file from the lang
   */
  @NotNull
  public StarfishLocaleFile getFile(@NotNull String lang) {
    for (StarfishLocaleFile file : this.files) {
      if (file.getLang().equalsIgnoreCase(lang)) {
        return file;
      }
    }
    return this.getFile("en");
  }

  /**
   * Get the file from a unicode
   *
   * @param unicode the unicode to get the file from
   * @return the file that posses the unicode
   * @throws IllegalArgumentException if the unicode does not match any file
   */
  @NotNull
  public StarfishLocaleFile getFileFromUnicode(@NotNull String unicode) {
    for (StarfishLocaleFile file : this.files) {
      if (file.getUnicode().equalsIgnoreCase(unicode)) {
        return file;
      }
    }
    throw new IllegalArgumentException(unicode + " is not a valid unicode");
  }

  /**
   * Get the file for certain context. This will get the lang from the context and then the file
   *
   * @param context the context to get the lang and the file
   * @return the file or "en" if not found
   */
  @NotNull
  public StarfishLocaleFile getFile(@Nullable CommandContext context) {
    StarfishLocaleFile file;
    if (context == null) {
      file = this.getFile("en");
    } else {
      file = this.getFile(this.getLang(context));
    }
    return file;
  }

  /**
   * Get the language for certain context. This will get the user and get its data to load and get
   * its locale
   *
   * @param context the context to get the language form
   * @return the locale for the user
   */
  @NotNull
  public String getLang(@NotNull CommandContext context) {
    return this.dataLoader.getStarfishUser(context.getSender().getIdLong()).getLang();
  }

  /** Stops the language handler */
  public void stop() {
    for (StarfishLocaleFile file : this.files) {
      file.save();
    }
  }

  /**
   * Set the data loader for the language handler
   *
   * @param dataLoader the new data loader
   */
  public void setDataLoader(@NotNull DataLoader dataLoader) {
    this.dataLoader = dataLoader;
  }

  /**
   * Get the files that are loaded
   *
   * @return the files that are loaded
   */
  @NotNull
  public Set<StarfishLocaleFile> getFiles() {
    return files;
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "language-handler";
  }

  @Override
  public @NotNull String invalidLong(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidInteger(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidDouble(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.double", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidBoolean(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.boolean", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidTime(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.time", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String missingArgument(
      @NotNull String s, @NotNull String s1, int i, CommandContext context) {
    return this.getFile(context)
        .get(
            "missing-argument",
            Maps.builder("name", this.getFile(context).get(s))
                .append("description", this.getFile(context).get(s1))
                .append("position", String.valueOf(i)));
  }

  @Override
  public @NotNull String invalidNumber(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String emptyDouble(@NotNull CommandContext context) {
    return this.getFile(context).get("invalid.double-empty");
  }

  @Override
  public @NotNull String commandNotFound(
      @NotNull String s, @NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("command-not-found", Maps.builder("name", s));
  }

  @Override
  public @NotNull String footer(@Nullable CommandContext commandContext) {
    return this.getFile(commandContext).get("footer");
  }

  @Override
  public @NotNull String getTitle(
      @NotNull ResultType resultType, @Nullable CommandContext commandContext) {
    StarfishLocaleFile file = this.getFile(commandContext);
    switch (resultType) {
      case UNKNOWN:
        return file.get("title.unknown");
      case PERMISSION:
        return file.get("title.permission");
      case GENERIC:
        return file.get("title.generic");
      case USAGE:
        return file.get("title.usage");
      case ERROR:
        return file.get("title.error");
    }
    throw new IllegalArgumentException(resultType + " is not a valid result");
  }

  @Override
  public @NotNull String response(
      @NotNull String s, @NotNull String s1, @Nullable CommandContext commandContext) {
    return this.getFile(commandContext)
        .get("response", Maps.builder("title", s).append("description", s1));
  }

  @Override
  public @NotNull String notAllowed(@NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("not-allowed");
  }

  @Override
  public @NotNull String guildOnly(@NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("guild-only");
  }

  @Override
  public @NotNull String thumbnailUrl(@Nullable CommandContext commandContext) {
    return this.getFile(commandContext).get("thumbnail-url");
  }

  @Override
  public @NotNull String cooldown(@NotNull Time time, @Nullable CommandContext commandContext) {
    return this.getFile(commandContext)
        .get("cooldown", Maps.singleton("left", time.toEffectiveString()));
  }

  @Override
  public @NotNull String invalidUser(@NotNull String s, @NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.user", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidMember(@NotNull String s, @NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.member", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidRole(@NotNull String s, @NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.role", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidTextChannel(String s, CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.channel", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String missingStrings(
      @NotNull String s,
      @NotNull String s1,
      int i,
      int i1,
      int i2,
      @NotNull CommandContext context) {
    return this.getFile(context)
        .get(
            "invalid.strings",
            Maps.builder("name", s)
                .append("description", s1)
                .append("position", String.valueOf(i))
                .append("min", String.valueOf(i1))
                .append("missing", String.valueOf(i2)));
  }
}
