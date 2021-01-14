package com.starfishst.api.loader;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.messages.MessagesProvider;
import com.starfishst.jda.result.ResultType;
import java.util.Collection;
import lombok.NonNull;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;

/** This object is in charge of handling the language of the bot and the users of it */
public interface LanguageHandler extends MessagesProvider, StarfishHandler {

  /**
   * Get the file for certain lang
   *
   * @param lang the lang to get the file for
   * @return the file from the lang
   */
  @NonNull
  default LocaleFile getFile(@NonNull String lang) {
    for (LocaleFile file : this.getFiles()) {
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
  @NonNull
  default LocaleFile getFileFromUnicode(@NonNull String unicode) {
    for (LocaleFile file : this.getFiles()) {
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
  @NonNull
  default LocaleFile getFile(CommandContext context) {
    LocaleFile file;
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
  @NonNull
  default String getLang(@NonNull CommandContext context) {
    return Starfish.getLoader().getStarfishUser(context.getSender().getIdLong()).getLang();
  }

  /**
   * Get the default language of the bot
   *
   * @return the default language
   */
  @NonNull
  default LocaleFile getDefault() {
    return this.getFile(Starfish.getConfiguration().getLang());
  }

  /**
   * Get the locale files that are loaded
   *
   * @return the files that are loaded
   */
  @NonNull
  Collection<LocaleFile> getFiles();

  @Override
  default void onUnload() {
    for (LocaleFile file : this.getFiles()) {
      file.save();
    }
  }

  @Override
  default String getName() {
    return "language-handler";
  }

  @Override
  default @NonNull String invalidLong(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  default @NonNull String invalidInteger(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  default @NonNull String invalidDouble(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.double", Maps.singleton("string", s));
  }

  @Override
  default @NonNull String invalidBoolean(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.boolean", Maps.singleton("string", s));
  }

  @Override
  default @NonNull String invalidTime(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.time", Maps.singleton("string", s));
  }

  @Override
  default @NonNull String missingArgument(
      @NonNull String s, @NonNull String s1, int i, CommandContext context) {
    return this.getFile(context)
        .get(
            "missing-argument",
            Maps.builder("name", this.getFile(context).get(s))
                .append("description", this.getFile(context).get(s1))
                .append("position", String.valueOf(i)));
  }

  @Override
  default @NonNull String commandNotFound(
      @NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("command-not-found", Maps.builder("name", s));
  }

  @Override
  default @NonNull String footer(CommandContext commandContext) {
    return this.getFile(commandContext).get("footer");
  }

  @Override
  default @NonNull String getTitle(@NonNull ResultType resultType, CommandContext commandContext) {
    LocaleFile file = this.getFile(commandContext);
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
  default @NonNull String response(
      @NonNull String s, @NonNull String s1, CommandContext commandContext) {
    return this.getFile(commandContext)
        .get("response", Maps.builder("title", s).append("description", s1));
  }

  @Override
  default @NonNull String notAllowed(@NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("not-allowed");
  }

  @Override
  default @NonNull String guildOnly(@NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("guild-only");
  }

  @Override
  default @NonNull String thumbnailUrl(CommandContext commandContext) {
    return this.getFile(commandContext).get("thumbnail-url");
  }

  @Override
  default @NonNull String cooldown(@NonNull Time time, CommandContext commandContext) {
    return this.getFile(commandContext)
        .get("cooldown", Maps.singleton("left", time.toEffectiveString()));
  }

  @Override
  default @NonNull String invalidUser(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.user", Maps.singleton("string", s));
  }

  @Override
  default @NonNull String invalidMember(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.member", Maps.singleton("string", s));
  }

  @Override
  default @NonNull String invalidRole(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.role", Maps.singleton("string", s));
  }

  @Override
  default @NonNull String invalidTextChannel(String s, CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.channel", Maps.singleton("string", s));
  }

  @Override
  default @NonNull String missingStrings(
      @NonNull String s,
      @NonNull String s1,
      int i,
      int i1,
      int i2,
      @NonNull CommandContext context) {
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
