package com.starfishst.api.loader;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.lang.LocaleFile;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.messages.MessagesProvider;
import me.googas.commands.jda.result.ResultType;
import me.googas.starbox.time.Time;

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
    return this.getFiles().stream()
        .filter(file -> file.getLang().equalsIgnoreCase(lang))
        .findFirst()
        .orElseGet(() -> this.getFile("en"));
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
    return this.getFiles().stream()
        .filter(file -> file.getUnicode().equalsIgnoreCase(unicode))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(unicode + " is not a valid unicode"));
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
    return Starfish.getLoader()
        .getSubloader(UserSubloader.class)
        .getStarfishUser(context.getSender().getIdLong())
        .getLang();
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
    return this.getFile(context).get("invalid.number", Collections.singletonMap("string", s));
  }

  @Override
  default @NonNull String invalidInteger(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Collections.singletonMap("string", s));
  }

  @Override
  default @NonNull String invalidDouble(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.double", Collections.singletonMap("string", s));
  }

  @Override
  default @NonNull String invalidBoolean(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.boolean", Collections.singletonMap("string", s));
  }

  @Override
  @NonNull
  default String cooldown(long timeLeft, CommandContext context) {
    return this.getFile(context)
        .get(
            "cooldown",
            Collections.singletonMap("cooldown", Time.ofMillis(timeLeft, true).toString()));
  }

  @Override
  default @NonNull String invalidTime(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.time", Collections.singletonMap("string", s));
  }

  @Override
  default @NonNull String missingArgument(
      @NonNull String name, @NonNull String description, int position, CommandContext context) {
    LocaleFile file = this.getFile(context);
    Map<String, String> placeholders = new HashMap<>();
    placeholders.put("name", file.get(name));
    placeholders.put("description", file.get(description));
    placeholders.put("position", String.valueOf(position));
    return file.get("missing-argument", placeholders);
  }

  @Override
  default @NonNull String commandNotFound(
      @NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext)
        .get("command-not-found", Collections.singletonMap("name", s));
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
      @NonNull String title, @NonNull String description, CommandContext commandContext) {
    Map<String, String> placeholders = new HashMap<>();
    placeholders.put("title", title);
    placeholders.put("description", description);
    return this.getFile(commandContext).get("response", placeholders);
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
  default @NonNull String invalidUser(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.user", Collections.singletonMap("string", s));
  }

  @Override
  default @NonNull String invalidMember(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext)
        .get("invalid.member", Collections.singletonMap("string", s));
  }

  @Override
  default @NonNull String invalidRole(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.role", Collections.singletonMap("string", s));
  }

  @Override
  default @NonNull String invalidTextChannel(String s, CommandContext commandContext) {
    return this.getFile(commandContext)
        .get("invalid.channel", Collections.singletonMap("string", s));
  }

  @Override
  default @NonNull String missingStrings(
      @NonNull String name,
      @NonNull String description,
      int position,
      int min,
      int missing,
      @NonNull CommandContext context) {
    Map<String, String> placeholders = new HashMap<>();
    placeholders.put("name", name);
    placeholders.put("description", description);
    placeholders.put("position", String.valueOf(position));
    placeholders.put("min", String.valueOf(min));
    placeholders.put("missing", String.valueOf(missing));
    return this.getFile(context).get("invalid.strings", placeholders);
  }
}
