package com.starfishst.bot.configuration;

import com.starfishst.api.configuration.DiscordConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for {@link DiscordConfiguration} */
public class StarfishDiscordConfiguration implements DiscordConfiguration {

  /** The roles that the bot can use */
  @NotNull private final Map<String, List<Long>> roles;
  /** The categories that the bot can use */
  @NotNull private final Map<String, Long> categories;
  /** The channels that the bot can use */
  @NotNull private final Map<String, Long> channels;
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
      @NotNull Map<String, List<Long>> roles,
      @NotNull Map<String, Long> categories,
      @NotNull Map<String, Long> channels) {
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
  @NotNull
  public static StarfishDiscordConfiguration fallback() {
    return new StarfishDiscordConfiguration(-1, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  @Override
  public @NotNull Map<String, List<Long>> getRoles() {
    return this.roles;
  }

  @Override
  public @NotNull Map<String, Long> getCategories() {
    return this.categories;
  }

  @Override
  public @NotNull Map<String, Long> getChannels() {
    return this.channels;
  }

  @Override
  public void setGuild(@Nullable Guild guild) {
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
