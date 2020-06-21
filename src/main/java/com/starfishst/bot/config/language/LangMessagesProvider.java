package com.starfishst.bot.config.language;

import com.starfishst.bot.Main;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.messages.MessagesProvider;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.core.utils.time.Time;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/**
 * This provider is needed by the command framework for customization. In case messages are too long
 * errors will be thrown in startup
 */
public class LangMessagesProvider implements MessagesProvider {

  @Override
  public @NotNull String invalidLong(@NotNull String s, @NotNull CommandContext commandContext) {
    return Lang.get("INVALID_LONG", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidInteger(@NotNull String s, @NotNull CommandContext commandContext) {
    return Lang.get("INVALID_INTEGER", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidDouble(@NotNull String s, @NotNull CommandContext commandContext) {
    return Lang.get("INVALID_DOUBLE", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidBoolean(@NotNull String s, @NotNull CommandContext commandContext) {
    return Lang.get("INVALID_BOOLEAN", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidTime(@NotNull String s, @NotNull CommandContext commandContext) {
    return Lang.get("INVALID_TIME", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String commandNotFound(@NotNull String s) {
    HashMap<String, String> placeHolders = new HashMap<>();
    placeHolders.put("command", s);
    return Lang.get("COMMAND_NOT_FOUND", placeHolders);
  }

  @Override
  public @NotNull String footer() {
    return Lang.get("FOOTER");
  }

  @Override
  public @NotNull String getTitle(@NotNull ResultType resultType) {
    switch (resultType) {
      case PERMISSION:
        return Lang.get("TITLE_PERMISSION");
      case GENERIC:
        return Lang.get("TITLE_GENERIC");
      case USAGE:
        return Lang.get("TITLE_USAGE");
      case ERROR:
        return Lang.get("TITLE_ERROR");
      case UNKNOWN:
        return Lang.get("TITLE_UNKNOWN");
      default:
        throw new IllegalArgumentException(resultType + " this is not reachable");
    }
  }

  @Override
  public @NotNull String response(@NotNull String s, @NotNull String s1) {
    HashMap<String, String> placeHolders = new HashMap<>();
    placeHolders.put("type", s);
    placeHolders.put("message", s1);
    return Lang.get("RESPONSE", placeHolders);
  }

  @Override
  public @NotNull String notAllowed() {
    return Lang.get("NO_PERMISSION");
  }

  @Override
  public @NotNull String guildOnly() {
    return Lang.get("GUILD_ONLY");
  }

  @Override
  public @NotNull String missingArgument(@NotNull String s, @NotNull String s1, int i) {
    HashMap<String, String> placeHolders = new HashMap<>();
    placeHolders.put("name", s);
    placeHolders.put("description", s1);
    placeHolders.put("position", String.valueOf(i));
    return Lang.get("MISSING_ARGUMENT", placeHolders);
  }

  @Override
  public @NotNull String invalidNumber(@NotNull String s, @NotNull CommandContext commandContext) {
    return Lang.get("INVALID_NUMBER", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String emptyDouble(@NotNull CommandContext commandContext) {
    return Lang.get("INVALID_DOUBLE_EMPTY");
  }

  @Override
  public @NotNull String thumbnailUrl() {
    String thumbnail = Lang.get("THUMBNAIL");
    if (thumbnail.isEmpty() || thumbnail.equalsIgnoreCase("bot")) {
      String avatarUrl = Main.getJda().getSelfUser().getAvatarUrl();
      thumbnail = avatarUrl == null ? "" : avatarUrl;
    } else if (thumbnail.equalsIgnoreCase("none")) {
      thumbnail = "";
    }
    return thumbnail;
  }

  @Override
  public @NotNull String cooldown(@NotNull Time time) {
    return Lang.get("ON_COOLDOWN", Maps.singleton("time", time.toEffectiveString()));
  }

  @Override
  public @NotNull String invalidUser(@NotNull String s, @NotNull CommandContext commandContext) {
    return Lang.get("INVALID_USER", Maps.singleton("string", s));
  }

  @NotNull
  @Override
  public String invalidMember(@NotNull String s, @NotNull CommandContext commandContext) {
    return Lang.get("INVALID_MEMBER", Maps.singleton("string", s));
  }

  @NotNull
  @Override
  public String invalidRole(@NotNull String s, @NotNull CommandContext commandContext) {
    return Lang.get("INVALID_ROLE", Maps.singleton("string", s));
  }

  @NotNull
  @Override
  public String invalidTextChannel(String s, CommandContext commandContext) {
    return Lang.get("INVALID_TEXT_CHANNEL", Maps.singleton("string", s));
  }
}
