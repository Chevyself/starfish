package com.starfishst.bot.configuration;

import com.google.gson.annotations.SerializedName;
import com.starfishst.api.configuration.DiscordConfiguration;
import com.starfishst.bot.utility.Mongo;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commons.CoreFiles;

/** An implementation for {@link DiscordConfiguration} */
public class StarfishDiscordConfiguration implements DiscordConfiguration {

  @NonNull @Getter private final Map<String, List<Long>> roles;
  @NonNull @Getter private final Map<String, Long> categories;
  @NonNull @Getter private final Map<String, Long> channels;

  @SerializedName("guild")
  @Setter
  @Getter
  private long guildId;

  /**
   * Create the discord configuration
   *
   * @param guildId the guild where the bot will work
   * @param roles the roles that the bot can use
   * @param categories the categories that the bot can use
   * @param channels the channels that the bot can use
   */
  public StarfishDiscordConfiguration(
      long guildId,
      @NonNull Map<String, List<Long>> roles,
      @NonNull Map<String, Long> categories,
      @NonNull Map<String, Long> channels) {
    this.guildId = guildId;
    this.roles = roles;
    this.categories = categories;
    this.channels = channels;
  }

  /** This constructor is used for gson */
  @Deprecated
  public StarfishDiscordConfiguration() {
    this(-1, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  /**
   * Create a discord configuration with a constructor with no parameters
   *
   * @return a discord configuration for fallback
   */
  @NonNull
  public static StarfishDiscordConfiguration fallback() {
    return new StarfishDiscordConfiguration(-1, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  @NonNull
  public static StarfishDiscordConfiguration init() {
    FileReader reader = null;
    StarfishDiscordConfiguration configuration = StarfishDiscordConfiguration.fallback();
    try {
      reader =
          new FileReader(CoreFiles.getFileOrResource(CoreFiles.currentDirectory(), "discord.json"));
      configuration = Mongo.GSON.fromJson(reader, StarfishDiscordConfiguration.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (reader != null) {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return configuration;
  }
}
