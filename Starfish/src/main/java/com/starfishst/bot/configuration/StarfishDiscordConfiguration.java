package com.starfishst.bot.configuration;

import com.starfishst.api.configuration.DiscordConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;

/** An implementation for {@link DiscordConfiguration} */
public class StarfishDiscordConfiguration implements DiscordConfiguration {

  /** The roles that the bot can use */
  @NonNull private final Map<String, List<Long>> roles;
  /** The categories that the bot can use */
  @NonNull private final Map<String, Long> categories;
  /** The channels that the bot can use */
  @NonNull private final Map<String, Long> channels;
  /** The guild where the bot is working */
  private long guild;

  /**
   * Create the discord configuration
   *
   * @param guild the guild where the bot will work
   * @param roles the roles that the bot can use
   * @param categories the categories that the bot can use
   * @param channels the channels that the bot can use
   */
  public StarfishDiscordConfiguration(
      long guild,
      @NonNull Map<String, List<Long>> roles,
      @NonNull Map<String, Long> categories,
      @NonNull Map<String, Long> channels) {
    this.guild = guild;
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

  @Override
  public @NonNull Map<String, List<Long>> getRoles() {
    return this.roles;
  }

  @Override
  public @NonNull Map<String, Long> getCategories() {
    return this.categories;
  }

  @Override
  public @NonNull Map<String, Long> getChannels() {
    return this.channels;
  }

  @Override
  public void setGuild(Guild guild) {
    this.guild = guild == null ? -1 : guild.getIdLong();
  }

  /**
   * Get the id of the guild where the bot is working
   *
   * @return the id of guild where the bot is working
   */
  @Override
  public long getGuildId() {
    return this.guild;
  }
}
