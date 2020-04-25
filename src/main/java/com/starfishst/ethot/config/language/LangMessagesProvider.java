package com.starfishst.ethot.config.language;

import com.starfishst.commands.messages.MessagesProvider;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.utils.time.Time;
import com.starfishst.ethot.Main;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/**
 * This provider is needed by the command framework for customization. In case messages are too long
 * errors will be thrown in startup
 *
 * @author Chevy
 * @version 1.0.0
 */
public class LangMessagesProvider implements MessagesProvider {

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
    HashMap<String, String> placeHolders = new HashMap<>();
    placeHolders.put("time", time.toEffectiveString());
    return Lang.get("ON_COOLDOWN", placeHolders);
  }
}
